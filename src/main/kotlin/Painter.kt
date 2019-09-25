
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Screen
import javafx.stage.Stage
import kotlin.system.measureTimeMillis

fun paintScene(
    resultMap: Map<String, Target>,
    scene: Scene,
    mouseHandler: EventHandler<MouseEvent>,
    stage: Stage
) {
    val freshCanvas = Screen.getPrimary().visualBounds.run { Canvas(width, height) }
    measureTimeMillis { paintTargets(freshCanvas, resultMap) }
    scene.root = Pane().apply { children.add(freshCanvas) }
    scene.onMouseClicked = mouseHandler
    stage.show()
    stage.requestFocus()
}

private fun paintTargets(canvas: Canvas, resultMap: Map<String, Target>) {
    resultMap.forEach { paintTarget(canvas, it.value, it.key) }
}

private fun paintTarget(canvas: Canvas, target: Target, tag: String) {
    if (target.conf <= 1 || target.string.length < 3) return
    val gc = canvas.graphicsContext2D

    gc.fill = Color(0.0, 1.0, 0.0, 0.4)
    gc.fillRoundRect(
        target.x1,
        target.y1,
        target.width,
        target.height,
        10.0,
        10.0
    )
    gc.fill = Color(1.0, 1.0, 0.0, 1.0)
    val widthOfTag = target.height * 0.7 * 2
    val startOfTag = target.x1 - widthOfTag
    gc.fillRoundRect(
        startOfTag,
        target.y1,
        widthOfTag,
        target.height,
        10.0,
        10.0
    )
    gc.fill = Color(0.0, 0.0, 0.0, 1.0)
    gc.font = Font.font("Courier")
    gc.fillText(tag.toUpperCase(), startOfTag, target.y2)
}