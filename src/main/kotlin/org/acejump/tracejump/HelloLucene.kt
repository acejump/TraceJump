//package org.acejump.tracejump
//
//import org.apache.lucene.analysis.standard.StandardAnalyzer
//import org.apache.lucene.index.DirectoryReader
//import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
//import org.apache.lucene.search.IndexSearcher
//import org.apache.lucene.search.TopScoreDocCollector
//import org.apache.lucene.store.MMapDirectory
//import java.io.File
//
//fun main() {
//
//}
//
//val analyzer = StandardAnalyzer()
//val index = MMapDirectory(File("index").toPath())
//val parser = MultiFieldQueryParser(arrayOf("title", "contents", "uri"),
//    analyzer
//)
//
//
//fun query(querystr: String = "test") {
//    // search
//    val hitsPerPage = 9
//    val reader = DirectoryReader.open(index)
//    val searcher = IndexSearcher(reader)
//
//    val collector = TopScoreDocCollector.create(hitsPerPage, 10)
//    searcher.search(parser.parse(querystr), collector)
//    val hits = collector.topDocs().scoreDocs
//
//    // display results
//    println("Found ${hits.size} hits.")
//    hits.forEachIndexed { i, scoreDoc ->
//        val docId = scoreDoc.doc
//        val d = searcher.doc(docId)
//        println((i + 1).toString() + ". " + d.get("title") + "\t" + d.get("uri"))
//    }
//    reader.close()
//}