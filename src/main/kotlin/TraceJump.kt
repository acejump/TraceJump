import Jumper.jumpTo
import javafx.application.Application
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle.TRANSPARENT
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.measureTimeMillis

class TraceJump : Application() {
    @Volatile
    var tagSelected = false
    @Volatile
    var selectedTag: Target? = null

    val modalKeyMap = mapOf(
        'g' to "https://google.com/search?q=",
        'h' to "https://github.com/search?type=Code&q=",
        'o' to "https://stackoverflow.com/search?q=",
        's' to "https://scholar.google.com/scholar?q="
    )

    var listener: Listener = Listener(this) {
        val lastTwo = it.takeLast(2)

        if (!tagSelected && lastTwo in resultMap) {
            selectedTag = resultMap[lastTwo]
            println("Tag selected: $selectedTag")
            tagSelected = true
        } else if (tagSelected) {
            val lastChar = it.last()
            if (lastChar in modalKeyMap) {
                jumpTo(selectedTag!!, modalKeyMap[lastChar]!!)
                hasJumped.set(true)
                tagSelected = false
            }
        }
    }

    @Volatile
    var resultMap: Map<String, Target> = mapOf()
    lateinit var canvas: Canvas
    lateinit var scene: Scene
    lateinit var stage: Stage

    val logoWidth = 100.0
    val logos: Map<String, Image> = listOf("github", "google", "stackoverflow", "scholar")
        .map { Pair(it, Image("/$it.png", logoWidth, logoWidth, false, false)) }.toMap()

    private val hasJumped = AtomicBoolean(false)

    val screenWatcher = object : Task<Void?>() {
        override fun call(): Void? {
            while (true) {
                if (listener.active.get()) repaintingThread?.resume()
                Reader.fetchTargets()?.run { resultMap = this }
                if (listener.active.get()) repaintingThread?.resume()
                screenWatcherThread?.suspend()
            }
        }
    }

    @Volatile
    var screenWatcherThread: Thread? = null
    @Volatile
    var repaintingThread: Thread? = null

    val repainting = object : Task<Void?>() {
        override fun call(): Void? {
            while (true) {
                repaintingThread?.suspend()

                if (listener.deactivated.compareAndSet(true, false))
                    Platform.runLater { reset() }
                else if (tagSelected)
                    Platform.runLater { openMenu(scene, mouseHandler, stage) }
                else if (hasJumped.compareAndSet(true, false))
                    Platform.runLater { reset() }
                else if (listener.activated.compareAndSet(true, false) && resultMap.isNotEmpty())
                    Platform.runLater { paintScene(resultMap, scene, mouseHandler, stage) }
            }
        }
    }

    fun openMenu(
        scene: Scene,
        mouseHandler: EventHandler<MouseEvent>,
        stage: Stage
    ) {
        val freshCanvas = Screen.getPrimary().visualBounds.run { Canvas(width, height) }
        measureTimeMillis { drawMenu(freshCanvas) }
        scene.root = Pane().apply { children.add(freshCanvas) }
        scene.onMouseClicked = mouseHandler
        stage.show()
        val os = System.getProperty("os.name")
        if ("nix" in os || "nux" in os || "aix" in os) {
            stage.fullScreenExitHint = ""
            stage.isFullScreen = true
        }
        stage.requestFocus()
    }

    private fun drawMenu(freshCanvas: Canvas) {
        val gc = freshCanvas.graphicsContext2D
        gc.font = Font.font("Courier", 20.0)
        gc.fill = Color(0.5, 0.5, 0.5, 1.0)

        val padding = 25.0
        val boxWidth = (logoWidth + padding)
        val startX = canvas.width / 2.0 - logos.size * boxWidth / 2.0
        val startY = canvas.height / 2.0 - logoWidth
        gc.fillRoundRect(
            startX - padding,
            startY - padding * 2,
            logos.size * boxWidth + padding,
            logoWidth + 4 * padding,
            20.0,
            20.0
        )
        val fontHeight = 20.0
        val fontWidth = 15.0

        logos.entries.forEachIndexed { i, e ->
            val bottomHeight = startY + logoWidth + padding / 2
            gc.drawImage(e.value, startX + boxWidth * i, startY)
            val startOfTag = startX + boxWidth * i + boxWidth / 2.0 - 20.0
            gc.fill = Color(1.0, 1.0, 0.0, 1.0)

            gc.fillRoundRect(startOfTag, bottomHeight + 3.0, fontWidth, fontHeight, 10.0, 10.0)
            gc.fill = Color(0.0, 0.0, 0.0, 1.0)
            val letter = when (e.key) {
                "google" -> "G"
                "github" -> "H"
                "stackoverflow" -> "O"
                "scholar" -> "S"
                else -> ""
            }
            gc.fillText(letter, startOfTag + 2, bottomHeight + fontHeight)
        }

        val query = selectedTag!!.string
        gc.fill = Color(0.5, 0.5, 0.5, 1.0)
        gc.fillRoundRect(startX, startY - 20 - fontHeight + 3, query.length * (fontWidth - 2.5), fontHeight, 10.0, 10.0)
        gc.fill = Color(0.0, 0.0, 0.0, 1.0)
        gc.fillText("\uD83D\uDD0E$query", startX, startY - 20)
    }

    val mouseHandler = EventHandler<MouseEvent> { event ->
        resultMap.values.firstOrNull { it.isPointInMap(event.x, event.y + VOFFSET) }
            ?.run {
                hasJumped.set(true)
            }
    }

    override fun start(stage: Stage) {
        Platform.setImplicitExit(false)

        this.stage = stage
        canvas = Screen.getPrimary().visualBounds.run { Canvas(width, height) }
        scene = Scene(Pane().apply { children.add(canvas) }, canvas.width, canvas.height).apply {
            fill = Color.TRANSPARENT
        }

        Thread(repainting).apply { isDaemon = true; repaintingThread = this }.start()
        Thread(screenWatcher).apply { isDaemon = true; screenWatcherThread = this }.start()

        stage.run {
            initStyle(TRANSPARENT)
            scene = this@TraceJump.scene
            x = 0.0
            y = 0.0
        }
    }

    private fun reset() {
        listener.query = ""
        listener.active.set(false)
        stage.close()
        selectedTag = null
        tagSelected = false
    }
}