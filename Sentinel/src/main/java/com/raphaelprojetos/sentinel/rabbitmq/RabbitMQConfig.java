package com.raphaelprojetos.sentinel.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOME_QUEUE = "alertasQueue";
    public static final String NOME_EXCHANGE = "alertasExchange";

    //Define o nome da fila como alertasQueue
    @Bean
    public Queue queue (){
        return new Queue(NOME_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange(){

        return new DirectExchange(NOME_EXCHANGE);
    }

    @Bean
    public Binding binding (Queue queue, DirectExchange exchange){

        return BindingBuilder.bind(queue).to(exchange).with("alertasRoutingKey");

    }

}
