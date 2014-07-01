package fi.uta.fsd.metkaSearch.sax;

import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.WikipediaIndexerCommand;
import fi.uta.fsd.metkaSearch.information.DirectoryInformation;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Indexer for wikipedia pages.
 * Used for testing lucene integration with large data sets.
 * Works as a state machine due to the need of using SAX and event based parsing.
 */
public class WikipediaHandler extends DefaultHandler {
    private static enum STATE {
        START_DOCUMENT,
        END_DOCUMENT,
        PAGE,
        REVISION,
        // STOP is a special state. If parser enters into stop then all event calls (except start document)
        // are returned straight away and no further parsing is done apart from possibly some finalizing actions.
        // If STOP state is reached then end document will ideally reverse all commits made during the file parsing
        // but at least the latest commit will be reversed.
        STOP
    }

    private final DirectoryInformation indexer;
    private final WikipediaIndexerCommand command;
    private static final int DOCUMENT_BATCH_SIZE = 500; // How many pages to index between flushes
    private int documentBatch;
    private Map<String, Analyzer> analyzers;
    private STATE state;
    private Document currentDocument;
    private StringBuilder currentCharacters;
    private int pagecounter = 0;

    public WikipediaHandler(DirectoryInformation indexer, WikipediaIndexerCommand command) {
        this.indexer = indexer;
        this.command = command;
    }

    /**
     * Start parsing provided Wikipedia document (assume correct document for now).
     * Initialise internal parameters so that indexing can proceed correctly.
     * @throws SAXException
     */
    @Override
    public void startDocument() throws SAXException {
        System.err.println("Started parsing document: "+command.getPath());
        state = STATE.START_DOCUMENT;
        // Create analyzers map for PerFieldAnalyzerWrapper
        analyzers = new HashMap<>();
        documentBatch = 0;
        currentCharacters = null;
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // STOP state check
        if(state == STATE.STOP) {
            return;
        }

        switch(localName) {
            case "page":
                // You can only move to a page element from START_DOCUMENT or another page.
                // If this is not the case then stop the parsing
                if(!(state == STATE.START_DOCUMENT || state == STATE.PAGE)) {
                    state = STATE.STOP;
                    return;
                }

                System.err.println("Started new page: "+(++pagecounter));
                state = STATE.PAGE;
                // Create new document
                currentDocument = new Document();
                // add XML-file path as a field to the
                currentDocument.add(new StringField("path", command.getPath(), Field.Store.NO));
                break;
            case "revision":
                // You should only get to revision from page
                if(state != STATE.PAGE) {
                    state = STATE.STOP;
                    return;
                }
                state = STATE.REVISION;
                break;
            case "title":
            case "id":
            case "text":
                // Any one of these warrants a new start of content
                currentCharacters = new StringBuilder();
                break;
        }
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // STOP state check
        if(state == STATE.STOP) {
            return;
        }

        // No StringBuilder, we don't want to read the element
        if(currentCharacters == null) {
            return;
        }

        // append characters to string builder
        currentCharacters.append(ch, start, length);

        super.characters(ch, start, length);
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // STOP state check
        if(state == STATE.STOP) {
            return;
        }

        // TODO: add more content and different analyzers
        switch(localName) {
            case "page":
                if(state != STATE.PAGE) {
                    // Something is wrong with the structure, stop
                    state = STATE.STOP;
                    return;
                }

                // Add current document to index
                // Create analyzer wrapper
                PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(IndexerComponent.USED_VERSION), analyzers);

                // TODO: Check if document already exists in the index
                // TODO: If document already exists in the index check the revision id, if revision id equals current id then there's no need to reindex.
                // TODO: If revision id has changed then remove old document before trying to add new document


                // Try to add document to index
                try {
                    indexer.getIndexWriter().addDocument(currentDocument, analyzer);
                } catch(Exception ex) {
                    ex.printStackTrace();
                    // Couldn't add document to index, no point in continuing
                    state = STATE.STOP;
                    return;
                }

                documentBatch++;
                // If there's been more or equal number of new documents than defined DOCUMENT_BATCH_SIZE then flush index.
                if(documentBatch >= DOCUMENT_BATCH_SIZE) {
                    try {
                        flushIndex();
                    } catch(IOException ex) {
                        ex.printStackTrace();
                        state = STATE.STOP;
                    }
                }
                break;
            case "revision":
                if(state != STATE.REVISION) {
                    // Something is wrong with the structure, stop
                    state = STATE.STOP;
                    return;
                }
                // Return to page level
                state = STATE.PAGE;
                break;
            case "title":
                addCharactersAsStringField("title");
                break;
            case "id":
                // TODO: add as number field with different analyzer
                if(state == STATE.PAGE) {
                    addCharactersAsStringField("pageId");
                } else if(state == STATE.REVISION) {
                    addCharactersAsStringField("revisionId");
                }
                break;
            case "text":
                addCharactersAsStringField("text");
                break;

        }
        super.endElement(uri, localName, qName);
    }

    private void addCharactersAsStringField(String key) {
        try {
            currentDocument.add(new TextField(key, currentCharacters.toString(), Field.Store.NO));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        currentCharacters = null;
    }

    /**
     * Finished parsing provided Wikipedia document.
     * Do finalizing operations.
     * @throws SAXException
     */
    @Override
    public void endDocument() throws SAXException {
        // STOP state check
        if(state == STATE.STOP) {
            // Try rolling back latest commit.
            try {
                indexer.getIndexWriter().rollback();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        state = STATE.END_DOCUMENT;
        try {
            flushIndex();
        } catch(IOException ex) {
            ex.printStackTrace();
            // Something is wrong with the indexer, abort current thread
            Thread.currentThread().interrupt();
        }
        super.endDocument();
    }

    /**
     * Flush current index writer.
     * This is called at least when file parsing has finished and can be called periodically throughout indexing.
     * @throws IOException
     */
    private void flushIndex() throws IOException {
        documentBatch = 0;
        if(indexer != null) {
            try {
                // Try to commit the writer
                indexer.getIndexWriter().commit();
                // Set indexer to dirty state so that searchers know to update their index
                indexer.setDirty(true);
            } catch (OutOfMemoryError er) {
                er.printStackTrace();
                // If we get an OutOfMemoryError then close the writer immediately
                try {
                    // Try closing the writer
                    indexer.getIndexWriter().close();
                } catch(OutOfMemoryError erc) {
                    // As I understand it we should get another OutOfMemoryError, close the writer again
                    indexer.getIndexWriter().close();
                } finally {
                    // Interrupt current Thread since we can't continue indexing
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
