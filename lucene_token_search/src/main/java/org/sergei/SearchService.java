package org.sergei;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Sergei_Doroshenko on 12/21/2016.
 */
public class SearchService {
    AtomicReference<IndexSearcher> searcherRef = new AtomicReference<IndexSearcher>( null );
    String indexPath = "";

    private Document createDocument(Order order) {
        Document document = new Document();
        document.add( new Field( "id", order.getOrgerId().toString(), Field.Store.NO, Field.Index.ANALYZED ) );
        document.add( new Field( "name", order.getOrderName(), Field.Store.YES, Field.Index.ANALYZED ) );
        document.add( new Field( "type", order.getOrderType(), Field.Store.YES, Field.Index.ANALYZED ) );
        document.add( new Field( "description", order.getOrderDesctiption(), Field.Store.YES, Field.Index.ANALYZED ) );
        return document;
    }

    private List<Document> createDocuments(List<Order> orders) {
        List documents = orders.stream().map(o -> createDocument( o )).collect( Collectors.toList());
        return documents;
    }

    public void createIndex(List<Document> documents) {
        Analyzer analyzer = new WhitespaceAnalyzer( Version.LUCENE_36);
        String indexName = indexPath;
        File indexFile = new File(indexName);
        try {
            IndexWriter indexWriter = new IndexWriter( new NIOFSDirectory( indexFile ), new IndexWriterConfig( Version.LUCENE_36, analyzer ) );

            for (Document d : documents) {
                indexWriter.addDocument( d );
            }

            indexWriter.commit();
            indexWriter.close();

            IndexSearcher searcher = searcherRef.getAndSet( createSearcher() );
            // close previous IndexSearcher
            if (searcher != null) {
                searcher.close();
                searcher.getIndexReader().close();
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private IndexSearcher createSearcher(){
        IndexSearcher searcher = null;
        try {
            IndexReader reader = IndexReader.open( new NIOFSDirectory( new File(indexPath) ) );
            searcher = new IndexSearcher( reader );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return searcher;
    }

    public List<String> search(String query) {
        List<String> result = new ArrayList<>();
        return result;
    }
}
