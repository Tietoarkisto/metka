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

import com.rabbitmq.client.*;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.storage.collecting.ReferenceCollector;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metkaAmqp.factories.*;
import fi.uta.fsd.metkaAmqp.payloads.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
    private ReferenceCollector references;

    private JSONUtil json; // Set during constructor

    public final MetkaMessageType<TestPayload> F0_TEST;
    public final MetkaMessageType<AuditPayload> FA_AUDIT;
    public final MetkaMessageType<StudyPayload> FB_ERROR_SCORE;
    public final MetkaMessageType<AipCompletePayload> FB_AIP;
    public final MetkaMessageType<FileMissingPayload> FB_FILES_MISSING;
    public final MetkaMessageType<VersionChangePayload> FB_VERSION_CHANGES;
    public final MetkaMessageType<FileRemovalPayload> FB_FILE_REMOVAL;
    public final MetkaMessageType<ContractTriggerPayload> FB_CONTRACT_TRIGGER;
    public final MetkaMessageType<RevisionPayload> FD_CREATE;
    public final MetkaMessageType<RevisionPayload> FD_UPDATE;
    public final MetkaMessageType<RevisionPayload> FD_REMOVE;
    public final MetkaMessageType<RevisionPayload> FD_DRAFT;
    public final MetkaMessageType<RevisionPayload> FD_APPROVE;
    public final MetkaMessageType<RevisionPayload> FD_CLAIM;
    public final MetkaMessageType<RevisionPayload> FD_RELEASE;
    public final MetkaMessageType<RevisionPayload> FD_RESTORE;
    public final MetkaMessageType<ProcessPayload> FE_START;
    public final MetkaMessageType<RevisionPayload> FF_ALERT;

    @Autowired
    public Messenger(JSONUtil json) {
        this.json = json;
        F0_TEST = new MetkaMessageType<>("0", "TEST", new TestFactory());
        FA_AUDIT = new MetkaMessageType<>("A", "AUDIT", new AuditFactory());
        FB_ERROR_SCORE = new MetkaMessageType<>("B", "ERROR_SCORE", new StudyMessageFactory<>());
        FB_AIP = new MetkaMessageType<>("B", "AIP", new AipCompleteMessageFactory());
        FB_FILES_MISSING = new MetkaMessageType<>("B", "FILES_MISSING", new FileMissingFactory());
        FB_VERSION_CHANGES = new MetkaMessageType<>("B", "VERSION_CHANGES", new VersionChangeFactory(json));
        FB_FILE_REMOVAL = new MetkaMessageType<>("B", "FILE_REMOVAL", new FileRemovalFactory());
        FB_CONTRACT_TRIGGER = new MetkaMessageType<>("B", "CONTRACT_TRIGGER", new ContractTriggerFactory());
        FD_CREATE = new MetkaMessageType<>("D", "CREATE", new RevisionFactory());
        FD_UPDATE = new MetkaMessageType<>("D", "UPDATE", new RevisionFactory());
        FD_REMOVE = new MetkaMessageType<>("D", "REMOVE", new RevisionFactory());
        FD_DRAFT = new MetkaMessageType<>("D", "DRAFT", new RevisionFactory());
        FD_APPROVE = new MetkaMessageType<>("D", "APPROVE", new RevisionFactory());
        FD_CLAIM = new MetkaMessageType<>("D", "CLAIM", new RevisionFactory());
        FD_RELEASE = new MetkaMessageType<>("D", "RELEASE", new RevisionFactory());
        FD_RESTORE = new MetkaMessageType<>("D", "RESTORE", new RevisionFactory());
        FE_START = new MetkaMessageType<>("E", "START", new ProcessFactory());
        FF_ALERT = new MetkaMessageType<>("F", "ALERT", new RevisionFactory());
    }

    public <T extends PayloadObject> void sendAmqpMessage(MetkaMessageType<T> type, T payload) {
        MetkaMessage<T> message = new MetkaMessage<>(type, payload);
        message.send(references, json, getAmqpMessenger());
    }

    private AmqpMessenger getAmqpMessenger() {
        AmqpMessenger amqpMessenger = new AmqpMessenger();
        amqpMessenger.init(RABBIT_SERVER_HOST, RABBIT_SERVER_PORT, RABBIT_USERNAME, RABBIT_PASSWORD);
        amqpMessenger.logState();
        return amqpMessenger;
    }

    public static class AmqpMessenger {

        private ConnectionFactory factory = null;
        private Connection connection = null;
        private Channel channel = null;
        private AMQPState state = AMQPState.AMQP_STOPPED;

        void init(String host, int port, String user, String password) {
            try {
                if (factory == null) {
                    factory = new ConnectionFactory();
                }
                factory.setHost(host);
                factory.setUsername(user);
                factory.setPassword(password);
                factory.setPort(port);
                connection = factory.newConnection();

                if (!connection.isOpen()) {
                    connection = factory.newConnection();
                }
                channel = connection.createChannel();
                state = AMQPState.AMQP_READY;
            } catch(IOException ioe) {
                Logger.error(AmqpMessenger.class, "AMQP channel creation failed.", ioe);
                channel = null;
                state = AMQPState.AMQP_CONNECTION_FAILED;
            }
        }

        void clean() {
            try {
                if (channel != null && channel.isOpen()) {
                    channel.close();
                }
                if (connection != null && connection.isOpen()) {
                    connection.close();
                }
                state = AMQPState.AMQP_STOPPED;
            } catch(IOException ioe) {
                Logger.error(getClass(), "IOException during AMQP cleanup.", ioe);
            }
        }

        void logState() {
            Logger.debug(getClass(), "AMQP Messenger is currently at state: "+state);
        }

        void write(String exchange, String routingKey, byte[] message) {
            if(state == AMQPState.AMQP_READY) {
                try {
                    channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
                } catch(IOException ioe) {
                    Logger.error(getClass(), "AMQP message write failed.", ioe);
                    state = AMQPState.AMQP_CONNECTION_FAILED;
                    logState();
                }
            } else {
                Logger.error(getClass(), "AMQP messenger is not in READY state, instead being in state: "+state);
            }
        }

        private static enum AMQPState {
            AMQP_READY,
            AMQP_CONNECTION_FAILED,
            AMQP_STOPPED
        }
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
