package fi.uta.fsd.metka.service;

import fi.uta.fsd.metka.messaging.AmqpConnector;
import fi.uta.fsd.metka.messaging.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.OutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Company: Proactum Oy
 * User: Eemu Bertling
 * Date: 19.12.2013
 * Time: 14:38
 */
public class LoggerTest {

    private class OutputTestStream extends OutputStream {
        protected StringBuffer buf = new StringBuffer();

        @Override
        public void write(byte[] b) {
            String str = new String(b);
            this.buf.append(str);
        }

        public void write(byte[] b, int off, int len) {
            String str = new String(b, off, len);
            this.buf.append(str);
        }

        public void write(int b) {
            String str = Integer.toString(b);
            this.buf.append(str);
        }

        public String toString() {
            return buf.toString();
        }
    }

    @Test
    @Ignore
    public void TestLogSuccess() {
        OutputTestStream stream = new OutputTestStream();

        Logger logger = Logger.getInstance();
        logger.info(this,"Logger test.",stream);
        assertFalse(stream.toString().contains("Logger test."));
    }

    @Test
    @Ignore
    public void TestLogFail() {
        String rabbitIP = AmqpConnector.getAmqpHost();
        AmqpConnector.setAmqpHost("127.0.0.0");

        OutputTestStream stream = new OutputTestStream();
        Logger logger = Logger.getInstance();
        logger.info(this,"Logger test.",stream);

        AmqpConnector.setAmqpHost(rabbitIP);

        assertTrue(stream.toString().contains("Logger test."));
    }
}
