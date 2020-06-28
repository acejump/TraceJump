package org.acejump.tracejump

import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Screen
import javafx.stage.Stage
import kotlin.system.measureTimeMillis

fun setStage(
    mouseHandler: EventHandler<MouseEvent>,
    stage: Stage,
    paint: (GraphicsContext) -> Unit
) {
    val freshCanvas = Screen.getPrimary().visualBounds.run { Canvas(width, height) }
    measureTimeMillis { paint(freshCanvas.graphicsContext2D) }
    val scene = stage.scene
    scene.root = Pane().apply { children.add(freshCanvas) }
    scene.onMouseClicked = mouseHandler
    stage.run {
        show()
        if (isLinux) {
            fullScreenExitHint = ""
            isFullScreen = true
            isAlwaysOnTop = true
        }
        requestFocus()
    }
}

val isLinux = System.getProperty("os.name")
    .let { "nix" in it || "nux" in it || "aix" in it }
var VOFFSET = if (isLinux) 0 else 25