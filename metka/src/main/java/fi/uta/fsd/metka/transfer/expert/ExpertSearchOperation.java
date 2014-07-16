package fi.uta.fsd.metka.transfer.expert;

public enum ExpertSearchOperation {
    QUERY,  // Perform expert search query and return the results
    SAVE,   // Save given expert search with given title
    LIST,   // Return all saved expert searches. This contains the saved queries.
    REMOVE  // Remove requested saved expert search
}
