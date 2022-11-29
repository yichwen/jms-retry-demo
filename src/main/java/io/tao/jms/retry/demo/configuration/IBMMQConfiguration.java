package io.tao.jms.retry.demo.configuration;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Configuration
@EnableConfigurationProperties(IBMMQProperties.class)
public class IBMMQConfiguration {

    @Bean
    public ConnectionFactory defaultConnectionFactory(IBMMQProperties properties) throws JMSException {
        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        factory.setHostName(properties.getHostName());
        factory.setPort(properties.getPort());
        factory.setQueueManager(properties.getQueueManager());
        factory.setChannel(properties.getChannel());
        factory.setTransportType(1);
        factory.setTargetClientMatching(true);
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate(ConnectionFactory defaultConnectionFactory) {
        return new JmsTemplate(defaultConnectionFactory);
    }

}
