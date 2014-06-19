import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.XMLIndexerCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class SpringLuceneTests {
    @Autowired
    private IndexerComponent indexer;

    @Test
    public void someTest() {
        assertEquals(0, indexer.queueSize());
        indexer.addCommand(new XMLIndexerCommand(IndexerCommand.Action.INDEX));
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
                indexer.addCommand(new XMLIndexerCommand(IndexerCommand.Action.INDEX));
                if(loops == 5) {
                    indexer.stopCommandHandler();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
