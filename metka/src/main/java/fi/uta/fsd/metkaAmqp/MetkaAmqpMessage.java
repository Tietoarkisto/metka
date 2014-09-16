package fi.uta.fsd.metkaAmqp;

interface MetkaAmqpMessage {
    void send(AmqpMessenger messenger, Object... parameters);
}
