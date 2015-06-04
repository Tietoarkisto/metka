package fi.uta.fsd.metkaAmqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import fi.uta.fsd.Logger;

import java.io.IOException;

class AmqpMessenger {

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

        void write(String message) {
            if(state == AMQPState.AMQP_READY) {
                try {
                    // TODO: Move routing key to message
                    channel.basicPublish("metka.test", "metka.queue", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
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