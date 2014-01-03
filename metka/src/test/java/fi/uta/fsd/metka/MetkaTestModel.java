package fi.uta.fsd.metka;

import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:testApplicationContext.xml"})
public class MetkaTestModel {

    @Autowired
    protected DomainFacade facade;

	@Before
	public void before() {
		
	}
}
