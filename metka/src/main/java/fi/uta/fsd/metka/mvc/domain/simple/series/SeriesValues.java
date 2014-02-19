package fi.uta.fsd.metka.mvc.domain.simple.series;

enum SeriesValues {
    SERIESNO("seriesno"),
    SERIESABB("seriesabb"),
    SERIESNAME("seriesname"),
    SERIESDESC("seriesdesc"),
    SERIESNOTES("seriesnotes");
    private String key;

    SeriesValues(String key) {
        this.key = key;
    }

    String getKey() {
        return this.key;
    }

    static SeriesValues fromString(String key) {
        if(key != null) {
            for(SeriesValues v : SeriesValues.values()) {
                if(key.equals(v.key)) {
                    return v;
                }
            }
        }
        throw new IllegalArgumentException("No value for ["+key+"] found.");
    }
}
