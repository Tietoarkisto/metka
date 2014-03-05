package fi.uta.fsd.metka.data.enums;

public enum UIRevisionState {
    DRAFT, APPROVED, REMOVED;

    public static UIRevisionState fromRevisionState(RevisionState state) throws IllegalArgumentException {
        switch(state) {
            case DRAFT:
                return UIRevisionState.DRAFT;
            case APPROVED:
                return UIRevisionState.APPROVED;
        }
        throw new IllegalArgumentException();
    }
}
