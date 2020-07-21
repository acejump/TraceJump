package org.acejump.tracejump

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.acejump.tracejump.Menu.SearchProvider.values

object Menu {
    const val logoWidth = 100.0

    enum class SearchProvider(val key: Char, val url: String) {
        GOOGLE('g', "https://google.com/search?q="),
        GITHUB('h', "https://github.com/search?type=Code&q="),
        STACKOVERFLOW('o', "https://stackoverflow.com/search?q="),
        SCHOLAR('s', "https://scholar.google.com/scholar?q="),
        WIKIPEDIA('w', "https://en.wikipedia.org/wiki/Special:Search?search="),
        TENSORFLOW('t', "https://www.tensorflow.org/s/results?q="),
        PYTORCH('p', "https://pytorch.org/docs/stable/search.html?q=")
    }

    val logos: Map<SearchProvider, Image> =
        values().map { it to Image("/$it.png", logoWidth, logoWidth, false, true) }.toMap()

    val modalKeyMap = (values().map { it.key to it.url } +
            ('1'..'6').map { it to "https://kotlinlang.org/?q=" }).toMap()

    fun draw(gc: GraphicsContext, selectedTag: Target, width: Double, height: Double) = gc.run {
        font = Font.font("Courier", 20.0)
        fill = Color(0.5, 0.5, 0.5, 1.0)

        val padding = 25.0
        val boxWidth = (logoWidth + padding)
        val startX = width / 2.0 - logos.size * boxWidth / 2.0
        val startY = height / 2.0 - logoWidth
        fillRoundRect(
            startX - padding,
            startY - padding * 2,
            logos.size * boxWidth + padding,
            logoWidth + 10 * padding,
            20.0,
            20.0
        )
        val fontHeight = 20.0
        val fontWidth = 15.0

        logos.entries.forEachIndexed { i, e ->
            val bottomHeight = startY + logoWidth + padding / 2
            drawImage(e.value, startX + boxWidth * i, startY)
            val startOfTag = startX + boxWidth * i + boxWidth / 2.0 - 20.0
            fill = Color(1.0, 1.0, 0.0, 1.0)

            fillRoundRect(startOfTag, bottomHeight + 3.0, fontWidth, fontHeight, 10.0, 10.0)
            fill = Color(0.0, 0.0, 0.0, 1.0)
            val letter = e.key.key.toUpperCase().toString()
            fillText(letter, startOfTag + 2, bottomHeight + fontHeight)
        }

        val query = selectedTag.string
        fill = Color(0.5, 0.5, 0.5, 1.0)
        fillRoundRect(startX, startY - 20 - fontHeight + 3, query.length * (fontWidth - 2.5), fontHeight, 10.0, 10.0)
        fill = Color(0.0, 0.0, 0.0, 1.0)
        fillText("\uD83D\uDD0E$query", startX, startY - 20)

        fill = Color(0.0, 0.0, 0.0, 1.0)

        val startOfResultX = startX
        val startOfResultY = startY + logoWidth + 2 * padding
        val words = "lorem ipsum … dolor sit … amet consectetur $query"

        ('A'..'F').forEachIndexed { i, l ->
            fill = Color(1.0, 1.0, 0.0, 1.0)
            fillRoundRect(startOfResultX, startOfResultY + (i * (fontHeight + 3)), fontWidth, fontHeight, 10.0, 10.0)
            fill = Color(0.0, 0.0, 0.0, 1.0)
            val result = words.split(" ").shuffled().joinToString(" ")
            fillText("${i + 1} Doc.$l - $result", startOfResultX + 2, startOfResultY - 5 + ((i+1) * (fontHeight + 3)))
        }
    }
}