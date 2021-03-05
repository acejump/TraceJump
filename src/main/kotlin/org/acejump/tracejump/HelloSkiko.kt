package org.acejump.tracejump

import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import org.jetbrains.skija.RRect
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow

fun main() {
    SkiaWindow().apply {
        // TODO: How do we make this window transparent / foreground invisible?

        layer.renderer = Renderer { renderer, w, h ->
            val canvas = renderer.canvas!!
            val paint1 = Paint().setColor(0xffff0000.toInt()).setAlpha(100) // ARGB
            canvas.drawRRect(RRect.makeLTRB(10f, 10f, w - 10f, h - 10f, 5f), paint1)
            val paint2 = Paint().setColor(0xff00ff00.toInt()).setAlpha(100) // ARGB
            canvas.drawRRect(RRect.makeLTRB(30f, 30f, w - 30f, h - 30f, 10f), paint2)
        }

        isVisible = true
        setSize(800, 600)
    }
}

class Renderer(val displayScene: (Renderer, Int, Int) -> Unit): SkiaRenderer {
    var canvas: Canvas? = null

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        this.canvas = canvas
        displayScene(this, width, height)
    }
}