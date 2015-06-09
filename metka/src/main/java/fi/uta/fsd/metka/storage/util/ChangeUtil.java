package fi.uta.fsd.metka.storage.util;

import fi.uta.fsd.metka.model.data.change.*;
import fi.uta.fsd.metka.model.data.container.*;

import java.util.Map;

/**
 * Provides unified functionality to manipulate the change objects in data-json
 */
public class ChangeUtil {
    // Util class, disable inheritance
    private ChangeUtil() {}

    public static Change getChange(Map<String, Change> changes, String key) {
        return changes.get(key);
    }

    public static void putChange(Map<String, Change> changes, Change change) {
        changes.put(change.getKey(), change);
    }

    public static void insertChange(Map<String, Change> changes, RowContainerDataField target, ContainerRow row) {
        ContainerChange cc;
        if(getChange(changes, target.getKey()) == null) {
            cc = new ContainerChange(target.getKey());
            putChange(changes, cc);
        } else {
            cc = (ContainerChange)getChange(changes, target.getKey());
        }

        if(cc.get(row.getRowId()) == null) {
            cc.put(new RowChange(row.getRowId()));
        }
    }
}
