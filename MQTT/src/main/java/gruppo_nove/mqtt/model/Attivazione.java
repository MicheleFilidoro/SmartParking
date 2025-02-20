package gruppo_nove.mqtt.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Filidoro Michele
 * @author Crevacore Andrea
 * @author Enea Frontera
 */

@Getter
@Setter
@NoArgsConstructor
public class Attivazione {
    private int id;
    private boolean current;
    private String time;
    private int idAttuatore;

    public Attivazione(boolean current, String time, int idAttuatore) {
        this.current = current;
        this.time = time;
        this.idAttuatore = idAttuatore;
    }

    public Attivazione(int id, String time, boolean stato, int idAttuatore) {
        this.id = id;
        this.current = stato;
        this.time = time;
        this.idAttuatore = idAttuatore;
    }
}
