package fi.uta.fsd.metkaAmqp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring singleton for sending status messages to different
 */
@Component
public class Messenger {

    @Value("${rabbit.server}")
    private String RABBIT_SERVER_HOST;

    @Value("${rabbit.port}")
    private int RABBIT_SERVER_PORT;

    @Value("${rabbit.user}")
    private String RABBIT_USERNAME;

    @Value("${rabbit.password}")
    private String RABBIT_PASSWORD;

    private final Map<AmqpMessageType, MetkaAmqpMessage> amqpMessages = new HashMap<>();

    @PostConstruct
    private void initAmqpMessages() {
        amqpMessages.put(AmqpMessageType.TEST, new TestMessage());
        amqpMessages.put(AmqpMessageType.STUDY_ERROR_POINTS_OVER_TRESHOLD, new StudyErrorPointWarningMessage());
    }

    public void sendAmqpMessage(AmqpMessageType type, Object... parameters) {
        MetkaAmqpMessage message = amqpMessages.get(type);
        AmqpMessenger messenger = getAmqpMessenger();
        message.send(messenger, parameters);
        messenger.clean();
    }

    private AmqpMessenger getAmqpMessenger() {
        AmqpMessenger amqpMessenger = new AmqpMessenger();
        amqpMessenger.init(RABBIT_SERVER_HOST, RABBIT_SERVER_PORT, RABBIT_USERNAME, RABBIT_PASSWORD);
        amqpMessenger.logState();
        return amqpMessenger;
    }

    public static enum AmqpMessageType {
        TEST,       // Sends a test message
        STUDY_ERROR_POINTS_OVER_TRESHOLD;
    }

    // Clean up



    /*private class FailOverRetry extends TimerTask {
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
    }*/

    /*public void critical (Object object, String message) {critical(object, message, System.out);}

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
    }*/

}
