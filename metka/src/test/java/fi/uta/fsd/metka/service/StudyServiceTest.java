package fi.uta.fsd.metka.service;

import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


import static org.junit.Assert.*;

public class StudyServiceTest {//extends MetkaTestModel

	@Test
    @Ignore
	public void test() throws  Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/META-INF/spring/applicationContext.xml"
        );

        final DomainFacade f = context.getBean(DomainFacade.class);

        int size = f.listAllStudies().size();

        StudyEntity study = f.createStudy();

        List<StudyEntity> series = f.listAllStudies();

        System.err.println("New size: "+series.size());

        assertEquals(series.size(), size+1);
	}

}
