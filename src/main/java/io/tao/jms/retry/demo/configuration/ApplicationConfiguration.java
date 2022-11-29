package io.tao.jms.retry.demo.configuration;

import io.tao.jms.retry.JmsRetryTemplate;
import io.tao.jms.retry.JmsTimeoutException;
import io.tao.jms.retry.demo.service.LoggerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public JmsRetryTemplate jmsRetryTemplate(JmsTemplate jmsTemplate, LoggerService loggerService) {
        // override default JmsTemplate, else sendAndReceive will wait the response indefinitely without timeout
        jmsTemplate.setReceiveTimeout(10000);
        JmsRetryTemplate channel = new JmsRetryTemplate(jmsTemplate);
        // add exception class, if that exception happened, will retry
        channel.addRetryableException(JmsTimeoutException.class);
        // setup messageSentCallback and messageReceivedCallback
        channel.setMessageReceivedCallback(loggerService);
        channel.setMessageSentCallback(loggerService);
        return channel;
    }

}
