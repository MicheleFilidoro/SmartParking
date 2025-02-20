package gruppo_nove.mqtt.subscriber;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Subscriber {
    private static final Logger logger = LogManager.getLogger(Subscriber.class);
    private final MqttClient mqttClient;

    public Subscriber(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void subscribe(String topic) throws MqttException {
        mqttClient.subscribe(topic, (topic1, message) -> {
            String payload = new String(message.getPayload());
            System.out.println("Messaggio ricevuto su topic " + topic1 + ": " + payload);
        });
    }


    public void subscribeToAllTopics() throws MqttException {
        String wildcardTopic = "#"; // Wildcard per ascoltare tutti i topic
        mqttClient.subscribe(wildcardTopic, (IMqttMessageListener) (topic, message) -> {
            String payload = new String(message.getPayload());
            logger.info("Messaggio ricevuto dal topic {}: {}", topic, payload);
            System.out.println("Messaggio ricevuto dal topic " + topic + ": " + payload);
        });
        logger.info("Sottoscritto a tutti i topic usando il wildcard '{}'", wildcardTopic);
    }

}
