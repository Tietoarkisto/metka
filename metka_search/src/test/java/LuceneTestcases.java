import fi.uta.fsd.metkaSearch.LuceneAPI;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Company: Proactum Oy
 * User: Eemu Bertling
 * Date: 24.1.2014
 * Time: 16:18
 */
public class LuceneTestcases {
    @Test
    public void LuceneMemoryTest() throws Exception {
        TopScoreDocCollector collector;
        LuceneAPI api;

        api = new LuceneAPI(LuceneAPI.IndexType.MEMORY, null);
        // Add document to index
        Document doc = new Document();
        doc.add(new TextField("document", "Plaa plaa plaa test", Store.YES));
        api.addDocument(doc);
        // Add another document to index
        doc = new Document();
        doc.add(new TextField("document", "Ploo ploo ploo test", Store.YES));
        api.addDocument(doc);

        // Search document.
        collector = api.findDocuments("document","plaa");
        assertTrue(collector.getTotalHits() == 1);

        // Add another document to index
        doc = new Document();
        doc.add(new TextField("document", "Ploo ploo plaa test", Store.YES));
        api.addDocument(doc);

        // Search document.
        collector = api.findDocuments("document","ploo");
        assertTrue(collector.getTotalHits() == 2);

    }

    @Test
    public void LuceneFileTest() throws Exception {
        LuceneAPI api = new LuceneAPI(LuceneAPI.IndexType.FILESYSTEM_WRITABLE, "luceneTestCaseIndex");
        // Add document to index
        Document doc = new Document();
        doc.add(new TextField("document", "Plaa plaa plaa test", Store.YES));
        api.addDocument(doc);
        // Add another document to index
        doc = new Document();
        doc.add(new TextField("document", "Ploo ploo ploo test", Store.YES));
        api.addDocument(doc);

        // Search document.
        TopScoreDocCollector collector;
        collector = api.findDocuments("document","plaa");
        int hits1 = collector.getTotalHits();

        // Delete index
        api.destroyIndex();

        assertTrue("Wrong amount of hits returned!", hits1 == 1);

    }


}
