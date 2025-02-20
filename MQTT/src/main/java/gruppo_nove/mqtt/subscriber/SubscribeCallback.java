package gruppo_nove.mqtt.subscriber;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.web.client.RestTemplate;
import gruppo_nove.mqtt.model.Attivazione;

public class SubscribeCallback implements MqttCallback {

    private static final Logger logger = LogManager.getLogger(SubscribeCallback.class.getName());
    private String apiUrlSensor;

    public SubscribeCallback() {
        this.apiUrlSensor = "http://localhost:8080/api/v1/sensore/add";
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String mex = new String(message.getPayload());
        RestTemplate restTemplate = new RestTemplate();

        logger.info("Messaggio arrivato: {}, con topic: {}", mex, topic);

        // Handling user notifications
        if (topic.startsWith("utenti/") && topic.endsWith("/notifiche")) {
            logger.info("Notifica per utente ricevuta: {}", mex);
            // Process user-specific notification here
        }

        if (topic.contains("SENSOR")) {
            restTemplate.postForObject(apiUrlSensor, mex, String.class);
        } else if (topic.contains("ACTUATOR")) {
            Gson gson = new Gson();
            Attivazione attivazione = gson.fromJson(mex, Attivazione.class);
            logger.info("Attuatore {} impostato su {}", attivazione.getIdAttuatore(), attivazione.isCurrent());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
