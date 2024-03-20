package com.github.paicoding.forum.core.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>date: 2024/03/20 16:49 </p>
 * <p>description:  </p>
 *
 * @author Liam
 */
@Service
public class RabbitmqUtil {
    @Autowired
    private AmqpAdmin amqpAdmin;

    public Exchange declareExchange(String exchange, BuiltinExchangeType exchangeType) {
        Exchange durableExchange;
        switch (exchangeType) {
            case DIRECT:
                durableExchange = new DirectExchange(exchange);
                break;
            case TOPIC:
                durableExchange = new TopicExchange(exchange);
                break;
            case FANOUT:
                durableExchange = new FanoutExchange(exchange);
                break;
            case HEADERS:
                durableExchange = new HeadersExchange(exchange);
                break;
            default:
                throw new IllegalArgumentException("Unsupported exchange type: " + exchangeType);
        }

        // 声明交换器
        amqpAdmin.declareExchange(durableExchange);
        return durableExchange;
    }
}