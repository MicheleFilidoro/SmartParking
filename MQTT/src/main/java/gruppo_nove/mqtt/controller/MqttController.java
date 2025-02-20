package gruppo_nove.mqtt.controller;

import gruppo_nove.mqtt.subscriber.Subscriber;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mqtt")
public class MqttController {

    private final Subscriber subscriber;

    public MqttController(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @PostMapping("/start-listening")
    public String startListening() {
        try {
            subscriber.subscribeToAllTopics();
            return "Sottoscrizione a tutti i topic avviata con successo!";
        } catch (Exception e) {
            return "Errore durante l'avvio della sottoscrizione: " + e.getMessage();
        }
    }
}
