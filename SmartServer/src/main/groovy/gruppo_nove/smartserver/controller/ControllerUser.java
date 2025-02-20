package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.AutoElettrica;
import gruppo_nove.smartserver.model.item.PostoAuto;
import gruppo_nove.smartserver.model.security.services.TokenService;
import gruppo_nove.smartserver.model.security.utils.TokenCheck;
import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.model.user.UserRole;
import gruppo_nove.smartserver.repository.GlobalRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class ControllerUser {
    private final TokenService tokenService;
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerUser.class.getName());
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

    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<Integer> addUser(@RequestBody String param, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.SYSTEMADMIN)) {
            Gson gson = new Gson();
            UserProfile user = gson.fromJson(param, UserProfile.class);
            return ResponseEntity.ok(this.repository.addUser(user));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    @GetMapping("/admin/stato_parcheggio")
    public ResponseEntity<Map<String, Object>> getStatoParcheggio() {
        // Recupera lo stato del parcheggio
        Map<String, Object> statoParcheggio = repository.getStatoParcheggio();

        // Log dei dati recuperati
        logger.info("Stato del parcheggio recuperato: {}", statoParcheggio);

        // Configura il topic e il payload MQTT
        String topic = "amministratore/stato_parcheggio";
        Gson gson = new Gson();
        String payload = gson.toJson(Map.of(
                "postiOccupati", statoParcheggio.get("postiOccupati"),
                "postiLiberi", statoParcheggio.get("postiLiberi")

        ));

        // Pubblica il messaggio su MQTT
        try {
            if (mqttClient == null || !mqttClient.isConnected()) {
                logger.error("Client MQTT non è connesso. Impossibile pubblicare il messaggio.");
                return ResponseEntity.status(500).body(Map.of("errore", "Client MQTT non connesso"));
            }

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            mqttClient.publish(topic, message);
            logger.info("Messaggio pubblicato su topic {}: {}", topic, payload);
        } catch (MqttException e) {
            logger.error("Errore durante la pubblicazione del messaggio su MQTT", e);
            return ResponseEntity.status(500).body(Map.of("errore", "Errore nella pubblicazione su MQTT"));
        }

        // Restituisce la risposta HTTP con lo stato del parcheggio
        return ResponseEntity.ok(statoParcheggio);
    }

    @GetMapping(value = "/get/all")
    @PreAuthorize("hasAuthority('SYSTEMADMIN')")
    public String getAllUser(HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);
        Gson gson = new Gson();
        if (this.tokenService.validateTokenAndRole(jwt, UserRole.SYSTEMADMIN)) {
            HashSet<UserProfile> userProfiles = this.repository.getAllUser();
            return gson.toJson(userProfiles);
        } else {
            return gson.toJson(Map.of("error", "Accesso negato"));
        }
    }
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable int id, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.SYSTEMADMIN)) {
            try {
                boolean isDeleted = this.repository.deleteUser(id);
                if (isDeleted) {
                    return ResponseEntity.ok("Utente eliminato con successo");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato");
                }
            } catch (Exception e) {
                logger.error("Errore durante l'eliminazione dell'utente con ID {}: {}", id, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno del server");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }
}