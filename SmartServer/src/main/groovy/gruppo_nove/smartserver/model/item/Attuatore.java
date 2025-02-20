package gruppo_nove.smartserver.model.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
public class Attuatore {
    private int id;
    private String nome;
    private HashSet<Attivazione> attivazioni;

    public Attuatore(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.attivazioni = new HashSet<>();
    }
}
