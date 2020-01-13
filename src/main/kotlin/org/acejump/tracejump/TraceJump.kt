package org.acejump.tracejump


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
import javafx.stage.StageStyle.TRANSPARENT
import org.acejump.tracejump.Jumper.jumpTo
import org.acejump.tracejump.Menu.modalKeyMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class TraceJump : Application() {
    @Volatile var tagSelected = false
    @Volatile var selectedTag: Target? = null

    var listener: Listener =
        Listener(this) {
            val lastTwo = it.takeLast(2)
            if (!tagSelected && lastTwo in resultMap) {
                selectedTag = resultMap[lastTwo]
                println("Tag selected: ${selectedTag!!.string}")
                tagSelected = true
                Platform.runLater {
                    setStage(mouseHandler, stage) { gc ->
                        Menu.draw(
                            gc,
                            selectedTag!!,
                            canvas.width,
                            canvas.height
                        )
                    }
                }
            } else if (tagSelected) {
                val lastChar = it.last()
                if (lastChar in modalKeyMap) {
                    jumpTo(selectedTag!!, modalKeyMap[lastChar]!!)
                    hasJumped.set(true)
                    Platform.runLater { reset() }
                    tagSelected = false
                }
            }
        }

    var resultMap: Map<String, Target> = ConcurrentHashMap()
    lateinit var canvas: Canvas
    lateinit var stage: Stage

    private val hasJumped = AtomicBoolean(false)

    val screenWatcher = object : Task<Void?>() {
        override fun call(): Void? {
            while (true) {
                if (!listener.active.get())
                    try {
                        Reader.fetchTargets()?.run { resultMap = this }
                    } catch (ex: Exception) { println("exception: ${ex.stackTrace}") }
                screenWatcherThread?.suspend()
            }
        }
    }

    @Volatile var screenWatcherThread: Thread? = null

    fun paint() = setStage(mouseHandler, stage) { gc -> resultMap.forEach { it.value.paint(gc, it.key) } }

    val menuMouseHandler = EventHandler<MouseEvent> { event ->
        println("Mouse clicked at: (${event.x}, ${event.y})")
    }

    val mouseHandler = EventHandler<MouseEvent> { event ->
        println("Mouse clicked at: (${event.x}, ${event.y})")
        resultMap.values
            .firstOrNull { it.isPointInMap(event.x, event.y) }
            ?.run {
                Platform.runLater {
                    setStage(menuMouseHandler, stage) { gc ->
                        Menu.draw(gc,
                            this,
                            canvas.width,
                            canvas.height
                        )
                    }
                }
                hasJumped.set(true)
            }
    }

    override fun start(stage: Stage) {
        Platform.setImplicitExit(false)

        this.stage = stage
        canvas = Screen.getPrimary().visualBounds.run { Canvas(width, height) }

        Thread(screenWatcher).apply { isDaemon = true; screenWatcherThread = this }.start()

        stage.run {
            initStyle(TRANSPARENT)
            scene = Scene(Pane().apply { children.add(canvas) }, canvas.width, canvas.height)
                .apply { fill = Color.TRANSPARENT }
            x = 0.0
            y = 0.0
        }
    }

    fun reset() {
        listener.query = ""
        listener.active.set(false)
        stage.close()
        selectedTag = null
        tagSelected = false
    }
}