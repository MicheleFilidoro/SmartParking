package gruppo_nove.smartserver.model.item;

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
public class AutoElettrica {
    private int id;
    private String Modello;
    private String Targa;
    private int idUtente;
    private int capacitaBatteria;

    public AutoElettrica(int id, String Modello, String Targa, int idUtente, int capacitaBatteria) {
        this.id = id;
        this.Modello = Modello;
        this.Targa = Targa;
        this.idUtente = idUtente;
        this.capacitaBatteria = capacitaBatteria;
    }
}
