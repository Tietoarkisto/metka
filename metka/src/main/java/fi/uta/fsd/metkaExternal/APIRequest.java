package fi.uta.fsd.metkaExternal;

public abstract class APIRequest {
    private APISignature authentication;

    public APISignature getAuthentication() {
        return authentication;
    }

    public void setAuthentication(APISignature authentication) {
        this.authentication = authentication;
    }
}
