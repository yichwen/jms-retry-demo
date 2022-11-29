package io.tao.jms.retry.demo.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tao.ibmmq")
public class IBMMQProperties {

    private String hostName;
    private int port;
    private String queueManager;
    private String channel;

}
