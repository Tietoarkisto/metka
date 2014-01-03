package fi.uta.fsd.metka.service;

import fi.uta.fsd.metka.MetkaTestModel;
import fi.uta.fsd.metka.data.entity.ConfigurationEntity;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.Assert.*;

public class StudyServiceTest extends MetkaTestModel {

	@Test
	public void test() throws Exception {

        int studiesSize = facade.listAllStudies().size();
        int revSize = facade.listAllRevisions().size();

        StudyEntity study = facade.createStudy();

        List<StudyEntity> studies = facade.listAllStudies();
        List<RevisionEntity> revisions = facade.listAllRevisions();

        System.err.println("New studies size: " + studies.size());
        System.err.println("New revisions size: " + revisions.size());

        assertEquals(studies.size(), studiesSize+1);
        assertEquals(revisions.size(), revSize+1);

        facade.addDraft(study);

        revisions = facade.listAllRevisions();

        System.err.println("New revisions size: " + revisions.size());
        assertEquals(revisions.size(), revSize+1);

        facade.approveDraft(revisions.get(0));
        assertEquals(revisions.get(0).getState(), RevisionEntity.RevisionState.APPROVED);
        facade.addDraft(study);

        revisions = facade.listAllRevisions();

        System.err.println("New revisions size: " + revisions.size());
        assertEquals(revSize+2, revisions.size());
	}

}
