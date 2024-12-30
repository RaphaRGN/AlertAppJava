package com.raphaelprojetos.sentinel.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertaConsumer {

    private static final String NOME_QUEUE = "alertasQueue";
    private static final Logger LOGGER = Logger.getLogger(AlertaConsumer.class.getName());
    private final Channel channel;

    public AlertaConsumer() throws Exception {
        LOGGER.info("Iniciando o consumidor de alertas...");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection()) {
            this.channel = connection.createChannel();
            LOGGER.info("Conexão com RabbitMQ estabelecida.");

            channel.queueDeclare(NOME_QUEUE, true, false, false, null);
            LOGGER.info("Fila '" + NOME_QUEUE + "' declarada com sucesso.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao iniciar o consumidor de alertas: " + e.getMessage(), e);
            throw e;
        }

        LOGGER.info("Consumidor de alertas iniciado com sucesso.");
    }
}
