package org.acejump.tracejump

import org.acejump.tracejump.Menu.SearchProvider.values
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Image
import org.jetbrains.skija.Paint

object Menu {
  const val logoWidth = 100f

  enum class SearchProvider(val key: Char, val url: String) {
    google('g', "https://google.com/search?q="),
    github('h', "https://github.com/search?type=Code&q="),
    stackoverflow('o', "https://stackoverflow.com/search?q="),
    scholar('s', "https://scholar.google.com/scholar?q="),
    wikipedia('w', "https://en.wikipedia.org/wiki/Special:Search?search="),
    tensorflow('t', "https://www.tensorflow.org/s/results?q="),
    pytorch('p', "https://pytorch.org/docs/stable/search.html?q=")
  }

  fun readFile(name: String) =
    javaClass.classLoader.getResource(name).readBytes()

  val logos: Map<SearchProvider, Image> =
    values().associate { it to Image.makeFromEncoded(readFile("$it.png")) }

  val modalKeyMap = (values().map { it.key to it.url } +
      ('1'..'6').map { it to "https://kotlinlang.org/?q=" }).toMap()

  // TODO: Figure out what's wrong and finish this
  fun draw(gc: Canvas, selectedTag: Target) =
    gc.run {
      val padding = 25f
      val boxWidth = (logoWidth + padding)
      val startX = 100 / 2f - logos.size * boxWidth / 2f
      val startY = 100 / 2f - logoWidth
      gc.paintRectangle(
        startX - padding,
        startY - padding * 2,
        logos.size * boxWidth + padding,
        logoWidth + 10 * padding,
        green
      )
      val fontHeight = 20f
      val fontWidth = 15f

      logos.entries.forEachIndexed { i, e ->
        val bottomHeight = startY + logoWidth + padding / 2
        drawImage(e.value, startX + boxWidth * i, startY, null)
        val startOfTag = startX + boxWidth * i + boxWidth / 2f - 20f
//            fill = Color(1.0, 1.0, 0.0, 1.0)

        paintRectangle(
          startOfTag, bottomHeight + 3f,
          fontWidth, fontHeight, Paint()
        )
//            fill = Color(0.0, 0.0, 0.0, 1.0)
        val letter = e.key.key.toUpperCase().toString()
        paintString(letter, startOfTag + 2, bottomHeight + fontHeight)
      }

      val query = selectedTag.string
//        fill = Color(0.5, 0.5, 0.5, 1.0)
      paintRectangle(
        startX, startY - 20 - fontHeight + 3,
        query.length * (fontWidth - 2.5f), fontHeight,
        green
      )
//        fill = Color(0.0, 0.0, 0.0, 1.0)
      paintString("\uD83D\uDD0E$query", startX, startY - 20)

//        fill = Color(0.0, 0.0, 0.0, 1.0)

      val startOfResultX = startX
      val startOfResultY = startY + logoWidth + 2 * padding
      val words = "lorem ipsum … dolor sit … amet consectetur $query"

      ('A'..'F').forEachIndexed { i, l ->
//            fill = Color(1.0, 1.0, 0.0, 1.0)
        paintRectangle(
          startOfResultX, startOfResultY + (i * (fontHeight + 3f)),
          fontWidth, fontHeight,
          green
        )
//            fill = Color(0.0, 0.0, 0.0, 1.0)
        val result = words.split(" ").shuffled().joinToString(" ")
        paintString(
          "${i + 1} Doc.$l - $result",
          startOfResultX + 2,
          startOfResultY - 5 + ((i + 1) * (fontHeight + 3))
        )
      }
    }
}