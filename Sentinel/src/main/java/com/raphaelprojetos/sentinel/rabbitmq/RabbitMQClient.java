package com.raphaelprojetos.sentinel.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class RabbitMQClient {

    public static final String NOME_QUEUE = "alertasQueue";

    public void enviarALerta (String alertaRabbit) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(NOME_QUEUE, true, false, false, null);
            channel.basicPublish("", NOME_QUEUE, null, alertaRabbit.getBytes());
            System.out.println("Mensagem enviada: " + alertaRabbit);
        }
    }

}
