package fi.uta.fsd.metkaSearch.commands.searcher.series;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.search.Query;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides information and query necessary for checking series abbreviation uniqueness.
 * This class as with most search commands is final since
 *
 * // TODO: general field uniqueness checker for both inside a revisionable and between revisionables. This implementation is really just a test case.
 */
public final class SeriesBasicSearchCommand extends RevisionSearchCommandBase<RevisionResult> {
    public static SeriesBasicSearchCommand build(boolean allowApproved, boolean allowDraft, boolean allowRemoved,
                                                 Long revisionableId, String abbreviation, String name) throws UnsupportedOperationException, QueryNodeException {
        //checkPath(path, ConfigurationType.SERIES);
        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, Language.DEFAULT, ConfigurationType.SERIES.toValue());
        return new SeriesBasicSearchCommand(path, allowApproved, allowDraft, allowRemoved, revisionableId, abbreviation, name);
    }

    private final Query query;
    private SeriesBasicSearchCommand(DirectoryManager.DirectoryPath path,
                                     boolean allowApproved, boolean allowDraft, boolean allowRemoved,
                                     Long revisionableId, String abbreviation, String name) throws QueryNodeException {
        super(path, ResultList.ResultType.REVISION);
        // Create new boolean query.
        // For each of allowApproved, allowDraft and allowRemoved if they are false
        // then set a new MUST condition where their respective fields must be false.
        // This allows for filtering based on status of revision or revisionable.
        // If given revisionableId is not null then add a condition where key.id must match given revisionableId
        // If abbreviation or name are non null non empty, then add MUST conditions for
        // seriesabbr and seriesname fields. These should support wildcards but must be tested.
        List<String> qrys = new ArrayList<>();
        Map<String, NumericConfig> nums = new HashMap<>();

        qrys.add(((!allowApproved)?"+":"")+"state.approved:"+allowApproved);
        qrys.add(((!allowDraft)?"+":"")+"state.draft:"+allowDraft);
        qrys.add(((!allowRemoved)?"+":"")+"state.removed:"+allowRemoved);

        if(revisionableId != null) qrys.add("+key.id:"+revisionableId);
        nums.put("key.id", new NumericConfig(1, new DecimalFormat(), FieldType.NumericType.LONG));

        if(StringUtils.hasText(abbreviation)) {
            qrys.add("+seriesabbr:"+abbreviation);
            addWhitespaceAnalyzer("seriesabbr");
        }
        if(StringUtils.hasText(name)) {
            qrys.add("+seriesname:"+name);
            addTextAnalyzer("seriesname");
        }

        String qryStr = "";
        for(String qry : qrys) {
            if(qryStr.length() > 0) {
                qryStr += " ";
            }
            qryStr += qry;
        }

        StandardQueryParser parser = new StandardQueryParser(getAnalyzer());
        parser.setNumericConfigMap(nums);
        query = parser.parse(qryStr, "general");

        //query = bQuery;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public ResultHandler<RevisionResult> getResulHandler() {
        return new BasicRevisionSearchResultHandler();
    }
}
