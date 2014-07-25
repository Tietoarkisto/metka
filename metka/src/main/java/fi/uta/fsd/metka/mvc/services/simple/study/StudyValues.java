package fi.uta.fsd.metka.mvc.services.simple.study;

enum StudyValues {
    STUDY_ID("study_id"),
    ID("id"),
    TITLE("title"),
    SUBMISSIONID("submissionid"),
    AIPCOMPLETE("aipcomplete"),
    DATAKIND("datakind"),
    ANONYMIZATION("anonymization"),
    SECURITYISSUES("securityissues"),
    PUBLIC("public"),
    DESCPUBLIC("descpublic"),
    VARPUBLIC("varpublic"),
    SERIESID("seriesid"),
    ORIGINALLOCATION("originallocation"),
    PROCESSINGNOTES("processingnotes")
    ;
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
