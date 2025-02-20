package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.PostoAuto;
import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.repository.GlobalRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/postoauto")
@RequiredArgsConstructor
public class ControllerPostoAuto {
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerPostoAuto.class.getName());

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
    public ResponseEntity<String> updateStatoPostoAuto(@RequestBody String param) {
        Gson gson = new Gson();
        PostoAuto postoAuto = gson.fromJson(param, PostoAuto.class);

        logger.info("Ricevuta richiesta per aggiornare stato del posto auto: {}", postoAuto);

        // Aggiorna il posto auto nel repository
        boolean updated = this.repository.updatePostoAuto(postoAuto);
        if (!updated) {
            logger.error("Errore nell'aggiornamento del posto auto nel database.");
            return ResponseEntity.status(500).body("Errore nell'aggiornamento del posto auto");
        }

        // Pubblica lo stato aggiornato su MQTT
        String topic = "posti_auto/" + postoAuto.getId() + "/stato";
        String payload = gson.toJson(Map.of(
                "idPosto", postoAuto.getId(),
                "stato", postoAuto.isOccupato() ? "occupato" : "libero"
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

        return ResponseEntity.ok("Stato aggiornato e notificato su MQTT");
    }

    @PostMapping(value = "/update")
    public ResponseEntity<String> updatePostoAuto(@RequestBody String param) {
        Gson gson = new Gson();
        PostoAuto postoAuto = gson.fromJson(param, PostoAuto.class);

        // Aggiorna il posto auto nel repository
        boolean success = this.repository.updatePostoAuto(postoAuto);

        if (success) {
            logger.info("Posto auto aggiornato con successo: {}", postoAuto);
            return ResponseEntity.ok("Posto auto aggiornato con successo");
        } else {
            logger.error("Errore nell'aggiornamento del posto auto: {}", postoAuto);
            return ResponseEntity.status(500).body("Errore nell'aggiornamento del posto auto");
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<Integer> addPostoAuto(@RequestBody String param) {
        Gson gson = new Gson();
        PostoAuto postoAuto = gson.fromJson(param, PostoAuto.class);
        return ResponseEntity.ok(this.repository.addPostoAuto(postoAuto));
    }
    @DeleteMapping(value = "/delete/{id}")
    public String deletePostoAuto(@PathVariable int id) {
        Gson gson = new Gson();
        this.repository.deletePostoAuto(id);
        return gson.toJson("Ok");
    }


    @GetMapping("/countOccupati")
    public int countPostiOccupati() {
        return repository.countPostiOccupati();
    }

    @GetMapping("/get/all")
    public ResponseEntity<HashSet<PostoAuto>> getAllPostiAuto() {
        try {
            HashSet<PostoAuto> postiAuto = repository.getAllPostiAuto();
            if (postiAuto.isEmpty()) {
                logger.info("Nessun posto auto trovato.");
                return ResponseEntity.noContent().build();
            }
            logger.info("Recuperati {} posti auto.", postiAuto.size());
            return ResponseEntity.ok(postiAuto);
        } catch (Exception e) {
            logger.error("Errore durante il recupero dei posti auto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/count/total")
    public ResponseEntity<Integer> getCountTotal() {
        try {
            int total = repository.getAllPostiAuto().size();
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            logger.error("Errore durante il recupero del conteggio totale dei posti auto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/count/occupied")
    public ResponseEntity<Integer> getCountOccupied() {
        try {
            int occupied = repository.countPostiOccupati();
            return ResponseEntity.ok(occupied);
        } catch (Exception e) {
            logger.error("Errore durante il recupero del conteggio dei posti occupati", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count/free")
    public ResponseEntity<Integer> getCountFree() {
        try {
            int total = repository.getAllPostiAuto().size();
            int occupied = repository.countPostiOccupati();
            int free = total - occupied;
            return ResponseEntity.ok(free);
        } catch (Exception e) {
            logger.error("Errore durante il recupero del conteggio dei posti disponibili", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}



