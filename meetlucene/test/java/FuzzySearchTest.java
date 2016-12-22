import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Sergei_Doroshenko on 5/2/2016.
 */
public class FuzzySearchTest {

    protected String[] ids = {"1", "2", "3"};
    protected String[] authors = {
            "Daniel Abraham",
            "Robert Jordan",
            "Peter S. Beagle"
    };
    protected String[] titles = {
            "A Shadow In Summer",
            "The Eye Of The World",
            "The Last Unicorn"
    };
    protected String[] texts = {
            "Seedy docksides come to life, impressive noble houses sparkle and glitter with wealth, raggedy beggars roam the streets begging for coin. " +
                    "This is a world that's completely alive folks, a world that beckons and entices. And it's a world that you want to lose yourself in utterly",

            "Since the happier days of Jordan in the 90's, fantasy has all grown up. Postmodernism is in. Antiheroes are vogue. Happy Heroes on a quest are most " +
                    "definitely not in fashion. Indeed, classic fantasy with callow village boys and dark wizards are often viewed with contempt by the modern fantasy " +
                    "aficionado who's standards in plotting, storytelling, and characterization have evolved.",

            "A modern classic that weaves together themes of language, immortality, illusion and belief. Its the story of a unicorn and her young human companion as " +
                    "they meander through a landscape woven of myth and legend. Its a book of startling beauty the words themselves are lyrical and even if you are not " +
                    "delighted and captivated by the story (which you will be), the beauty of the lyrical prose will keep you spellbound."
    };

    private Directory directory;
    private Analyzer analyzer = new WhitespaceAnalyzer();

    @Before
    public void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("authors", authors[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("titles", titles[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("texts", texts[i], Field.Store.YES, Field.Index.ANALYZED));
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
        String[] fields = new String[] {"authors", "titles", "texts"};
        QueryParser qp = new MultiFieldQueryParser(Version.LUCENE_30, fields, analyzer);
        qp.setLowercaseExpandedTerms(false);
        Query query = qp.parse("Dan~0.3f");
        System.out.println("start...");

        TopDocs hits = is.search(query, 3);
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            System.out.println(doc.get("titles"));
        }

        is.close();
    }
}
