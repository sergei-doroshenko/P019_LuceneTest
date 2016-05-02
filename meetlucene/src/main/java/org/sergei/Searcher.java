package org.sergei;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

public class Searcher {

    public static void main(String[] args) throws IllegalArgumentException, IOException, ParseException {

        String indexDir = "tmp2/index";               //1
        String q = "hello";                      //2

        search(indexDir, q);
    }

    public static void search(String indexDir, String q) throws IOException, ParseException {

        Directory dir = FSDirectory.open(new File(indexDir)); //3
        IndexSearcher is = new IndexSearcher(dir);   //3

        QueryParser parser = new QueryParser(
                Version.LUCENE_30,
                "contents",
                new WhitespaceAnalyzer()
//                new StandardAnalyzer(Version.LUCENE_30)
        );

        Query query = parser.parse(q.toUpperCase());              //4

        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10); //5
        long end = System.currentTimeMillis();

        System.err.println("Found " + hits.totalHits +   //6
            " document(s) (in " + (end - start) +        // 6
            " milliseconds) that matched query '" +     // 6
            q + "':");                                   // 6

        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);               //7
            System.out.println(doc.get("fullpath"));  //8
        }

        is.close();                                //9
    }
}

/*
#1 Parse provided index directory
#2 Parse provided query string
#3 Open index
#4 Parse query
#5 Search index
#6 Write search stats
#7 Retrieve matching document
#8 Display filename
#9 Close IndexSearcher
*/
