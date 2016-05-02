import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
public class FuzzySearchTest {
    protected String[] ids = {"1", "2", "3"};
    protected String[] text = {"Netherlands", "Italy", "Norway Republic"};

    private Directory directory;
    private Analyzer analyzer = new WhitespaceAnalyzer();

    @Before
    public void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("contents", text[i], Field.Store.YES, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        writer.close();
    }

    private IndexWriter getWriter() throws IOException {
        return new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
    }

    @Test
    public void testFuzzyMultifieldSearch() throws IOException, ParseException {
        IndexSearcher is = new IndexSearcher(directory);
        String[] fields = new String[] {"contents"};
        QueryParser qp = new MultiFieldQueryParser(Version.LUCENE_30, fields, analyzer);
        qp.setLowercaseExpandedTerms(false);
        Query query = qp.parse("Norwa~");
        System.out.println("start...");

        TopDocs hits = is.search(query, 5);
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);               //7
            System.out.println(doc.get("contents"));  //8
        }

        is.close();
    }
}
