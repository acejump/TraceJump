package org.acejump.tracejump

import javafx.application.Platform
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.NativeInputEvent
import org.jnativehook.NativeInputEvent.*
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyEvent.VC_BACK_SLASH
import org.jnativehook.keyboard.NativeKeyEvent.VC_ESCAPE
import org.jnativehook.keyboard.NativeKeyListener
import java.util.*
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess


class Listener(val traceJump: TraceJump, val takeAction: (String) -> Unit?) : NativeKeyListener,
    /*NativeMouseMotionListener, NativeMouseListener,*/ AbstractExecutorService() {
    var ctrlDown = AtomicBoolean(false)
    var lastUpdated = 0L
    @Volatile
    var active = AtomicBoolean(false)
    @Volatile
    var query = ""

    init {
        try {
            Logger.getLogger(GlobalScreen::class.java.getPackage().name).apply {
                level = Level.WARNING
                useParentHandlers = false
            }

            GlobalScreen.registerNativeHook()
        } catch (ex: NativeHookException) {
            System.err.println(ex.message)
            exitProcess(1)
        }

        GlobalScreen.addNativeKeyListener(this)
//        GlobalScreen.addNativeMouseMotionListener(this)
//        GlobalScreen.addNativeMouseListener(this)
        GlobalScreen.setEventDispatcher(this)
    }

    val ctrlKeys = listOf(CTRL_MASK, CTRL_L_MASK, CTRL_R_MASK, 29)

    override fun nativeKeyPressed(keyEvent: NativeKeyEvent) {
        if (keyEvent.keyCode in ctrlKeys) {
            ctrlDown.set(true)
            traceJump.screenWatcherThread?.resume()
        } else if (keyEvent.keyCode == VC_BACK_SLASH && ctrlDown.compareAndSet(true, false)) {
            active.set(true)
            Platform.runLater { traceJump.paint() }
            traceJump.screenWatcherThread?.resume()
        } else if (keyEvent.keyCode == VC_ESCAPE) {
            Platform.runLater { traceJump.reset() }
        }

        Trigger(100) { traceJump.screenWatcherThread?.resume() }
    }

    override fun nativeKeyReleased(keyEvent: NativeKeyEvent) {
        if (keyEvent.keyCode in ctrlKeys) ctrlDown.set(false)
    }

    override fun nativeKeyTyped(keyEvent: NativeKeyEvent) {
        if (keyEvent.keyChar.isLetterOrDigit() && active.get()) {
            query += keyEvent.keyChar.toString()
            takeAction(query)
            consume(keyEvent)
        }
    }

    private fun consume(keyEvent: NativeKeyEvent) {
        try {
            NativeInputEvent::class.java.getDeclaredField("reserved").apply {
                isAccessible = true
                setShort(keyEvent, 0x01.toShort())
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

//    override fun nativeMouseMoved(p0: NativeMouseEvent?) =
//        Trigger (1000) { traceJump.screenWatcherThread?.resume() }
//
//    override fun nativeMouseDragged(p0: NativeMouseEvent?) =
//        Trigger (1000) { traceJump.screenWatcherThread?.resume() }
//
//    override fun nativeMousePressed(p0: NativeMouseEvent?) =
//        Trigger (1000) { traceJump.screenWatcherThread?.resume() }
//
//    override fun nativeMouseClicked(p0: NativeMouseEvent?) =
//        Trigger (1000) { traceJump.screenWatcherThread?.resume() }
//
//    override fun nativeMouseReleased(p0: NativeMouseEvent?) =
//        Trigger (1000) { traceJump.screenWatcherThread?.resume() }

    private var running = false

    override fun shutdown() {
        running = false
    }

    override fun shutdownNow(): List<Runnable?>? {
        running = false
        return ArrayList(0)
    }

    override fun isShutdown() = !running

    override fun isTerminated() = !running

    @Throws(InterruptedException::class)
    override fun awaitTermination(timeout: Long, unit: TimeUnit?) = true

    override fun execute(r: Runnable) = r.run()
}