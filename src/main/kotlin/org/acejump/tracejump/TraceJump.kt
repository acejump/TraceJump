package org.acejump.tracejump

import org.acejump.tracejump.Jumper.jumpTo
import org.acejump.tracejump.Menu.modalKeyMap
import org.jetbrains.skiko.SkiaWindow
import java.awt.event.WindowEvent
import java.awt.event.WindowEvent.WINDOW_CLOSING
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JFrame

object TraceJump {
    @Volatile
    var tagSelected = false

    @Volatile
    var selectedTag: Target? = null

    @Volatile
    var openWindow: JFrame? = null

    var listener = Listener(this) {
        val lastTwo = it.takeLast(2)
        if (!tagSelected && lastTwo in resultMap) {
            selectedTag = resultMap[lastTwo]
            println("Tag selected: ${selectedTag!!.string}")
            tagSelected = true
            openWindow?.close()
            openWindow = setStage(makeWindow()) { gc -> Menu.draw(gc, selectedTag!!) }
        } else if (tagSelected) {
            val lastChar = it.last()
            if (lastChar in modalKeyMap) {
                jumpTo(selectedTag!!, modalKeyMap[lastChar]!!)
                hasJumped.set(true)
                reset()
                tagSelected = false
            }
        }

        Unit
    }

    fun makeWindow() = SkiaWindow().apply {
        isUndecorated = true
        opacity = 0.4f
        isVisible = true
    }

    var resultMap: Map<String, Target> = ConcurrentHashMap()

    private val hasJumped = AtomicBoolean(false)

    val screenWatcher = Thread {
        while (true) {
            if (!listener.active.get())
                try {
                    Reader.fetchTargets()?.run { resultMap = this }
                } catch (ex: Exception) {
                    println("exception: ${ex.stackTrace}")
                }
            screenWatcherThread?.suspend()
        }
    }

    var screenWatcherThread: Thread? = null

    fun tagScreen() {
        openWindow?.close()
        openWindow = setStage(makeWindow()) { gc ->
            resultMap.forEach { it.value.paint(gc, it.key) }
        }
    }

    fun start() = Thread(screenWatcher).apply {
        isDaemon = true
        screenWatcherThread = this
    }.start()

    fun JFrame.close() =
        apply { dispatchEvent(WindowEvent(this, WINDOW_CLOSING)) }

    fun reset() {
        openWindow?.close()
        openWindow = null
        listener.query = ""
        listener.active.set(false)
        selectedTag = null
        tagSelected = false
    }
}