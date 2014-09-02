package fi.uta.fsd.metka.enums;

public enum ContainerType {
    TAB,
    SECTION,
    COLUMN,
    ROW,
    CELL,
    EMPTYCELL;

    public boolean canContain(ContainerType t) {
        // We never place containers with similar types within one another
        if(this == t) {
            return false;
        }

        // Cells can't have containers
        if(this == ContainerType.CELL || this == ContainerType.EMPTYCELL) {
            return false;
        }

        // Tabs never contain rows or cells directly
        if(this == ContainerType.TAB && !(t == SECTION || t == COLUMN)) {
            return false;
        }

        // Sections can contain only column containers
        if(this == SECTION && t != COLUMN) {
            return false;
        }

        // Columns can contain only rows
        if(this == COLUMN && t != ROW) {
            return false;
        }

        // Rows can contain only cells
        if(this == ROW && !(t == CELL || t == EMPTYCELL)) {
            return false;
        }

        // This container type can contain the provided container type
        return true;
    }
}
