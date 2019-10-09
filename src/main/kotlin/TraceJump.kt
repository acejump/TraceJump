import Jumper.jumpTo
import javafx.application.Application
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.util.concurrent.atomic.AtomicBoolean

class TraceJump : Application() {
    var listener: Listener = Listener(this) { resultMap[it]?.run { jumpTo(this); hasJumped.set(true) } }

    @Volatile
    var resultMap: Map<String, Target> = mapOf()
    lateinit var canvas: Canvas
    lateinit var scene: Scene
    lateinit var stage: Stage

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

    @Volatile var screenWatcherThread: Thread? = null
    @Volatile var repaintingThread: Thread? = null

    val repainting = object : Task<Void?>() {
        override fun call(): Void? {
            while (true) {
                repaintingThread?.suspend()

                if (hasJumped.compareAndSet(true, false) ||
                    listener.deactivated.compareAndSet(true, false))
                    Platform.runLater { reset() }
                else if (listener.activated.compareAndSet(true, false) && resultMap.isNotEmpty())
                    Platform.runLater { paintScene(resultMap, scene, mouseHandler, stage) }
            }
        }
    }

    val mouseHandler = EventHandler<MouseEvent> { event ->
        resultMap.values.firstOrNull { it.isPointInMap(event.x, event.y + VOFFSET) }
            ?.run {
                jumpTo(this)
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
            initStyle(StageStyle.TRANSPARENT)
            scene = this@TraceJump.scene
            x = 0.0
            y = 0.0
        }
    }

    private fun reset() {
        listener.query = ""
        listener.active.set(false)
        stage.close()
    }

    fun run() = launch()
}