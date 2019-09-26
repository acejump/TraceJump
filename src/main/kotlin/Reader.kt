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
import kotlin.math.abs
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

object Reader {
    private val api = TessBaseAPI()
    val SCALE_FACTOR = 1.0f
    var previousScreenshot: PIX? = null
    var lastFractDiff = 0.0f
    var fractDiff = 0.0f

    init {
        if (api.Init("src/main/resources", "eng") != 0) {
            System.err.println("Could not initialize Tesseract")
            exitProcess(1)
        }
    }

    private val robot = Robot()

    fun getScreenContents(): PIX {
        val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
        val capture = robot.createScreenCapture(screenRect)
        val j2d = Java2DFrameConverter().convert(capture)
        val pix = LeptonicaFrameConverter().convert(j2d)
        val pixScaled = lept.pixScale(pix, SCALE_FACTOR, SCALE_FACTOR)
        pix.deallocate()
        return pixScaled
    }

    fun fetchTargets(): Map<String, Target>? {
        val screenshot = getScreenContents()
        if (!hasScreenChanged(screenshot, previousScreenshot)) {
            screenshot.deallocate()
            return null
        }
        val results = getResults(screenshot).filter { it.string.length > 3 }
        if(previousScreenshot != null) lept.pixDestroy(previousScreenshot)
        previousScreenshot = screenshot
        lastFractDiff = fractDiff
        return Pattern.filterTags("").zip(results).toMap()
    }

    private fun hasScreenChanged(screenshot: PIX, previousScreenshot: PIX?) =
        if (previousScreenshot == null) true
        else {
            val fractdiff = FloatPointer(0.20f)
            val avediff = FloatPointer(0.0f)
            lept.pixGetDifferenceStats(
                screenshot, previousScreenshot, 0, 1, fractdiff, avediff, 0
            )
            this.fractDiff = fractdiff.get()
            listOf(fractdiff, avediff).forEach { it.deallocate() }
            val relativeDiff = abs(lastFractDiff - fractDiff)
            0.0001f < relativeDiff
        }

    fun TessBaseAPI.recognizeImage(image: PIX) {
        SetImage(image)
        val durationMs = measureTimeMillis {
            val monitor = TessMonitorCreate()
            val resultCode = Recognize(monitor)
            monitor.deallocate()
            if (resultCode != 0) {
                throw Exception("Recognition error: $resultCode")
            }
        }
    }

    fun getResults(image: PIX): MutableList<Target> {
        api.recognizeImage(image)
        val results = mutableListOf<Target>()
        val resultIt = api.GetIterator()
        if (resultIt != null) {
            do results.add(readTargetFromResult(resultIt))
            while (resultIt.Next(RIL_WORD))
            resultIt.deallocate()
        }
        return results
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