package fi.uta.fsd.metka.transfer.series;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class SeriesAbbreviationsResponse {
    private ReturnResult result;
    private final List<String> abbreviations = new ArrayList<>();

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<String> getAbbreviations() {
        return abbreviations;
    }
}
