package fi.uta.fsd.metka.mvc.domain.simple.study;

enum StudyValues {
    STUDY_ID("study_id"),
    ID("id"),
    SUBMISSIONID("submissionid"),
    TITLE("title"),
    DATAKIND("datakind"),
    ISPUBLIC("ispublic"),
    SERIESID("seriesid");
    private String key;

    StudyValues(String key) {
        this.key = key;
    }

    String getKey() {
        return this.key;
    }

    static StudyValues fromString(String key) {
        if(key != null) {
            for(StudyValues v : StudyValues.values()) {
                if(key.equals(v.key)) {
                    return v;
                }
            }
        }
        throw new IllegalArgumentException("No value for ["+key+"] found.");
    }
}
