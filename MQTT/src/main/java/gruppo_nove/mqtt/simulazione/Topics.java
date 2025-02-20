package gruppo_nove.mqtt.simulazione;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;



@Getter
@Setter

public class Topics {

    LinkedList<TopicCreator> topics;


    public Topics() {
        this.topics = new LinkedList<>();
    }

}