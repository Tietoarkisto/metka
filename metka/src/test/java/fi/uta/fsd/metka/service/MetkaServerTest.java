package fi.uta.fsd.metka.service;

import com.rabbitmq.client.QueueingConsumer;
import org.junit.Ignore;
import org.junit.Test;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Company: Proactum Oy
 * User: Eemu Bertling
 * Date: 17.12.2013
 * Time: 10:59
 */
public class MetkaServerTest {

    ConnectionFactory factory = null;

    private final static String QUEUE_NAME = "MetkaTest";
    private final static String MESSAGE = "Test message";

    private final static String RABBIT_SERVER_HOST = "10.81.0.169";

    // Basic test of RabbitMQ connection,

    @Ignore
    @Test
    public void testChannel() throws  Exception {
        // Test connection
        System.out.println("Starting Rabbit connection test");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBIT_SERVER_HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        assertNotNull(channel);

        // Test creating of queue.
        System.out.println("Starting Rabbit queue test");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Test creating of message.
        System.out.println("Starting Rabbit message test");
        channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());

        // Test receiving of message.
        System.out.println("Trying to read Rabbit message");
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);

        // Read message from queue. Wait 2s for it.
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(2000l);
        System.out.println("Message received.");
        String response = new String(delivery.getBody());
        assertEquals("Return message is invalid", MESSAGE, response);

        // Cleanup
        System.out.println("Cleaning the test queue.");
        channel.queueDelete(QUEUE_NAME);
        channel.close();
        connection.close();
    }
}



