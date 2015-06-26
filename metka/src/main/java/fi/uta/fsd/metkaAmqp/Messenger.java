/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metkaAmqp;

import fi.uta.fsd.metka.mvc.services.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ReferenceService references;

    private final Map<AmqpMessageType, MetkaAmqpMessage> amqpMessages = new HashMap<>();

    @PostConstruct
    private void initAmqpMessages() {
        amqpMessages.put(AmqpMessageType.TEST, new TestMessage());
        amqpMessages.put(AmqpMessageType.METKA_MESSAGE_0, new MetkaMessage0(references));
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
        METKA_MESSAGE_0,
        METKA_MESSAGE_A,
        METKA_MESSAGE_B,
        METKA_MESSAGE_C,
        METKA_MESSAGE_D,
        METKA_MESSAGE_E
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
