package fi.uta.fsd.metka.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * Company: Proactum Oy
 * User: Eemu Bertling
 * Date: 18.12.2013
 * Time: 15:25
 */
public class AmqpConnector {
    private static ConnectionFactory factory = null;
    private static Connection connection = null;

    // TODO Read from properties file
    private static String RABBIT_SERVER_HOST = "10.81.0.169";

    private AmqpConnector(){}

    public static String getAmqpHost(){return RABBIT_SERVER_HOST;}
    public static void setAmqpHost(String ip){
        try {
            connection.close();
        } catch (Exception e) {}
        RABBIT_SERVER_HOST = ip;
    }

    public static Channel getChannel() throws IOException {
        if (factory == null) {
            factory = new ConnectionFactory();
        }
        factory.setHost(RABBIT_SERVER_HOST);
        connection = factory.newConnection();

        if (!connection.isOpen()) {
            connection = factory.newConnection();
        }
        return connection.createChannel();
    }
}
