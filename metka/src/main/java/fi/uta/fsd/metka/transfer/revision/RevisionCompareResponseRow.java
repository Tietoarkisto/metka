package fi.uta.fsd.metka.transfer.revision;

public class RevisionCompareResponseRow {
    private final String key;
    private final String original;
    private final String current;

    public RevisionCompareResponseRow(String key, String original, String current) {
        this.key = key;
        this.original = original;
        this.current = current;
    }

    public String getKey() {
        return key;
    }

    public String getOriginal() {
        return original;
    }

    public String getCurrent() {
        return current;
    }
}
