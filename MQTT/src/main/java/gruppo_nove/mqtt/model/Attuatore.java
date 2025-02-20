package gruppo_nove.mqtt.model;

import gruppo_nove.mqtt.publisher.Publisher;
import gruppo_nove.mqtt.subscriber.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Attuatore {
    private static final Logger logger = LogManager.getLogger(Attuatore.class.getName());

    private final Publisher publisher;
    private final Subscriber subscriber;
    private final List<String> subscribed;

    // Costruttore che utilizza l'iniezione delle dipendenze di Spring
    public Attuatore(Publisher publisher, Subscriber subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.subscribed = new ArrayList<>();
    }

    /**
     * Pubblica un messaggio su un topic specifico.
     *
     * @param topic   il topic su cui pubblicare
     * @param message il messaggio da pubblicare
     */
    public void pub(String topic, String message) {
        if (topic.contains("ACTUATOR")) {
            this.publisher.publish(topic, message);
            logger.info("Pubblicazione ACTUATOR effettuata. Topic: {}, Messaggio: {}", topic, message);
        } else {
            logger.warn("Il topic {} non è valido per gli attuatori.", topic);
        }
    }

    /**
     * Sottoscrive un topic specifico se non già sottoscritto.
     *
     * @param topic il topic da sottoscrivere
     * @throws MqttException se si verifica un errore durante la sottoscrizione
     */
    public void sub(String topic) throws MqttException {
        if (!this.subscribed.contains(topic)) {
            this.subscriber.subscribe(topic);
            this.subscribed.add(topic);
            logger.info("Sottoscrizione completata per il topic: {}", topic);
        } else {
            logger.info("Il topic {} è già sottoscritto.", topic);
        }
    }
}
