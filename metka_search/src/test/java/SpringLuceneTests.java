import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommandBase;
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
        indexer.addCommand(new XMLIndexerCommand(IndexerCommandBase.Action.INDEX));
        assertEquals(1, indexer.queueSize());
    }
}
