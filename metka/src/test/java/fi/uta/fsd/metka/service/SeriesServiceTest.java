package fi.uta.fsd.metka.service;

import fi.uta.fsd.metka.data.deprecated.SeriesEntity;
import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 11/20/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesServiceTest {
    @Test
    @Ignore
    public void test() throws  Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/META-INF/spring/applicationContext.xml"
        );

        final DomainFacade f = context.getBean(DomainFacade.class);

        int size = f.listAllSeries().size();

        ExecutorService s = Executors.newSingleThreadExecutor();
        Future<?> future = s.submit(new Runnable() {
            @Override
            public void run() {
                f.createSeries(new SeriesEntity());
            }
        });

        future.get();
        s.shutdownNow();


        List<SeriesEntity> series = f.listAllSeries();
        assertEquals(series.size(), size+1);
    }
}
