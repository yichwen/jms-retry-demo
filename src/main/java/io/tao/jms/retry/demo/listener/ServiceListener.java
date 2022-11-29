package io.tao.jms.retry.demo.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.tao.jms.retry.JmsRetryTemplate;
import io.tao.jms.retry.demo.model.Country;
import io.tao.jms.retry.demo.service.CountryService;
import io.tao.jms.retry.demo.service.LoggerService;
import io.tao.jms.retry.message.JmsMQMessage;
import io.tao.jms.retry.message.JmsMQMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.Message;
import java.util.List;

@Component
@Slf4j
public class ServiceListener {

    @Autowired
    private LoggerService loggerService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private JmsRetryTemplate jmsRetryTemplate;

    private static Gson gson = new GsonBuilder().create();

    public ServiceListener() {
    }

    @JmsListener(destination = "DEV.QUEUE.1")
    public void onMessage(Message jmsMessage) {

        JmsMQMessage message = JmsMQMessage.fromJmsMessage(jmsMessage);
        loggerService.messageReceived(message);

        JmsMQMessageBuilder builder = JmsMQMessage.builder();

        // process payload
        String serviceCode = (String) message.getHeader("ServiceCode");
        if (serviceCode != null) {

            if (serviceCode.equals("getCountries")) {
                List<Country> countries = countryService.getCountries();
                String payload = gson.toJson(countries);
                builder.setPayload(payload);
            } else if (serviceCode.equals("getCountryByCode")) {
                String countryCode = gson.fromJson(message.getPayload(), Country.class).getCountryCode();
                Country country = countryService.getCountryByCode(countryCode.toUpperCase());
                if (country == null) {
                    log.error("country code {} not found", countryCode);
                    return;
                }
                String payload = gson.toJson(country);
                builder.setPayload(payload);
            } else {
                log.error("invalid service code, transaction ignored");
                return;
            }

        } else {
            log.error("no service code found, transaction ignored");
            return;
        }

        // if there is no replyTo, no response needed
        Object replyTo = message.getHeader(JmsMQMessage.JMS_REPLY_TO);
        if (replyTo instanceof Destination) {

            String correlationID = (String) message.getHeader(JmsMQMessage.JMS_CORRELATION_ID);
            if (correlationID == null) {
                correlationID = (String) message.getHeader(JmsMQMessage.JMS_MESSAGE_ID);
            }

            builder.addHeader(JmsMQMessage.JMS_CORRELATION_ID, correlationID);
            jmsRetryTemplate.send((Destination) replyTo, builder.build());
        }

    }

}
