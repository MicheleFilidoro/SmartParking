package gruppo_nove.mqtt.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

/**
 * @author Filidoro Michele
 * @author Crevacore Andrea
 * @author Enea Frontera
 */

@Getter
@Setter
@NoArgsConstructor
public class Sensore {
    private int id;
    private String nome;
    private String type;
    private HashSet<Misura> misure;
    boolean enabled;

    public Sensore(int id, String nome, String type, boolean enabled) {
        this.id = id;
        this.nome = nome;
        this.type = type;
        this.misure = new HashSet<>();
        this.enabled = enabled;
    }

}

