package fi.uta.fsd.metka.transfer.expert;

public class ExpertSearchRequest {
    private ExpertSearchOperation operation;
    private String data;

    public ExpertSearchOperation getOperation() {
        return operation;
    }

    public void setOperation(ExpertSearchOperation operation) {
        this.operation = operation;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static enum ExpertSearchOperation {
        QUERY,  // Perform expert search query and return the results
        SAVE,   // Save given expert search with given title
        LOAD,   // Return requested expert search query
        REMOVE  // Remove requested saved expert search
    }
}
