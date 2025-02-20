package gruppo_nove.mqtt.simulazione;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopicCreator {
    private String topic;
    private String typeSensore;
    private int idSensore;

    public TopicCreator(String topic, String typeSensore, int idSensore) {
        this.topic = topic;
        this.typeSensore = typeSensore;
        this.idSensore = idSensore;
    }
}
