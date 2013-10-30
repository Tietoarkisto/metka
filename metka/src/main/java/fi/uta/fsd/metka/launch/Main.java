package fi.uta.fsd.metka.launch;

import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 10/30/13
 * Time: 9:01 AM
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();

        // Resolve from configuration later.
        String webappDirLocation = "src/main/webapp/";
        tomcat.setPort(8080);

        // Add webapp
        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();
    }
}
