import org.bytedeco.javacpp.FloatPointer
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacv.Java2DFrameConverter
import org.bytedeco.javacv.LeptonicaFrameConverter
import org.bytedeco.leptonica.PIX
import org.bytedeco.leptonica.global.lept
import org.bytedeco.tesseract.ResultIterator
import org.bytedeco.tesseract.TessBaseAPI
import org.bytedeco.tesseract.global.tesseract.*
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.min
import kotlin.system.exitProcess

object Reader {
    private val cores = min(3, Runtime.getRuntime().availableProcessors() / 2)
    private var apis: Array<TessBaseAPI>

    private const val SCALE_FACTOR = 1.0f
    private var previousScreenshot: PIX? = null
    private var lastFractDiff = 0.0f
    private var fractDiff = 0.0f

    init {
        apis = (0 until cores).map { TessBaseAPI() }.toTypedArray()
        apis.forEach {
            if (it.Init("src/main/resources", "eng") != 0) {
                System.err.println("Could not initialize Tesseract")
                exitProcess(1)
            }
        }
    }

    private val robot = Robot()

    private fun getScreenContents(): Pair<PIX, BufferedImage> {
        val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
        val capture = robot.createScreenCapture(screenRect)
        val pixScaled = imgToPix(capture)
        return Pair(pixScaled, capture)
    }

    private fun imgToPix(image: BufferedImage): PIX {
        val j2d = Java2DFrameConverter().convert(image)
        val pix = LeptonicaFrameConverter().convert(j2d)
        val pixScaled = lept.pixScale(pix, SCALE_FACTOR, SCALE_FACTOR)
        pix.deallocate()
        return pixScaled
    }

    fun fetchTargets(): Map<String, Target>? {
        val (pix, image) = getScreenContents()
        if (!areDifferent(pix, previousScreenshot)) {
            pix.deallocate()
            return null
        }

        if (previousScreenshot != null) lept.pixDestroy(previousScreenshot)
        previousScreenshot = pix
        lastFractDiff = fractDiff

        return Pattern.filterTags("").zip(parseImage(image)).toMap()
    }

    private fun parseImage(img: BufferedImage) =
        apis.mapIndexed { i, api ->
            val height = img.height / cores
            Pair(imgToPix(img.getSubimage(0, i * height, img.width, height)), api)
        }.parallelStream()
            .map { getResults(it.first, it.second) }
            .toArray()
            .mapIndexed { i, targets ->
                (targets as List<Target>).map {
                    val hAdjust = i * img.height / cores
                    it.apply { y1 += hAdjust; y2 += hAdjust }
                }.toTypedArray()
            }.toTypedArray().flatten()

    private fun areDifferent(img1: PIX, img2: PIX?) =
        if (img2 == null) true
        else {
            val fractdiff = FloatPointer(0.20f)
            val avediff = FloatPointer(0.0f)
            lept.pixGetDifferenceStats(img1, img2, 0, 1, fractdiff, avediff, 0)
            this.fractDiff = fractdiff.get()
            listOf(fractdiff, avediff).forEach { it.deallocate() }
            val relativeDiff = abs(lastFractDiff - fractDiff)
            0.0001f < relativeDiff
        }

    private fun TessBaseAPI.recognizeImage(image: PIX) {
        SetImage(image)
        val res = GetSourceYResolution()
        if (res < 70) SetSourceResolution(70)
        val monitor = TessMonitorCreate()
        val resultCode = Recognize(monitor)
        monitor.deallocate()
        if (resultCode != 0)
            throw Exception("Recognition error: $resultCode")
    }

    private fun getResults(image: PIX, api: TessBaseAPI): List<Target> {
        api.recognizeImage(image)
        val results = mutableListOf<Target>()
        val resultIt = api.GetIterator()
        if (resultIt != null) {
            while (resultIt.Next(RIL_WORD)) results.add(readTargetFromResult(resultIt))
            resultIt.deallocate()
        }
        return results.filter { it.height > 10 && it.string.count { it.isLetterOrDigit() } > 4 }
    }

    private fun readTargetFromResult(resultIt: ResultIterator): Target {
        val outTextPtr = resultIt.GetUTF8Text(RIL_WORD)
        val outText = outTextPtr.string
        outTextPtr.deallocate()

        val conf = resultIt.Confidence(RIL_WORD)

        val top = IntPointer(1)
        val left = IntPointer(1)
        val right = IntPointer(1)
        val bottom = IntPointer(1)

        val pageIt = TessResultIteratorGetPageIterator(resultIt)
        TessPageIteratorBoundingBox(pageIt, RIL_WORD, left, top, right, bottom)

        val x1 = left.get().toDouble() / SCALE_FACTOR
        val y1 = top.get().toDouble() / SCALE_FACTOR
        val x2 = right.get().toDouble() / SCALE_FACTOR
        val y2 = bottom.get().toDouble() / SCALE_FACTOR
        arrayOf(left, top, right, bottom).forEach { it.deallocate() }
        pageIt.deallocate()

        return Target(outText, conf, x1, y1, x2, y2)
    }
}