package gruppo_nove.smartserver.controller;


import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.MWBot;
import gruppo_nove.smartserver.model.item.Sensore;
import gruppo_nove.smartserver.repository.GlobalRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mwbot")
@RequiredArgsConstructor
public class ControllerMWBot {
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerMWBot.class.getName());
    private MqttClient mqttClient;
    @PostConstruct
    public void init() {
        try {
            mqttClient = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
            mqttClient.connect();
            logger.info("Connesso al broker MQTT.");
        } catch (MqttException e) {
            logger.error("Errore durante la connessione al broker MQTT: {}", e.getMessage());
        }
    }

    @PostMapping(value = "/update/stato")
    public ResponseEntity<String> updateStatoMWBot(@RequestBody String param) {
        Gson gson = new Gson();

        // Deserializza il JSON in un oggetto MWBot
        MWBot mwBot = gson.fromJson(param, MWBot.class);
        logger.info("Ricevuta richiesta per aggiornare stato del MWBot: {}", mwBot);

        // Pubblica lo stato aggiornato su MQTT
        String topic = "mwbot/stato";
        String payload = gson.toJson(Map.of(
                "stato", mwBot.getStato(),
                "idAutoElettrica", mwBot.getIdAutoElettrica(),
                "percentualeCarica", mwBot.getPercentualeCarica()
        ));

        try {
            if (mqttClient == null || !mqttClient.isConnected()) {
                logger.error("Client MQTT non è connesso. Impossibile pubblicare il messaggio.");
                return ResponseEntity.status(500).body("Errore: client MQTT non connesso");
            }

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            mqttClient.publish(topic, message);
            logger.info("Messaggio pubblicato su topic {}: {}", topic, payload);
        } catch (MqttException e) {
            logger.error("Errore durante la pubblicazione del messaggio su MQTT", e);
            return ResponseEntity.status(500).body("Errore nella pubblicazione su MQTT");
        }

        return ResponseEntity.ok("Stato MWBot aggiornato e notificato su MQTT");
    }

    @PostMapping(value = "/add")
    public ResponseEntity<Integer> addMWBot(@RequestBody String param) {
        Gson gson = new Gson();
        MWBot mwBot = gson.fromJson(param, MWBot.class);
        return ResponseEntity.ok(this.repository.addMWBot(mwBot));
    }
    @DeleteMapping(value = "/delete/{id}")
    public String deleteMWBot(@PathVariable int id) {
        Gson gson = new Gson();
        this.repository.deleteMWBot(id);
        return gson.toJson("Ok");
    }
    @GetMapping(value = "/get/all")
    public String getAllMWBots() {
        Gson gson = new Gson();
        HashSet<MWBot> mwBots = this.repository.getAllMWBots();
        return gson.toJson(mwBots);
    }
}