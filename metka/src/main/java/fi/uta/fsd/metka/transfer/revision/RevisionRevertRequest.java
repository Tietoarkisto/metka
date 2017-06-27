package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.model.general.RevisionKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by juusoko on 15.6.2017.
 * juuso.korhonen@visma.com
 */
public class RevisionRevertRequest {
    private RevisionKey key;

    private Integer targetNo;

    private final Map<String, String> values = new HashMap<>();

    public RevisionKey getKey() {
        return key;
    }

    public void setKey(RevisionKey key) {
        this.key = key;
    }

    public Integer getTargetNo() {
        return targetNo;
    }

    public void setTargetNo(Integer targetNo) {
        this.targetNo = targetNo;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getByKey(String key) {
        return values.containsKey(key) ? values.get(key) : null;
    }
}
