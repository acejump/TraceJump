package org.acejump.tracejump

import org.jetbrains.skija.Canvas

class Target(
  val string: String = "",
  val conf: Float = 0f,
  val x1: Float = 0.0f,
  var y1: Float = 0.0f,
  val x2: Float = 0.0f,
  var y2: Float = 0.0f
) {
  val width = x2 - x1
  val height = y2 - y1
  fun isPointInMap(x: Double, y: Double) =
    x in x1..x2 && y in y1..y2

  fun paint(canvas: Canvas, tag: String) {
    if (conf <= 1 || string.length < 3) return

    canvas.paintRectangle(x1, y1, width, height, green)

    val heightTag = 15f
    val widthOfTag = 30f
    val startOfTag = if (x1 > widthOfTag) x1 - widthOfTag else x2

    canvas.paintRectangle(
      startOfTag, y2 - heightTag - 10f,
      widthOfTag, heightTag + 10f, red
    )

    canvas.paintString(tag.toUpperCase(), startOfTag, y2)
  }
}