package fi.uta.fsd.metka.model.transfer;

import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;

import java.util.ArrayList;
import java.util.List;

public class TransferValue {
    private String current = "";
    private String original = "";
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

    public Value toValue() {
        // TODO: Derived values might need to be kept here too
        return new Value(current, "");
    }

    public static TransferValue buildFromValueDataFieldFor(Language language, ValueDataField field) {
        TransferValue value = new TransferValue();
        if(field.hasOriginalFor(language)) {
            value.setOriginal(field.getOriginalFor(language).getActualValue());
        }
        if(field.hasCurrentFor(language)) {
            value.setCurrent(field.getCurrentFor(language).getActualValue());
        }
        return value;
    }
}
