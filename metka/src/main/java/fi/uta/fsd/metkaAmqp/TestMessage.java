package fi.uta.fsd.metkaAmqp;

public class TestMessage implements MetkaAmqpMessage {
    @Override
    public void send(AmqpMessenger messenger, Object... parameters) {
        messenger.write("This is a test message");
    }
}
