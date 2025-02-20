package gruppo_nove.smartserver.model.item;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Filidoro Michele
 * @author Crevacore Andrea
 * @author Enea Frontera
 */

@Setter
@Getter
@NoArgsConstructor

public class PostoAuto {

    private int id;
    private Sensore sensoreOccupazione;
    private Attuatore attuatoreOccupazione;
    private boolean occupato;

    public PostoAuto(int id, boolean occupato) {
        this.id = id;
        this.sensoreOccupazione = new Sensore();
        this.attuatoreOccupazione = new Attuatore();
        this.occupato = occupato;
    }

}
