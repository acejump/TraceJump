import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font

object Menu {
    val logoWidth = 100.0
    val logos: Map<String, Image> = listOf("github", "google", "stackoverflow", "scholar")
        .map { Pair(it, Image("/$it.png", logoWidth, logoWidth, false, false)) }.toMap()

    val modalKeyMap = mapOf(
        'g' to "https://google.com/search?q=",
        'h' to "https://github.com/search?type=Code&q=",
        'o' to "https://stackoverflow.com/search?q=",
        's' to "https://scholar.google.com/scholar?q="
    )

    fun draw(gc: GraphicsContext, selectedTag: Target, width: Double, height: Double) {
        gc.font = Font.font("Courier", 20.0)
        gc.fill = Color(0.5, 0.5, 0.5, 1.0)

        val padding = 25.0
        val boxWidth = (logoWidth + padding)
        val startX = width / 2.0 - logos.size * boxWidth / 2.0
        val startY = height / 2.0 - logoWidth
        gc.fillRoundRect(
            startX - padding,
            startY - padding * 2,
            logos.size * boxWidth + padding,
            logoWidth + 4 * padding,
            20.0,
            20.0
        )
        val fontHeight = 20.0
        val fontWidth = 15.0

        logos.entries.forEachIndexed { i, e ->
            val bottomHeight = startY + logoWidth + padding / 2
            gc.drawImage(e.value, startX + boxWidth * i, startY)
            val startOfTag = startX + boxWidth * i + boxWidth / 2.0 - 20.0
            gc.fill = Color(1.0, 1.0, 0.0, 1.0)

            gc.fillRoundRect(startOfTag, bottomHeight + 3.0, fontWidth, fontHeight, 10.0, 10.0)
            gc.fill = Color(0.0, 0.0, 0.0, 1.0)
            val letter = when (e.key) {
                "google" -> "G"
                "github" -> "H"
                "stackoverflow" -> "O"
                "scholar" -> "S"
                else -> ""
            }
            gc.fillText(letter, startOfTag + 2, bottomHeight + fontHeight)
        }

        val query = selectedTag.string
        gc.fill = Color(0.5, 0.5, 0.5, 1.0)
        gc.fillRoundRect(startX, startY - 20 - fontHeight + 3, query.length * (fontWidth - 2.5), fontHeight, 10.0, 10.0)
        gc.fill = Color(0.0, 0.0, 0.0, 1.0)
        gc.fillText("\uD83D\uDD0E$query", startX, startY - 20)
    }
}