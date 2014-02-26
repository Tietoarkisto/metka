package fi.uta.fsd.metka.mvc.domain.simple.transfer;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ContainerFieldChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.ContainerFieldContainer;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.container.RowContainer;

import java.util.ArrayList;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

public class ContainerTransfer {
    private String key;
    private Integer nextRowId;
    private final List<RowTransfer> rows = new ArrayList<>();


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getNextRowId() {
        return nextRowId;
    }

    public void setNextRowId(Integer nextRowId) {
        this.nextRowId = nextRowId;
    }

    public List<RowTransfer> getRows() {
        return rows;
    }

    /**
     * Convert a top level field into a ContainerTransfer object.
     * This method is a factory for building unknown information into a ContainerTransfer object and uses many
     * helper methods to achieve this. Due to the recursive nature of CONTAINER fields some of these methods
     * are accessed by other factories as well (namely RowTransfer factories).
     * @param data RevisionData being converted
     * @param key Field key of the requested CONTAINER field
     * @param config Configuration for the RevisionData being manipulated
     * @return ContainerTransfer object containing everything needed by UI
     */
    public static ContainerTransfer buildContainerTransfer(RevisionData data, String key, Configuration config) {
        ContainerTransfer t = null;
        if(data.getState() == RevisionState.DRAFT) {
            t = containerTransferFromDraft(data, key, config);
        } else {
            t = containerTransferFromApproved(data, key, config);
        }
        return t;
    }

    private static ContainerTransfer containerTransferFromDraft(RevisionData data, String key, Configuration config) {
        if(data.getState() != RevisionState.DRAFT) {
            // This should be impossible to reach but why not check anyway
            return null;
        }
        ContainerFieldChange change = getContainerFieldChangeFromRevisionData(data, key, config);
        if(change == null) {
            return null;
        }
        ContainerTransfer t = changeToTransfer(change);
        return t;
    }

    private static ContainerTransfer containerTransferFromApproved(RevisionData data, String key, Configuration config) {
        if(data.getState() == RevisionState.DRAFT) {
            // This should be impossible to reach but why not check anyway
            return null;
        }
        ContainerFieldContainer container = getContainerFieldContainerFromRevisionData(data, key, config);
        if(container == null) {
            return null;
        }
        ContainerTransfer t = containerToTransfer(container);
        return t;
    }

    static ContainerTransfer changeToTransfer(ContainerFieldChange container) {
        ContainerTransfer t = new ContainerTransfer();
        t.setKey(container.getKey());
        t.setNextRowId(container.getNextRowId());
        for(RowChange row : container.getRows()) {
            t.getRows().add(RowTransfer.buildRowTransferFromRowChange(row));
        }
        return null;
    }

    static ContainerTransfer containerToTransfer(ContainerFieldContainer container) {
        ContainerTransfer t = new ContainerTransfer();
        t.setKey(container.getKey());
        t.setNextRowId(container.getNextRowId());
        for(RowContainer row : container.getRows()) {
            t.getRows().add(RowTransfer.buildRowTransferFromRowContainer(row));
        }
        return null;
    }
}
