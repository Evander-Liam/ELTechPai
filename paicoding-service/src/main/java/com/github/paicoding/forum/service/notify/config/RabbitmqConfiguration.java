package com.github.paicoding.forum.service.notify.config;

import com.github.paicoding.forum.core.config.RabbitmqProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>date: 2024/03/20 15:52 </p>
 * <p>description: RabbitMQ连接池 </p>
 *
 * @author Liam
 */
@Configuration
@ConditionalOnProperty(value = "rabbitmq.switchFlag", havingValue = "true")
@EnableConfigurationProperties(RabbitmqProperties.class)
public class RabbitmqConfiguration {
    @Bean
    public CachingConnectionFactory connectionFactory(RabbitmqProperties rabbitmqProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(rabbitmqProperties.getHost());
        connectionFactory.setPort(rabbitmqProperties.getPort());
        connectionFactory.setUsername(rabbitmqProperties.getUsername());
        connectionFactory.setPassword(rabbitmqProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitmqProperties.getVirtualhost());
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        connectionFactory.setConnectionCacheSize(rabbitmqProperties.getPoolSize());
        return connectionFactory;
    }
}
