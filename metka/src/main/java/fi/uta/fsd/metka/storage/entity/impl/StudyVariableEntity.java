package fi.uta.fsd.metka.storage.entity.impl;

import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.enums.ConfigurationType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity class for study variable revisionable objects.
 * Unlike most of the revisionable entities this contains additional information namely the id of a
 * study variables revisionable object this variable belongs to and id of the variable saved using this object.
 * These are exclusively used to speed up data manipulation operations and should not be used outside of variable parsing.
 * None of these fields are key fields (primary or foreign) and are assumed to be created when the study variable is created.
 * All of these fields are immutable since they are integral part of single variables identity (even if they are not keys).
 * STUDY_ID what study this variable ultimately belongs to. Since this is immutable information and it's useful to know it makes sense
 * to have it on the study variable as well
 * STUDY_VARIABLES_ID what study variables collection this variable belongs to.
 * VARIABLE_ID this is the information gathered from the actual variable file. It's one of the first things determined and it's used to
 * identify variables when merging new variable files.
 */
@Entity
@DiscriminatorValue(ConfigurationType.Values.STUDY_VARIABLE)
public class StudyVariableEntity extends RevisionableEntity {
    @Column(name = "STUDY_ID", updatable = false)
    private Long studyId;

    @Column(name = "STUDY_VARIABLES_ID", updatable = false)
    private Long studyVariablesId;

    @Column(name = "VARIABLE_ID", updatable = false)
    private String variableId;

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Long getStudyVariablesId() {
        return studyVariablesId;
    }

    public void setStudyVariablesId(Long studyVariablesId) {
        this.studyVariablesId = studyVariablesId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }
}
