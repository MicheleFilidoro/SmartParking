package gruppo_nove.mqtt;

import gruppo_nove.mqtt.subscriber.Subscriber;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqttApplication implements CommandLineRunner {
    private final Subscriber subscriber;

    public MqttApplication(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public static void main(String[] args) {
        SpringApplication.run(MqttApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            subscriber.subscribeToAllTopics();
            System.out.println("Sottoscritto a tutti i topic.");
        } catch (Exception e) {
            System.err.println("Errore durante la sottoscrizione ai topic: " + e.getMessage());
        }
    }
}
