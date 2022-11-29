package io.tao.jms.retry.demo.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.tao.jms.retry.JmsRetryTemplate;
import io.tao.jms.retry.JmsTimeoutException;
import io.tao.jms.retry.demo.model.Country;
import io.tao.jms.retry.message.JmsMQMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/jms-countries")
public class JmsCountryController {

    @Autowired
    private JmsRetryTemplate jmsRetryTemplate;

    private static Gson gson = new GsonBuilder().create();

    @GetMapping("")
    public ResponseEntity<List<Country>> getCountries() {
        JmsMQMessage requestMessage = JmsMQMessage.builder()
                .addHeader("ServiceCode", "getCountries")
                .addHeader(JmsMQMessage.JMS_REPLY_TO, "DEV.QUEUE.2")
                // payload does not allow null
                .setPayload("this is payload")
                .build();
        // send to DEV.QUEUE.1 and expect receive from DEV
        try {
            JmsMQMessage replyMessage = jmsRetryTemplate.sendAndReceive("DEV.QUEUE.1", requestMessage);
            Type listOfMyClassObject = new TypeToken<ArrayList<Country>>() {}.getType();
            List<Country> countries = gson.fromJson(replyMessage.getPayload(), listOfMyClassObject);
            return new ResponseEntity<List<Country>>(countries, HttpStatus.OK);
        } catch (JmsTimeoutException ex) {
            return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
        }

    }

    @GetMapping("/{code}")
    public ResponseEntity<Country> getCountryByCode(@PathVariable String code) {
        Country requestCountry = new Country(null, code, null);
        JmsMQMessage requestMessage = JmsMQMessage.builder()
                .addHeader("ServiceCode", "getCountryByCode")
                .addHeader(JmsMQMessage.JMS_REPLY_TO, "DEV.QUEUE.2")
                // payload does not allow null
                .setPayload(gson.toJson(requestCountry))
                .build();
        // send to DEV.QUEUE.1 and expect receive from DEV
        try {
            JmsMQMessage replyMessage = jmsRetryTemplate.sendAndReceive("DEV.QUEUE.1", requestMessage);
            Country country = gson.fromJson(replyMessage.getPayload(), Country.class);
            return new ResponseEntity<>(country, HttpStatus.OK);
        } catch (JmsTimeoutException ex) {
            return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
        }
    }

}
