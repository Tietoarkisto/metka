package fi.uta.fsd.metkaAmqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyErrorPointWarningMessage implements MetkaAmqpMessage {
    private static final Logger logger = LoggerFactory.getLogger(StudyErrorPointWarningMessage.class);
    @Override
    public void send(AmqpMessenger messenger, Object... parameters) {

    }
}