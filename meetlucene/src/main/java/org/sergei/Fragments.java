package org.sergei;

import org.apache.lucene.store.*;
import org.apache.lucene.search.*;
import org.apache.lucene.index.*;
import java.io.File;
import java.io.IOException;


/** Just contains any code fragments from chapter 1 */

public class Fragments {
  public void simpleSearch() throws IOException {
    Directory dir = FSDirectory.open(new File("/tmp/index"));
    IndexSearcher searcher = new IndexSearcher(dir);
    Query q = new TermQuery(new Term("contents", "lucene"));
    TopDocs hits = searcher.search(q, 10);
    searcher.close();
  }
}