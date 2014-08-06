package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.List;

public class SeriesAbbreviationsResponse {
    private ReturnResult result;
    private List<String> abbreviations;

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<String> getAbbreviations() {
        return abbreviations;
    }

    public void setAbbreviations(List<String> abbreviations) {
        this.abbreviations = abbreviations;
    }
}
