package fi.uta.fsd.metka.mvc.domain.simple;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessage {


    private String title = null;
    private String msg;
    private final List<String> data = new ArrayList<String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getData() {
        return data;
    }

    // Static factory methods
    public static ErrorMessage noResults(String type) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.search.noResult");
        error.getData().add("general.errors.search.noResult."+type);

        return error;
    }

    public static ErrorMessage saveSuccess() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("alert.notice.save.success");

        return error;
    }

    public static ErrorMessage saveFail() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("alert.error.save.fail");

        return error;
    }

    public static ErrorMessage approveSuccess() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("alert.notice.approve.success");

        return error;
    }

    public static ErrorMessage approveFailSave() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("alert.error.approve.fail.save");

        return error;
    }

    public static ErrorMessage approveFailValidate() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("alert.error.approve.fail.validate");

        return error;
    }

    public static ErrorMessage noSuchRevision(String type, Long id, Integer revision) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.revision.noRevision");
        error.getData().add("general.errors.revision.noRevision."+type);
        error.getData().add(id+"");
        error.getData().add(revision+"");

        return error;
    }

    public static ErrorMessage noConfigurationForRevision(String type, Long id, Integer revision) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.revision.noConfigurationForRevision");
        error.getData().add("general.errors.revision.noConfigurationForRevision."+type);
        error.getData().add(id+"");
        error.getData().add(revision+"");

        return error;
    }

    public static ErrorMessage configurationSerializationError(String type, Long id, Integer revision) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.revision.configurationSerializationError");
        error.getData().add("general.errors.revision.configurationSerializationError."+type);
        error.getData().add(id+"");
        error.getData().add(revision+"");

        return error;
    }

    public static ErrorMessage guiConfigurationSerializationError(String type, Long id, Integer revision) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.revision.guiConfigurationSerializationError");
        error.getData().add("general.errors.revision.guiConfigurationSerializationError."+type);
        error.getData().add(id+"");
        error.getData().add(revision+"");

        return error;
    }

    public static ErrorMessage noViewableRevision(String type, Long id) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.revision.noViewableRevision");
        error.getData().add("general.errors.revision.noViewableRevision."+type);
        error.getData().add(id+"");

        return error;
    }

    public static ErrorMessage studyAttachmentSaveAndApproveSuccesss() {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.studyAttachment.saveSuccess");

        return error;
    }

    public static ErrorMessage studyAttachmentSaveAndApproveFail(Exception ex) {
        ErrorMessage error = new ErrorMessage();
        error.setMsg("general.errors.studyAttachment.saveFail");
        error.getData().add(ex.toString());

        return error;
    }
}
