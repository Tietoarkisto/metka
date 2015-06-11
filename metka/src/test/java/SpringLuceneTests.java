import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metkaSearch.IndexWriterFactory;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class SpringLuceneTests {
    @Autowired
    private IndexerComponent indexer;
    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private ConfigurationRepository configs;

    @Autowired
    private DirectoryManager manager;

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Test
    public void temp() throws Exception {
        Analyzer analyzer = FinnishVoikkoAnalyzer.ANALYZER;
        Document doc = new Document();
        RAMDirectory directory = new RAMDirectory();
        IndexWriter writer = IndexWriterFactory.createIndexWriter(directory);
        doc.add(new TextField("test", "<b>Jotain tekstiä</b> <span>tagien</span> sisällä", Field.Store.NO));
        writer.addDocument(doc, analyzer);
    }

    @Test
    public void tempSearchTest() throws IOException, QueryNodeException {
        DirectoryManager.DirectoryPath path = new DirectoryManager.DirectoryPath(false, IndexerConfigurationType.REVISION);
        DirectoryInformation dir = manager.getIndexDirectory(path, false);
        IndexReader reader = dir.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);

        Map<String, Analyzer> analyzers = new HashMap<>();
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(LuceneConfig.USED_VERSION), analyzers);

        StandardQueryParser parser = new StandardQueryParser(analyzer);
        Query query = parser.parse("+notes.note:(aineisto teksti)", "general");

        TopDocs hits = searcher.search(query, 100);
        for(ScoreDoc doc : hits.scoreDocs) {
            Document document = searcher.doc(doc.doc);
            System.err.println("Search result ID: "+document.get("key.id")+" | NO: "+document.get("key.no") + " | STUDY_ID: "+document.get("studyid"));
        }
        System.err.println("Hits: " + hits.totalHits);
    }

    @Test
    public void randomTest() {
        String s = "jotain";
        assertTrue(s.split("\\.").length == 0);
    }

    @Test
    public void expertSearchTest() throws IOException, QueryNodeException, ParseException {
        SearchCommand<RevisionResult> command = ExpertRevisionSearchCommand.build("variables.variables.valuelabels.label:nainen", configs);
        ResultList<RevisionResult> results = searcher.executeSearch(command);
        System.err.println("Results: "+results.getResults().size());
        for(RevisionResult result : results.getResults()) {
            System.err.println(result.toString());
        }
    }

    @Test
    public void analyzerTest() throws IOException, QueryNodeException {
        //indexer.startIndexer(DirectoryManager.formPath(true, IndexerConfigurationType.DUMMY, null));
        DirectoryInformation di = manager.getIndexDirectory(DirectoryManager.formPath(true, IndexerConfigurationType.DUMMY), true);
        Map<String, Analyzer> analyzers = new HashMap<>();
        analyzers.put("analyzed1", new FinnishVoikkoAnalyzer());
        analyzers.put("analyzed2", new FinnishVoikkoAnalyzer());
        TEST_ANALYZER = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(LuceneConfig.USED_VERSION), analyzers);
        Document document = new Document();
        document.add(new TextField("analyzed1", "Tässä tekstiä sekä numero 111 ja lisäksi päivämäärä 2020-02-20T02:02", Field.Store.NO));
        document.add(new TextField("analyzed2", "Tässä tekstiä sekä numero 111 ja lisäksi päivämäärä 2020-02-20T02:02", Field.Store.YES));
        document.add(new TextField("text1", "Joku arvo", Field.Store.NO));
        document.add(new TextField("text2", "Joku toinen arvo", Field.Store.NO));
        document.add(new StringField("string1", "Joku arvo", Field.Store.NO));
        document.add(new StringField("string2", "Joku toinen arvo", Field.Store.NO));
        document.add(new StringField("string3", "Joku", Field.Store.NO));
        di.getIndexWriter().addDocument(document, TEST_ANALYZER);
        di.getIndexWriter().commit();
        IndexReader reader = di.getIndexReader();
        TEST_SEARCHER = new IndexSearcher(reader);
        TEST_PERFORM_NUM = 0;
        TEST_PARSER = new StandardQueryParser(TEST_ANALYZER);
        performTestQuery("+analyzed1:1");
        performTestQuery("+analyzed1:111");
        performTestQuery("+analyzed1:2020");
        performTestQuery("+analyzed1:2020*");
        performTestQuery("+analyzed1:\"2020-02-20T02:02\"");
        performTestQuery("+text1:\"Joku arvo\"");
        performTestQuery("+text1:\"joku arvo\"");
        performTestQuery("+text1:\"Joku  arvo\"");
        performTestQuery("+text2:\"Joku arvo\"");
        performTestQuery("+text2:\"Joku toinen arvo\"");
        performTestQuery("+text2:Joku");
        performTestQuery("+text2:joku");
        performTestQuery("+string1:\"Joku arvo\"");
        performTestQuery("+string1:\"joku arvo\"");
        performTestQuery("+string1:\"Joku  arvo\"");
        performTestQuery("+string2:\"Joku arvo\"");
        performTestQuery("+string2:\"Joku toinen arvo\"");

        performTestQuery("+string3:Joku");
    }

    @Test
    public void dateRangeTest() throws Exception {
        TEST_INDEXER = manager.getIndexDirectory(DirectoryManager.formPath(true, IndexerConfigurationType.DUMMY), true);
        Map<String, Analyzer> analyzers = new HashMap<>();
        analyzers.put("date1", new FinnishVoikkoAnalyzer());
        analyzers.put("date2", new KeywordAnalyzer());
        analyzers.put("date_description", new FinnishVoikkoAnalyzer());
        TEST_ANALYZER = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(LuceneConfig.USED_VERSION), analyzers);

        System.err.println("-- Analyzer --");
        System.err.println("Documents are analyzed using WhiteSpaceAnalyzer as the default analyzer, using FinnishVoikkoAnalyzer for field 'date1' and 'date_description' and using KeywordAnalyzer of field 'date2'");
        System.err.println("");
        System.err.println("-- Data --");
        System.err.println("All documents contain the following fields:");
        System.err.println("document_id which is a running integer");
        System.err.println("date1 which is a finnish voikko analyzed text field");
        System.err.println("date2 which is a keyword analyzed text field");
        System.err.println("date3 which is a whitespace analyzed text field");
        System.err.println("date4 which is a string field");
        System.err.println("date_description which is a finnish analyzed text field with the phrase 'Dokumentin päiväys on' appended before the date time value.");
        System.err.println("");
        for(int i = 1; i < 6; i++) {
            addDateRangeDateDocument(new LocalDateTime(2014, i, i, i, i, i));
        }
        TEST_INDEXER.getIndexWriter().commit();

        IndexReader reader = TEST_INDEXER.getIndexReader();
        TEST_SEARCHER = new IndexSearcher(reader);
        TEST_PERFORM_NUM = 0;
        TEST_PARSER = new StandardQueryParser(TEST_ANALYZER);

        System.err.println("");
        System.err.println("-- Queries --");

        System.err.println("All queries are performed for all fields except document_id");
        System.err.println("All queries are performed with + in their field clause meaning that the query is a MUST query (document must contain result)");
        System.err.println("All query results contain the number of results and the ID's of the documents that contain a hit.");
        System.err.println("Results are in following order: ");
        System.err.println("Result 1: date_description");
        System.err.println("Result 2: date1");
        System.err.println("Result 3: date2");
        System.err.println("Result 4: date3");
        System.err.println("Result 5: date4");

        System.err.println("");
        System.err.println("-- Results --");

        performDateRangeTestQuery("2014");
        performDateRangeTestQuery("dokumentti");
        performDateRangeTestQuery("päiväys");
        performDateRangeTestQuery("2014-01");
        performDateRangeTestQuery("\"2014-01\"");
        performDateRangeTestQuery("2014*");
        performDateRangeTestQuery("2014-01-01T01:01:01.000");
        performDateRangeTestQuery("\"2014-01-01T01:01:01.000\"");
        performDateRangeTestQuery("[2014-01 2014-03]");
        performDateRangeTestQuery("[2014-01* 2014-03*]");
        performDateRangeTestQuery("[\"2014-01*\" \"2014-03*\"]");
        performDateRangeTestQuery("[2014-01-01 2014-03-02]");
        performDateRangeTestQuery("[2014-01-01* 2014-03-02*]");
        performDateRangeTestQuery("[\"2014-01-01*\" \"2014-03-02*\"]");
        performDateRangeTestQuery("[2014-01-01 2014-03-03]");
        performDateRangeTestQuery("[2014-01-01* 2014-03-03*]");
        performDateRangeTestQuery("[\"2014-01-01*\" \"2014-03-03*\"]");
        performDateRangeTestQuery("[2014-01-01 2014-03-04]");
        performDateRangeTestQuery("[2014-01-01* 2014-03-04*]");
        performDateRangeTestQuery("[\"2014-01-01*\" \"2014-03-04*\"]");
        performDateRangeTestQuery("[2014-02-01 2014-02-02]");
        performDateRangeTestQuery("[2014-02-01* 2014-02-02*]");
        performDateRangeTestQuery("[\"2014-02-01*\" \"2014-02-02*\"]");
        performDateRangeTestQuery("[2014-02-01 2014-02-03]");
        performDateRangeTestQuery("[2014-02-01* 2014-02-03*]");
        performDateRangeTestQuery("[\"2014-02-01*\" \"2014-02-03*\"]");
        performDateRangeTestQuery("[2014-02-02 2014-02-02]");
        performDateRangeTestQuery("[2014-02-02* 2014-02-02*]");
        performDateRangeTestQuery("[\"2014-02-02*\" \"2014-02-02*\"]");
        performDateRangeTestQuery("[2014-02-02 2014-02-03]");
        performDateRangeTestQuery("[2014-02-02* 2014-02-03*]");
        performDateRangeTestQuery("[\"2014-02-02*\" \"2014-02-03*\"]");
        performDateRangeTestQuery("[02-02 02-03]");
        performDateRangeTestQuery("[02-02* 02-03*]");
        performDateRangeTestQuery("[\"02-02*\" \"02-03*\"]");
        TEST_PARSER.setAllowLeadingWildcard(true);
        System.err.println("Enable allow leading wildcard");
        performDateRangeTestQuery("*02");
        performDateRangeTestQuery("*02*");
        performDateRangeTestQuery("*02-02*");
        performDateRangeTestQuery("\"*02-02*\"");
        performDateRangeTestQuery("*\"02-02\"*");
        performDateRangeTestQuery("[*02-02 *02-03]");
        performDateRangeTestQuery("[*02-02* *02-03*]");
        performDateRangeTestQuery("[\"*02-02*\" \"*02-03*\"]");
        TEST_PARSER.setAllowLeadingWildcard(false);
        System.err.println("Disable allow leading wildcard");
    }

    private void addDateRangeDateDocument(LocalDateTime dt) throws Exception {
        Document document = new Document();
        document.add(new IntField("document_id", ++TEST_DOCUMENT_ID, Field.Store.YES));
        document.add(new TextField("date_description", "Dokumentin päiväys on " + dt.toString(), Field.Store.NO));
        document.add(new TextField("date1", dt.toString(), Field.Store.NO));
        document.add(new TextField("date2", dt.toString(), Field.Store.NO));
        document.add(new TextField("date3", dt.toString(), Field.Store.NO));
        document.add(new StringField("date4", dt.toString(), Field.Store.NO));
        System.err.println("New document: ID ("+TEST_DOCUMENT_ID+") | LocalDateTime ("+dt.toString()+")");
        TEST_INDEXER.getIndexWriter().addDocument(document, TEST_ANALYZER);
    }

    private void performDateRangeTestQuery(String query) throws Exception {
        if(!StringUtils.hasText(query)) {
            return;
        }
        System.err.println("Performing query: "+query);
        TEST_PERFORM_NUM = 0;
        performDateRangeTestQuery(query, "date_description");
        performDateRangeTestQuery(query, "date1");
        performDateRangeTestQuery(query, "date2");
        performDateRangeTestQuery(query, "date3");
        performDateRangeTestQuery(query, "date4");
    }

    private void performDateRangeTestQuery(String query, String field) throws Exception {
        try {
            performTestQuery("+"+field+":"+query, "document_id");
        } catch(Exception e) {
            System.err.println("Could not perform query on field: "+field);
        }
    }

    @Test
    public void numberRangeTest() throws Exception {
        TEST_INDEXER = manager.getIndexDirectory(DirectoryManager.formPath(true, IndexerConfigurationType.DUMMY), true);
        Map<String, Analyzer> analyzers = new HashMap<>();
        analyzers.put("int1", new FinnishVoikkoAnalyzer());
        analyzers.put("int2", new KeywordAnalyzer());
        analyzers.put("float1", new FinnishVoikkoAnalyzer());
        analyzers.put("float2", new KeywordAnalyzer());

        Map<String, NumericConfig> nums = new HashMap<>();
        nums.put("int3", new NumericConfig(LuceneConfig.PRECISION_STEP, new DecimalFormat(), FieldType.NumericType.INT));
        nums.put("float3", new NumericConfig(LuceneConfig.PRECISION_STEP, new DecimalFormat(), FieldType.NumericType.FLOAT));

        TEST_ANALYZER = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(LuceneConfig.USED_VERSION), analyzers);

        System.err.println("-- Analyzer --");
        System.err.println("Documents are analyzed using WhiteSpaceAnalyzer as the default analyzer, using FinnishVoikkoAnalyzer for field 'int1' and 'float1' and using KeywordAnalyzer of fields 'int2' and 'float2'");
        System.err.println("");
        System.err.println("-- Data --");
        System.err.println("All documents contain the following fields:");
        System.err.println("int1 which is a finnish voikko analyzed text field");
        System.err.println("int2 which is a keyword analyzed text field");
        System.err.println("int3 which is a IntField and searcher is told to regard it as an IntField");
        System.err.println("int4 which is a IntField with seracher not told to regard it as an IntField");
        System.err.println("float1 which is a finnish voikko analyzed text field");
        System.err.println("float2 which is a keyword analyzed text field");
        System.err.println("float3 which is a FloatField and searcher is told to regard it as an FloatField");
        System.err.println("float4 which is a FloatField with seracher not told to regard it as an FloatField");
        System.err.println("");

        for(int i = 1; i < 6; i++) {
            addNumberRangeDateDocument(i);
        }

        TEST_INDEXER.getIndexWriter().commit();

        IndexReader reader = TEST_INDEXER.getIndexReader();
        TEST_SEARCHER = new IndexSearcher(reader);
        TEST_PERFORM_NUM = 0;
        TEST_PARSER = new StandardQueryParser(TEST_ANALYZER);
        TEST_PARSER.setNumericConfigMap(nums);

        System.err.println("");
        System.err.println("-- Queries --");

        System.err.println("All queries are performed for all fields except document_id");
        System.err.println("All queries are performed with + in their field clause meaning that the query is a MUST query (document must contain result)");
        System.err.println("All query results contain the number of results and the ID's of the documents that contain a hit.");
        System.err.println("Results are in following order: ");
        System.err.println("Result 1: int1");
        System.err.println("Result 2: int2");
        System.err.println("Result 3: int3");
        System.err.println("Result 4: int4");
        System.err.println("Result 5: float1");
        System.err.println("Result 6: float2");
        System.err.println("Result 7: float3");
        System.err.println("Result 8: float4");

        System.err.println("");
        System.err.println("-- Results --");

        performNumberRangeTestQuery("{1.5 3.5}");
        performNumberRangeTestQuery("[1.5 3.5]");
        performNumberRangeTestQuery("{1,5 3,5}");
        performNumberRangeTestQuery("[1,5 3,5]");

        /*TEST_PARSER.setAllowLeadingWildcard(true);
        System.err.println("Enable allow leading wildcard");
        performNumberRangeTestQuery("*.5");
        TEST_PARSER.setAllowLeadingWildcard(false);
        System.err.println("Disable allow leading wildcard");*/
    }

    private void addNumberRangeDateDocument(int i) throws Exception {
        Document document = new Document();
        document.add(new IntField("document_id", ++TEST_DOCUMENT_ID, Field.Store.YES));
        document.add(new TextField("int1", i+"", Field.Store.NO));
        document.add(new TextField("int2", i+"", Field.Store.NO));
        document.add(new IntField("int3", i, Field.Store.NO));
        document.add(new IntField("int4", i, Field.Store.NO));
        document.add(new TextField("float1", (i*1.0f+0.5f)+"", Field.Store.NO));
        document.add(new TextField("float2", (i*1.0f+0.5f)+"", Field.Store.NO));
        document.add(new FloatField("float3", (i*1.0f+0.5f), Field.Store.NO));
        document.add(new FloatField("float4", (i*1.0f+0.5f), Field.Store.NO));
        System.err.println("New document: ID ("+TEST_DOCUMENT_ID+") | Integer ("+i+") | Float ("+(i+0.5f)+")");
        TEST_INDEXER.getIndexWriter().addDocument(document, TEST_ANALYZER);
    }

    private void performNumberRangeTestQuery(String query) throws Exception {
        if(!StringUtils.hasText(query)) {
            return;
        }
        System.err.println("Performing query: "+query);
        TEST_PERFORM_NUM = 0;

        performNumberRangeTestQuery(query, "int1");
        performNumberRangeTestQuery(query, "int2");
        performNumberRangeTestQuery(query, "int3");
        performNumberRangeTestQuery(query, "int4");
        performNumberRangeTestQuery(query, "float1");
        performNumberRangeTestQuery(query, "float2");
        performNumberRangeTestQuery(query, "float3");
        performNumberRangeTestQuery(query, "float4");
    }

    private void performNumberRangeTestQuery(String query, String field) throws Exception {
        try {
            performTestQuery("+"+field+":"+query, "document_id");
        } catch(Exception e) {
            System.err.println("Could not perform query on field: "+field);
        }
    }

    @Test
    public void phraseTest() throws Exception {
        TEST_INDEXER = manager.getIndexDirectory(DirectoryManager.formPath(true, IndexerConfigurationType.DUMMY), true);
        Map<String, Analyzer> analyzers = new HashMap<>();
        analyzers.put("phrase1", new FinnishVoikkoAnalyzer());
        analyzers.put("phrase2", new KeywordAnalyzer());
        analyzers.put("phrase4", new KeywordAnalyzer());

        TEST_ANALYZER = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(LuceneConfig.USED_VERSION), analyzers);

        System.err.println("-- Analyzer --");
        System.err.println("Documents are analyzed using WhiteSpaceAnalyzer as the default analyzer, using FinnishVoikkoAnalyzer for field 'phrase1' and using KeywordAnalyzer of field 'phrase2'");
        System.err.println("");
        System.err.println("-- Data --");
        System.err.println("All documents contain the following fields:");
        System.err.println("phrase1 which is a finnish voikko analyzed text field");
        System.err.println("phrase2 which is a keyword analyzed text field");
        System.err.println("phrase3 which is a whitespace analyzed text field");
        System.err.println("phrase4 which is a string field and defined having keyword analyzer");
        System.err.println("phrase5 which is a string field");
        System.err.println("");

        addPhraseDocument("testi fraasi");
        addPhraseDocument("testi, fraasi");
        addPhraseDocument("testi: fraasi, teksti");
        addPhraseDocument("kolme,. välimerkkiä!");

        TEST_INDEXER.getIndexWriter().commit();

        IndexReader reader = TEST_INDEXER.getIndexReader();
        TEST_SEARCHER = new IndexSearcher(reader);
        TEST_PERFORM_NUM = 0;
        TEST_PARSER = new StandardQueryParser(TEST_ANALYZER);

        System.err.println("");
        System.err.println("-- Queries --");

        System.err.println("All queries are performed for all fields except document_id");
        System.err.println("All queries are performed with + in their field clause meaning that the query is a MUST query (document must contain result)");
        System.err.println("All query results contain the number of results and the ID's of the documents that contain a hit.");
        System.err.println("Results are in following order: ");
        System.err.println("Result 1: phrase1");
        System.err.println("Result 2: phrase2");
        System.err.println("Result 3: phrase3");
        System.err.println("Result 4: phrase4");
        System.err.println("Result 5: phrase5");

        System.err.println("");
        System.err.println("-- Results --");

        performPhraseTestQuery("fraasi");
        performPhraseTestQuery("fraasi*");
        performPhraseTestQuery("\"fraasi\"");

        /*TEST_PARSER.setAllowLeadingWildcard(true);
        System.err.println("Enable allow leading wildcard");

        TEST_PARSER.setAllowLeadingWildcard(false);
        System.err.println("Disable allow leading wildcard");*/
    }

    private void addPhraseDocument(String phrase) throws Exception {
        Document document = new Document();
        document.add(new IntField("document_id", ++TEST_DOCUMENT_ID, Field.Store.YES));
        document.add(new TextField("phrase1", phrase, Field.Store.NO));
        document.add(new TextField("phrase2", phrase, Field.Store.NO));
        document.add(new TextField("phrase3", phrase, Field.Store.NO));
        document.add(new StringField("phrase4", phrase, Field.Store.NO));
        document.add(new StringField("phrase5", phrase, Field.Store.NO));
        System.err.println("New document: ID ("+TEST_DOCUMENT_ID+") | Phrase ("+phrase+")");
        TEST_INDEXER.getIndexWriter().addDocument(document, TEST_ANALYZER);
    }

    private void performPhraseTestQuery(String query) throws Exception {
        if(!StringUtils.hasText(query)) {
            return;
        }
        System.err.println("Performing query: "+query);
        TEST_PERFORM_NUM = 0;

        performPhraseTestQuery(query, "phrase1");
        performPhraseTestQuery(query, "phrase2");
        performPhraseTestQuery(query, "phrase3");
        performPhraseTestQuery(query, "phrase4");
        performPhraseTestQuery(query, "phrase5");
    }

    private void performPhraseTestQuery(String query, String field) throws Exception {
        try {
            performTestQuery("+"+field+":"+query, "document_id");
        } catch(Exception e) {
            System.err.println("Could not perform query on field: "+field);
        }
    }

    private static DirectoryInformation TEST_INDEXER;
    private static Analyzer TEST_ANALYZER;
    private static int TEST_DOCUMENT_ID = 0;
    private static int TEST_PERFORM_NUM = 0;
    private static IndexSearcher TEST_SEARCHER = null;
    private static StandardQueryParser TEST_PARSER;



    private void performTestQuery(String query) throws IOException, QueryNodeException {
        performTestQuery(query, null);
    }

    private void performTestQuery(String query, String displayField) throws IOException, QueryNodeException {
        Query q = TEST_PARSER.parse(query, "none");
        TopDocs hits = TEST_SEARCHER.search(q, 100);
        String docList = "";
        if(displayField != null) {
            if(hits.totalHits > 0) {
                docList += "[";
                boolean first = true;
                for(ScoreDoc sd : hits.scoreDocs) {
                    if(first) {
                        first = false;
                    } else {
                        docList += ", ";
                    }
                    Document doc = TEST_SEARCHER.doc(sd.doc);
                    docList += doc.get(displayField);
                }
                docList += "]";
            }
        }
        System.err.println("Result " + ++TEST_PERFORM_NUM+": "+hits.totalHits + " " +docList);

    }

    private void printSearchResult(IndexSearcher searcher, Query qr) throws IOException {
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(qr, 100);
        System.err.println("Query performed in "+(System.currentTimeMillis()-start)+"ms");

        System.err.println("Hits: " + hits.totalHits);
        System.err.println("PageId (Title)");
        for(ScoreDoc doc : hits.scoreDocs) {
            Document document = searcher.doc(doc.doc);
            /*for(IndexableField field : document.getFields()) {
                System.err.println("["+field.name()+"] "+field.stringValue());
            }*/
            /*for(int i = 1; i <= 4; i++) {
                for(int j = 1; j <= 10; j++) {
                    System.err.println("["+i+","+j+"] "+document..get("topic"+i+".word"+j));
                }
            }*/
            System.err.println(document.get("pageId")+"("+document.get("title")+")");
        }

    }
}
