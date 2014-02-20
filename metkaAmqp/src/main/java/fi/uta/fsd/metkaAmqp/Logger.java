package fi.uta.fsd.metkaAmqp;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Company: Proactum Oy
 * User: Eemu Bertling
 * Date: 18.12.2013
 * Time: 14:31
 */

public class Logger {

    // Logging method
    public static enum LoggingMethod {
        AMQP,
        AMQP_FAILOVER,
        SYSLOG
    }

    // Default logging method
    private LoggingMethod loggingMethod = LoggingMethod.SYSLOG;

    private Logger() {
    }

    private static Logger logger = null;

    // Logger instance getter. Use this.
    public static Logger getInstance() {
        return getInstance(System.out);
    }

    // Logger instance getter for debug and testcases.
    public static Logger getInstance(OutputStream failSafeLog) {
        if (logger == null) logger = new Logger();
        if (!logger.isInitialized) logger.Init(failSafeLog);
        return logger;
    }

    private Channel channel = null;
    private boolean isInitialized = false;
    private Toolkit toolkit = null;
    private Timer timer = null;

    // Initialize RabbitMQ connection
    private void Init(OutputStream failSafeLog) {
        try {
            channel = AmqpConnector.getChannel();
            loggingMethod = LoggingMethod.AMQP;
        } catch (Exception e) {
            safeLog(failSafeLog, "Unable to initialize RabbitMQ connection!");
        }
        isInitialized = true;
    }

    private void WriteAmqpLog(String routingKey, String message) throws IOException {
        channel.basicPublish("systemlog_exchange", routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    }

    private class FailOverRetry extends TimerTask {
        public void run() {
            System.out.println("Retry AMQP connection.");
            Init(System.out);
            if (loggingMethod != LoggingMethod.AMQP_FAILOVER) timer.cancel();
        }
    }

    private String objectName(Object object){
        String className = null;
        try {
            className = object.getClass().getName();
        } catch (Exception e) {}
        if (className == null) {
            className = "null";
        }
        return className;
    }

    public void critical (Object object, String message) {critical(object, message, System.out);}

    public void critical (Object object, String message, OutputStream failSafeLog) {
        String routingKey = "log." + objectName(object) + ".critical";
        log(routingKey, message, failSafeLog);
    }

    public void error (Object object, String message) {error(object, message, System.out);}

    public void error (Object object, String message, OutputStream failSafeLog) {
        String routingKey = "log." + objectName(object) + ".error";
        log(routingKey, message, failSafeLog);
    }

    public void warning (Object object, String message) {warning(object, message, System.out);}

    public void warning (Object object, String message, OutputStream failSafeLog) {
        String routingKey = "log." + objectName(object) + ".varning";
        log(routingKey, message, failSafeLog);
    }

    public void debug (Object object, String message) {debug(object, message, System.out);}

    public void debug (Object object, String message, OutputStream failSafeLog) {
        String routingKey = "log." + objectName(object) + ".debug";
        log(routingKey, message, failSafeLog);
    }

    public void info (Object object, String message) {info(object, message, System.out);}

    public void info (Object object, String message, OutputStream failSafeLog) {
        String routingKey = "log." + objectName(object) + ".info";
        log(routingKey, message, failSafeLog);
    }

    private void safeLog(OutputStream stream, String log){
        if(stream == null) stream = System.out;
        try {
            stream.write(log.getBytes());
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String routingKey, String message, OutputStream failSafeLog) {
        if(loggingMethod == LoggingMethod.AMQP)
        {
            if(isInitialized == false || (channel == null) || !channel.isOpen()) Init(failSafeLog);
            try {
                WriteAmqpLog(routingKey, message);
            } catch (Exception e) {
                safeLog(failSafeLog, "Failed to write AMQP log!!!\nTrying to reconnect.\n" + e.toString());
                try {
                    channel.close();
                    Init(failSafeLog);
                    WriteAmqpLog(routingKey, message);
                } catch (Exception ee) {
                    safeLog(failSafeLog, "Failed to reconnect AMQP. Switching to system log failover..");
                    loggingMethod = LoggingMethod.AMQP_FAILOVER;
                    if(toolkit == null) toolkit = Toolkit.getDefaultToolkit();
                    if(timer == null) timer = new Timer();
                    timer.scheduleAtFixedRate(new FailOverRetry(),1000l,5000l);
                }
            }
        }
        if((loggingMethod == LoggingMethod.SYSLOG) || (loggingMethod == LoggingMethod.AMQP_FAILOVER)) {
            safeLog(failSafeLog, message);
        }
    }

}

