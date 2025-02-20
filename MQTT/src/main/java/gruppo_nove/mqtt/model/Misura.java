package gruppo_nove.mqtt.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Filidoro Michele
 * @author Crevacore Andrea
 * @author Enea Frontera
 */

@Getter
@Setter
@NoArgsConstructor
public class Misura {
    private int id;
    private Double value;
    private String time;
    private int idSensore;

    public Misura(Integer id, Double value, String time, int idSensore) {
        this.id = id;
        this.value = value;
        this.time = time;
        this.idSensore = idSensore;
    }

    public Misura(Double value, String time, int idSensore) {
        this.value = value;
        this.time = time;
        this.idSensore = idSensore;
    }
}

