package fi.uta.fsd.metka.model.interfaces;

import fi.uta.fsd.metka.model.data.change.Change;

import java.util.Map;

public interface Row {
    /**
     * Used to mark given row as removed and make a change note if missing
     * @param changeMap Map that should contain the change for container where given row is present
     */
    public void remove(Map<String, Change> changeMap);
}
