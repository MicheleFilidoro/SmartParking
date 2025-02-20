package gruppo_nove.smartserver.model.item;


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
public class RichiestaRicarica {
    private int id;
    private int idUtente;
    private int idAutoElettrica;
    private int PercentualeDesiderata;
    private String orarioLimite;
    private boolean accettata;


    public RichiestaRicarica(int id, int idUtente, int idAutoElettrica, int percentualeDesiderata, String orarioLimite, boolean accettata) {
        this.id = id;
        this.idUtente = idUtente;
        this.idAutoElettrica = idAutoElettrica;
        this.PercentualeDesiderata = percentualeDesiderata;
        this.orarioLimite = orarioLimite;
        this.accettata = accettata;
    }
}
