package io.tao.jms.retry.demo.service;

import io.tao.jms.retry.MessageReceivedCallback;
import io.tao.jms.retry.MessageSentCallback;
import io.tao.jms.retry.message.JmsMQMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Queue;

@Service
@Log4j2
public class LoggerService implements MessageSentCallback, MessageReceivedCallback {

    private String getDestinationName(JmsMQMessage message) {
        String destinationName = "*UNKNOWN";
        Object destination = message.getHeader("JMSDestination");
        try {
            if (destination instanceof Queue) {
                destinationName = ((Queue) destination).getQueueName();
            } else if (destination instanceof String) {
                destinationName = (String) destination;
            } else if (destination != null) {
                destinationName = destination.toString();
            }
        } catch (JMSException ex) {
            // ignore
        }
        return destinationName;
    }

    @Override
    public void messageReceived(JmsMQMessage message) {
        log.info("*INCOMING <<< {} \n" + message.getHeaders() + "\n" + message.getPayload() + "\n", getDestinationName(message));
    }

    @Override
    public void messageSent(JmsMQMessage message) {
        log.info("*OUTGOING >>> {} \n" + message.getHeaders() + "\n" + message.getPayload() + "\n", getDestinationName(message));
    }
    
}
