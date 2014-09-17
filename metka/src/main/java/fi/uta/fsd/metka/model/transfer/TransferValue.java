package fi.uta.fsd.metka.model.transfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TransferValue {
    private String current = null;
    private String original = null;
    private final List<FieldError> errors = new ArrayList<>();

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    @JsonIgnore public boolean hasOriginal() {
        return StringUtils.hasText(original);
    }

    @JsonIgnore public boolean hasCurrent() {
        return StringUtils.hasText(current);
    }

    @JsonIgnore public boolean hasValue() {
        return hasCurrent() || hasOriginal();
    }

    public void addError(FieldError error) {
        boolean found = false;
        for(FieldError e : errors) {
            if(e == error) {
                found = true;
                break;
            }
        }

        if(!found) {
            errors.add(error);
        }
    }

    public static TransferValue buildFromValueDataFieldFor(Language language, ValueDataField field) {
        TransferValue value = new TransferValue();
        if(field.hasOriginalFor(language)) {
            value.setOriginal(field.getOriginalFor(language).getActualValue());
        }
        if(field.hasCurrentFor(language)) {
            value.setCurrent(field.getCurrentFor(language).getActualValue());
        }
        if(value.getCurrent() == null && value.getOriginal() == null) {
            return null;
        }
        return value;
    }
}
