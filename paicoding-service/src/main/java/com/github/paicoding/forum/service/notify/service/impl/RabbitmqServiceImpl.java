package com.github.paicoding.forum.service.notify.service.impl;

import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqUtil;
import com.github.paicoding.forum.core.util.JsonUtil;
import com.github.paicoding.forum.core.util.SpringUtil;
import com.github.paicoding.forum.service.notify.service.NotifyService;
import com.github.paicoding.forum.service.notify.service.RabbitmqService;
import com.github.paicoding.forum.service.user.repository.entity.UserFootDO;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RabbitmqServiceImpl implements RabbitmqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitmqUtil rabbitmqUtil;

    @Autowired
    private NotifyService notifyService;

    @Override
    public boolean enabled() {
        return "true".equalsIgnoreCase(SpringUtil.getConfig("rabbitmq.switchFlag"));
    }

    @Override
    public void publishMsg(String exchange, BuiltinExchangeType exchangeType, String routingKey, String message) {
        // 声明exchange中的消息为可持久化，不自动删除；而AMQP创建的默认符合
        rabbitmqUtil.declareExchange(exchange, exchangeType);
        // 发送消息
        rabbitTemplate.convertAndSend(exchange, routingKey, message.getBytes());
        log.info("Publish msg: {}", message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = CommonConstants.QUERE_NAME_PRAISE, durable = "true"),
            exchange = @Exchange(value = CommonConstants.EXCHANGE_NAME_DIRECT, type = ExchangeTypes.DIRECT, durable = "true"),
            key = CommonConstants.QUERE_KEY_PRAISE), ackMode = "MANUAL")
    public void consumerMsg4Ori(byte[] body, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) {
        try {
            String msg = new String(body, StandardCharsets.UTF_8);
            log.info("Consumer msg: {}", msg);

            // 获取Rabbitmq消息，并保存到DB
            UserFootDO userFootDO = JsonUtil.toObj(msg, UserFootDO.class);
            notifyService.saveArticleNotify(userFootDO, NotifyTypeEnum.PRAISE);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error(e.toString());
            // 消息处理失败，重新放入队列
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ex) {
                log.error(ex.toString());
            }
        }
    }
}
