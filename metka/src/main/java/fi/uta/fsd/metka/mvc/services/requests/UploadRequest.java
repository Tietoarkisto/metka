package fi.uta.fsd.metka.mvc.services.requests;

import org.springframework.web.multipart.MultipartFile;

public class UploadRequest {
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
