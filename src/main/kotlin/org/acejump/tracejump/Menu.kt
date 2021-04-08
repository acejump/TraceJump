package org.acejump.tracejump

import org.acejump.tracejump.Menu.SearchProvider.values
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Image
import java.awt.*


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
  fun draw(gc: Canvas, selectedTag: Target) = gc.run {
    val padding = 35f
    val boxWidth = logoWidth + padding
    val dimension: Dimension = Toolkit.getDefaultToolkit().screenSize
    val (cw, ch) = dimension.getWidth().toFloat() to
      dimension.getHeight().toFloat()
    val (midX, midY) =
      cw / 2f - (logos.size + 1) * boxWidth / 2f to
        ch / 2f - logoWidth

    val fontHeight = 23f
    val fontWidth = 18f

    paintRectangle(
      midX - padding,
      midY - padding * 2 - padding,
      (logos.size + 1) * boxWidth + padding,
      logoWidth + 11 * padding,
      gray
    )

    logos.entries.forEachIndexed { i, e ->
      val bottomHeight = logoWidth + padding / 2
      paintImage(
        e.value,
        midX + boxWidth * i,
        midY,
        bottomHeight,
        bottomHeight
      )
      val startOfTag = midX + boxWidth * i + boxWidth / 2f - 20f

      paintRectangle(
        startOfTag, midY + bottomHeight + 3f,
        fontWidth, fontHeight, red
      )

      val letter = e.key.key.toUpperCase().toString()
      paintString(letter, startOfTag, midY + bottomHeight + fontHeight + 5f)
    }

    val query = selectedTag.string

    paintRectangle(
      midX, midY - 2 * fontHeight,
      query.length * fontWidth, fontHeight,
      green
    )

    paintString("\uD83D\uDD0E$query", midX, midY - 20)

    val startOfResultX = midX
    val startOfResultY = midY + logoWidth + 2 * padding
    val words = "lorem ipsum … dolor sit … amet consectetur $query"

    val vspace = fontHeight + 5f
    ('A'..'F').forEachIndexed { i, l ->
      paintRectangle(
        startOfResultX + 2f, startOfResultY + i * vspace - 6f,
        fontWidth, fontHeight,
        red
      )

      val result = words.split(" ").shuffled().joinToString(" ")
      paintString(
        "${i + 1} Doc.$l - $result",
        startOfResultX + 2,
        startOfResultY - 5 + (i + 1) * vspace
      )
    }
  }
}