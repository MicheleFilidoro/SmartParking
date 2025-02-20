package gruppo_nove.mqtt.simulazione;

import gruppo_nove.mqtt.model.Misura;
import gruppo_nove.mqtt.model.Sensore;
import gruppo_nove.mqtt.publisher.Publisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class Simulazione {
    private static final Logger logger = LogManager.getLogger(Simulazione.class.getName());

    private final Publisher publisher;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final String apiUrl = "http://localhost:8080/api/v1/sensore/get/all";

    public Simulazione(Publisher publisher) {
        this.publisher = publisher;
        this.restTemplate = new RestTemplate();
        this.gson = new Gson();
    }

    public void start() throws MqttException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        Gson gson = new Gson();

        try {
            // Effettua una richiesta al server per ottenere i sensori
            String urlSensori = "http://localhost:8080/api/v1/sensore/get/all";
            logger.info("Richiedo sensori dall'endpoint: {}", urlSensori);

            String response = restTemplate.getForObject(urlSensori, String.class);
            logger.info("Risposta dal server: {}", response);

            // Converti la risposta JSON in un array di oggetti Sensore
            Sensore[] sensori = gson.fromJson(response, Sensore[].class);

            for (Sensore sensore : sensori) {
                if (sensore.isEnabled()) {
                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    double value = new Random().nextDouble() * 100;

                    Misura misura = new Misura(value, time, sensore.getId());
                    String misuraJson = gson.toJson(misura);

                    // Pubblica i dati sul topic MQTT
                    String topic = "sensore/" + sensore.getId();
                    this.publisher.publish(topic, misuraJson);

                    logger.info("Pubblicato messaggio per il sensore {} sul topic {}: {}", sensore.getNome(), topic, misuraJson);
                } else {
                    logger.info("Sensore {} disabilitato, saltato.", sensore.getNome());
                }
            }

            Thread.sleep(10000); // Aspetta prima di ricominciare
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Endpoint non trovato: {}", e.getMessage());
        } catch (RestClientException e) {
            logger.error("Errore durante la chiamata REST: {}", e.getMessage());
        }
    }


}
