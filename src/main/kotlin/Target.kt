import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font

class Target(
    val string: String = "",
    val conf: Float = 0f,
    val x1: Double = 0.0,
    var y1: Double = 0.0,
    val x2: Double = 0.0,
    var y2: Double = 0.0
) {
    val width = x2 - x1
    val height = y2 - y1
    fun isPointInMap(x: Double, y: Double) = x in x1..x2 && y in y1..y2

    fun paint(gc: GraphicsContext, tag: String) {
        if (conf <= 1 || string.length < 3) return

        gc.fill = Color(0.0, 1.0, 0.0, 0.4)
        gc.fillRoundRect(
            x1,
            y1 - VOFFSET,
            width,
            height,
            10.0,
            10.0
        )
        gc.fill = Color(1.0, 1.0, 0.0, 1.0)
        val heightTag = 15.0
        val widthOfTag = 20.0
        val startOfTag = if(x1 > widthOfTag) x1 - widthOfTag else x2
        gc.fillRoundRect(
            startOfTag,
            y2 - heightTag - VOFFSET,
            widthOfTag,
            heightTag,
            10.0,
            10.0
        )
        gc.fill = Color(0.0, 0.0, 0.0, 1.0)
        gc.font = Font.font("Courier")
        gc.fillText(tag.toUpperCase(), startOfTag + 2, y2 - VOFFSET - 3)
    }
}