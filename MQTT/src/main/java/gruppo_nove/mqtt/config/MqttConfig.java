package gruppo_nove.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() throws MqttException {
        String brokerUrl = "tcp://localhost:1883";
        String clientId = "SpringMqttClient";
        MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        client.connect();
        return client;
    }
}
