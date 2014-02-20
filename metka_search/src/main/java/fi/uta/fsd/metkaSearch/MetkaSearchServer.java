package fi.uta.fsd.metkaSearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.*;

import fi.uta.fsd.metkaAmqp.AmqpConnector;
import fi.uta.fsd.metkaAmqp.Logger;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

import java.io.IOException;

/**
 * Company: Proactum Oy
 * User: Eemu Bertling
 * Date: 21.1.2014
 * Time: 10:35
 */

public class MetkaSearchServer implements Daemon {

    // TODO Read configuration from properties file
    private final static String RABBIT_SERVER_HOST = "10.81.0.169";
    private final static String SEARCH_QUEUE_NAME = "MetkaSearch";

    // Initialize logger.
    private static final Logger log = Logger.getInstance();

    Channel amqpChannel;
    private Thread luceneSearchThread;
    private boolean stopped = false;


    // Daemon controll functions
    @Override
    public void init(DaemonContext daemonContext) throws DaemonInitException, Exception {
        log.debug(this, "MetkaSearchServer initializing..");
        amqpChannel = AmqpConnector.getChannel();
        luceneSearchThread = new SearchThread();
    }

    @Override
    public void start() throws Exception {
        log.debug(this, "MetkaSearchServer starting..");
        stopped = false;
        luceneSearchThread.start();
    }

    @Override
    public void stop() throws Exception {
        log.debug(this, "MetkaSearchServer stop.");
        stopped = true;
        try {
            luceneSearchThread.join(10000);
        } catch (InterruptedException e) {
            log.error(this, e.getMessage());
            throw e;
        }
    }

    @Override
    public void destroy() {
        log.debug(this, "MetkaSearchServer destroy.");
    }


    // Search server thread.
    private class SearchThread extends Thread {
        @Override
        public synchronized void start() {
            MetkaSearchServer.this.stopped = false;
            super.start();
        }

        @Override
        public void run() {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBIT_SERVER_HOST);

            Connection connection;
            Channel channel = null;
            QueueingConsumer consumer = null;

            boolean channelOk = false;

            while(!stopped){
                try {
                    // If connection is not yet initialized or it is faulty, Make connection.
                    if (!channelOk) {
                        // Create connection
                        connection = factory.newConnection();
                        channel = connection.createChannel();
                        // If queue is not exist, create it
                        channel.queueDeclare(SEARCH_QUEUE_NAME, false, false, false, null);
                        // Priorize queue.
                        channel.basicQos(1);

                        consumer = new QueueingConsumer(channel);
                        channel.basicConsume(SEARCH_QUEUE_NAME, false, consumer);
                        // Flag connection ok.
                        channelOk = true;
                    }

                    // Wait new search delivery.
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    // Start new thread for processing that query.
                    new Thread(new SearchActionRunnable(channel, delivery)).start();

                    // If anything fails then catch it. (Server execution must recover if something fails.
                } catch (Exception e) {
                    // Flag status to not ok.
                    channelOk = false;
                    // Try to sleep a second. This prevent flooding Rabbit and Lucene.
                    try {
                        log.warning(this, "Problems with consumer..\n" + e.getMessage());
                        Thread.sleep(1000);
                    } catch (Exception ee) {
                        // If sleep fails then there is major server issues or server is shutting down.
                    }
                }
            }
        }
    }

    // Single search Thread. May have more than one running at same time.
    private class SearchActionRunnable implements Runnable {

        private QueueingConsumer.Delivery delivery = null;
        private TopScoreDocCollector collector = null;
        private String searchQuery = "";
        private Channel channel = null;
        // Hide basic constructor..
        private SearchActionRunnable(){}

        // Constructor takes in communication channel and delivered message.
        public SearchActionRunnable(Channel channel, QueueingConsumer.Delivery delivery) {
            this.delivery = delivery;
            this.channel = channel;
        }

        // And run() actually do all the search work.
        public void run() {
            // Parse incoming message.
            JsonNode message = null;
            ObjectMapper mapper = new ObjectMapper();

            BasicProperties props = delivery.getProperties();
            BasicProperties replyProps = new BasicProperties.Builder()
                    .correlationId(props.getCorrelationId())
                    .build();

            try {
                // Parse message
                message = mapper.readTree(delivery.getBody());
                searchQuery = message.get("searchQuery").asText();
            } catch (IOException e) {
                // Parse failed
                log.error(this, "Error parsing search message.\nMessage body: " + delivery.getBody() + "\n" + e.getMessage());
                try {
                    // Try to send error thu Rabbit..
                    channel.basicPublish("", props.getReplyTo(), replyProps, generateErrorJson("Failed to parse message!").getBytes());
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (IOException e1) {
                    // RabbitMQ dead? There is nothing that can be done here.
                    log.critical(this, "Cant send error response");
                }
            }
            // Introduce Lucene API
            LuceneAPI api;
            try {
                // Do the search from Lucene
                api = new LuceneAPI(LuceneAPI.IndexType.FILESYSTEM_READONLY,"tietoarkisto");
                //TODO "body" is for testing purposes only! Need to be adjusted when
                //document format is ready.
                collector =  api.findDocuments("body", searchQuery);
            } catch (IOException e) {
                log.error(this, "Can't access to lucene!\n" + e.getMessage());
            } catch (ParseException e) {
                log.error(this, "Invalid search query!\nQuery: " + searchQuery + "\n" + e.getMessage());
            } catch (Exception e) {
                log.error(this, "Lucene index problem!\n" + e.getMessage());
            }
            log.debug(this, collector.toString());

            // Organize result
            ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;

            // Introduce response string
            String json;
            try {
                json = mapper.writeValueAsString(scoreDocs);
            } catch (JsonProcessingException e) {
                json = generateErrorJson(e.getMessage());
            }
            log.debug(this, json);

            // Send the response
            try {
                channel.basicPublish("", props.getReplyTo(), replyProps, json.getBytes());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            } catch (IOException e) {
                log.warning(this, "Failed to delivery response for search query.");
            }
        }

        // JSON string generator for error responses.
        private String generateErrorJson(String errorText) {
            return "{\"error\":\""+ String.valueOf(JsonStringEncoder.getInstance().quoteAsString(errorText)) + "\"}";
        }
    }
}


