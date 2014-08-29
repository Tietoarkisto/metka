package fi.uta.fsd.metka.transfer.settings;

public class NewAPIUserRequest {
    private String name;
    private boolean hasStudyCreatePermission;
    private boolean hasSearchPermission;
    private boolean hasReadPermission;
    private boolean hasEditPermission;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasStudyCreatePermission() {
        return hasStudyCreatePermission;
    }

    public void setHasStudyCreatePermission(boolean hasStudyCreatePermission) {
        this.hasStudyCreatePermission = hasStudyCreatePermission;
    }

    public boolean isHasSearchPermission() {
        return hasSearchPermission;
    }

    public void setHasSearchPermission(boolean hasSearchPermission) {
        this.hasSearchPermission = hasSearchPermission;
    }

    public boolean isHasReadPermission() {
        return hasReadPermission;
    }

    public void setHasReadPermission(boolean hasReadPermission) {
        this.hasReadPermission = hasReadPermission;
    }

    public boolean isHasEditPermission() {
        return hasEditPermission;
    }

    public void setHasEditPermission(boolean hasEditPermission) {
        this.hasEditPermission = hasEditPermission;
    }
}
