package fi.uta.fsd.metkaExternal;

public class APISignature {
    // Public key of the program making the request
    private String key;
    // RequestSignature created using programs api key and some other parameters
    private String signature;
    // ISO_8601 encoded timestamp of when the request was made, part of the signature creation
    private String accessTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }
}
