package gruppo_nove.smartserver.model.item;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MWBot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stato; // libero | in_movimento | in_carica
    private String idAutoElettrica; // ID dell'auto corrente
    private int percentualeCarica; // Percentuale carica

    

    public MWBot(String stato, String idAutoElettrica, Integer percentualeCarica) {
        this.stato = stato;
        this.idAutoElettrica = idAutoElettrica;
        this.percentualeCarica = percentualeCarica;
    }


    public MWBot(int id, String stato, String idAutoElettrica, int percentualeCarica) {
    }
}
