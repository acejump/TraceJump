import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder

object Jumper {
    fun jumpTo(tg: Target) {
        try {
            val queryString = URLEncoder.encode(tg.string, "UTF-8")
            val urlPrefix = "https://stackoverflow.com/search?q="
            val url = "$urlPrefix$queryString".also { println("Jumping to $it") }
            Desktop.getDesktop().browse(URI(url))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}