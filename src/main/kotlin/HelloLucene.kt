
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import java.io.FileReader
import java.nio.file.Paths

fun main() {
    val analyzer = StandardAnalyzer()
    val index = RAMDirectory() // FSDirectory.open(Paths.get("test"))

    val config = IndexWriterConfig(analyzer)

    val w = IndexWriter(index, config)
    addDoc(w, "Lucene in Action", "193398817")
    addDoc(w, "Lucene for Dummies", "55320055Z")
    addDoc(w, "Managing Gigabytes", "55063554A")
    addDoc(w, "The Art of Computer Science", "9900333X")
    w.close()

    // 2. query
    val querystr = "lucene"

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    val q = QueryParser("title", analyzer).parse(querystr)

    // 3. search
    val hitsPerPage = 10
    val reader = DirectoryReader.open(index)
    val searcher = IndexSearcher(reader)
    val collector = TopScoreDocCollector.create(hitsPerPage, 10)
    searcher.search(q, collector)
    val hits = collector.topDocs().scoreDocs

    // 4. display results
    println("Found " + hits.size + " hits.")
    for (i in hits.indices) {
        val docId = hits[i].doc
        val d = searcher.doc(docId)
        println((i + 1).toString() + ". " + d.get("isbn") + "\t" + d.get("title"))
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close()
}

private fun addDoc(w: IndexWriter, title: String, isbn: String) =
    w.addDocument(Document().apply {
        add(TextField("title", title, Field.Store.YES))
        add(StringField("isbn", isbn, Field.Store.YES))
    })

class LuceneFileSearch(val indexDirectory: Directory, val analyzer: StandardAnalyzer) {
    fun addFileToIndex(filepath: String) {
        val file = Paths.get(javaClass.classLoader.getResource(filepath)!!.toURI()).toFile()
        val indexWriterConfig = IndexWriterConfig(analyzer)
        val indexWriter = IndexWriter(indexDirectory, indexWriterConfig)
        val document = Document()

        val fileReader = FileReader(file)
        document.add(TextField("contents", fileReader))
        document.add(StringField("path", file.path, Field.Store.YES))
        document.add(StringField("filename", file.name, Field.Store.YES))

        indexWriter.addDocument(document)

        indexWriter.close()
    }

    fun searchFiles(inField: String, queryString: String): List<Document> {
        val query = QueryParser(inField, analyzer).parse(queryString)
        val indexReader = DirectoryReader.open(indexDirectory)
        val searcher = IndexSearcher(indexReader)
        return searcher.search(query, 10).scoreDocs.map { searcher.doc(it.doc) }
    }
}