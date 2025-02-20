package gruppo_nove.mqtt.publisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
public class Publisher {
    private static final Logger logger = LogManager.getLogger(Publisher.class);
    private final MqttClient mqttClient;

    public Publisher(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void publish(String topic, String payload) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            mqttClient.publish(topic, message);
            logger.info("Messaggio pubblicato su topic {}: {}", topic, payload);
        } catch (Exception e) {
            logger.error("Errore durante la pubblicazione del messaggio: {}", e.getMessage());
        }
    }
}
