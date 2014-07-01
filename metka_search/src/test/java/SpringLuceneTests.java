import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.DummyIndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.WikipediaIndexerCommand;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class SpringLuceneTests {
    @Autowired
    private IndexerComponent indexer;

    @Test
    public void someTest() {
        assertEquals(0, indexer.queueSize());
        indexer.addCommand(new DummyIndexerCommand(IndexerCommand.Action.INDEX));
        assertEquals(1, indexer.queueSize());
    }

    @Test
    public void indexerRunningTest() {
        try {
            int loops = 0;
            while(indexer.isHandlerRunning()) {
                Thread.sleep(15000);
                loops++;
                System.err.println("Test loops: "+loops);
                indexer.addCommand(new DummyIndexerCommand(IndexerCommand.Action.INDEX));
                if(loops == 5) {
                    indexer.stopCommandHandler();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void wikipediaIndexingTest() {
        try {
            if(indexer.isHandlerRunning()) {
                indexer.addCommand(new WikipediaIndexerCommand("/home/lasseku/wikipedia/en/enwiki-latest-pages-articles2.xml-p000010002p000024999", IndexerCommand.Action.INDEX));
            }
            while(indexer.isHandlerRunning()) {
                Thread.sleep(1000);
                if(!IndexerComponent.handlingCommand) {
                    indexer.stopCommandHandler();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void printSearchResult(IndexSearcher searcher, Query qr) throws IOException {
        TopDocs hits = searcher.search(qr, 100);
        System.err.println("Hits: " + hits.totalHits);

        for(ScoreDoc doc : hits.scoreDocs) {
            System.err.println(searcher.doc(doc.doc).get("content")+" "+"["+doc.score+"]");
        }

    }
}
