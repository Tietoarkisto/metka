package fi.uta.fsd.metka.mvc.validator;

import fi.uta.fsd.metka.mvc.domain.requests.UploadRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UploadRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void validate(Object target, Errors errors) {
        UploadRequest request = (UploadRequest)target;
        if(request.getFile().getSize() == 0) {
            errors.rejectValue("file", "upload.selectFile");
        }
    }
}
