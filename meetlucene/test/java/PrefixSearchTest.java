import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.sergei.TestUtil;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Sergei_Doroshenko on 5/2/2016.
 */
public class PrefixSearchTest {
    protected String[] ids = {"1", "2", "3"};
    protected String[] unindexed = {"Netherlands", "Italy", "Norway"};
    protected String[] unstored = {"Amsterdam has lots of bridges", "Amor Venice has lots of canals", "Test"};
    protected String[] text = {"Amsterdam", "Venice", "Amlon"};
    private Directory directory;
    private Analyzer analyzer = new WhitespaceAnalyzer();
//    private Analyzer analyzer = new SimpleAnalyzer();

    @Before
    public void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("country", unindexed[i], Field.Store.YES, Field.Index.NO));
            doc.add(new Field("contents", unstored[i], Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field("city", text[i], Field.Store.YES, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        writer.close();
    }

    private IndexWriter getWriter() throws IOException {
        return new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
    }

    protected int getHitCount(String fieldName, String searchString) throws IOException {
        IndexSearcher searcher = new IndexSearcher(directory);
        Term t = new Term(fieldName, searchString);
        Query query = new TermQuery(t);
        int hitCount = TestUtil.hitCount(searcher, query);
        searcher.close();
        return hitCount;
    }

    @Test
    public void testPrefixSearch() throws IOException, ParseException {
        IndexSearcher is = new IndexSearcher(directory);
        QueryParser qp = new QueryParser(Version.LUCENE_30, "city", analyzer);
        qp.setLowercaseExpandedTerms(false);
        Query query = qp.parse("Am*");
        System.out.println("start...");

        TopDocs hits = is.search(query, 2);
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);               //7
            System.out.println(doc.get("city"));  //8
        }

        is.close();

//        assertEquals(ids.length, writer.numDocs());

    }

    @Test
    public void testPrefixMultifieldSearch() throws IOException, ParseException {
        IndexSearcher is = new IndexSearcher(directory);
        String[] fields = new String[] {"city", "contents"};
        QueryParser qp = new MultiFieldQueryParser(Version.LUCENE_30, fields, analyzer);
        qp.setLowercaseExpandedTerms(false);
        Query query = qp.parse("Am*");
        System.out.println("start...");

        TopDocs hits = is.search(query, 5);
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);               //7
            System.out.println(doc.get("city"));  //8
        }

        is.close();
    }

    @Test
    public void testLowercasing() throws Exception {
        Query q = new QueryParser(Version.LUCENE_30, "field", analyzer).parse("PrefixQuery*");
        assertEquals("lowercased", "prefixquery*", q.toString("field"));
        QueryParser qp = new QueryParser(Version.LUCENE_30, "field", analyzer);
        qp.setLowercaseExpandedTerms(false);
        q = qp.parse("PrefixQuery*");
        assertEquals("not lowercased", "PrefixQuery*", q.toString("field"));
    }

}
