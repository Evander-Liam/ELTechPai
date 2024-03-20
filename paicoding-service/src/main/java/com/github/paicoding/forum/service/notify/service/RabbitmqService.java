package com.github.paicoding.forum.service.notify.service;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author YiHui
 * @date 2022/9/3
 */
public interface RabbitmqService {

    boolean enabled();

    /**
     * 发布消息
     *
     * @param exchange
     * @param exchangeType
     * @param toutingKey
     * @param message
     * @throws IOException
     * @throws TimeoutException
     */
    void publishMsg(String exchange,
                    BuiltinExchangeType exchangeType,
                    String toutingKey,
                    String message) throws IOException, TimeoutException;

    /**
     * 消费指定信息
     *
     * @param body
     * @param deliveryTag
     * @param channel
     */
    void consumerMsg4Ori(byte[] body,
                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                         Channel channel);

    /**
     * 消费消息
     *
     * @param exchange
     * @param queue
     * @param routingKey
     * @throws IOException
     * @throws TimeoutException
     */
    /*
    void consumerMsg(String exchange,
                     String queue,
                     String routingKey) throws IOException, TimeoutException;
    */
}
