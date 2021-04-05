package org.acejump.tracejump

import org.jetbrains.skija.*
import org.jetbrains.skija.FontStyle.BOLD
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow

fun setStage(skiaWindow: SkiaWindow, draw: (Canvas) -> Unit) =
  skiaWindow.apply {
    layer.renderer = Renderer { renderer, w, h -> draw(renderer.canvas!!) }

    setSize(10000, 10000)
  }

fun Canvas.paintRectangle(
  x1: Float, y1: Float, width: Float,
  height: Float, paint: Paint
) = drawRRect(
  RRect.makeLTRB(
    x1, y1 - VOFFSET,
    x1 + width, y1 - VOFFSET + height,
    10f, 10f
  ), paint
)

fun Canvas.paintString(tag: String, x1: Float, y1: Float) =
  FontMgr.getDefault().matchFamilyStyle("Courier", BOLD).use { face ->
    Font(face, 26f).use { font ->
      drawString(
        tag,
        x1 + 2, y1 - VOFFSET - 10f,
        font,
        black
      )
    }
  }

class Renderer(val displayScene: (Renderer, Int, Int) -> Unit) : SkiaRenderer {
  var canvas: Canvas? = null

  override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
    this.canvas = canvas
    displayScene(this, width, height)
  }
}

val red = Paint().setColor(0xffff0000.toInt()).setAlpha(200)
val green = Paint().setColor(0xff00ff00.toInt()).setAlpha(100)
val black = Paint().setColor(-0x1000000)

val isLinux = System.getProperty("os.name")
  .let { "nix" in it || "nux" in it || "aix" in it }

var VOFFSET = 25