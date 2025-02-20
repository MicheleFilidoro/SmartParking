package gruppo_nove.smartserver.model.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Filidoro Michele
 * @author Crevacore Andrea
 * @author Enea Frontera
 */

@Getter
@Setter
@NoArgsConstructor
public class Prenotazione {
    private int id;
    private int idPostoAuto;
    private String orario_arrivo;
    private int durata_prenotazione;
    private String orario_fine_prenotazione;
    private int idUtente;
    private boolean accettata;

    /**
     * Costruttore di Prenotazione che calcola automaticamente l'orario di fine prenotazione.
     *
     * @param id                  Identificatore unico della prenotazione.
     * @param idPostoAuto         Identificatore del posto auto prenotato.
     * @param orario_arrivo       Ora di arrivo prevista.
     * @param durata_prenotazione Durata della prenotazione in minuti.
     * @param idUtente            Identificatore dell'utente che effettua la prenotazione.
     */
    public Prenotazione(int id, int idPostoAuto, String orario_arrivo, int durata_prenotazione, int idUtente, boolean accettata) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // Converte la stringa in LocalDateTime
        LocalDateTime orarioArrivoDateTime = LocalDateTime.parse(orario_arrivo, formatter);

        // Calcola l'orario di fine prenotazione e lo converte in stringa
        this.id = id;
        this.idPostoAuto = idPostoAuto;
        this.orario_arrivo = orario_arrivo;
        this.durata_prenotazione = durata_prenotazione;
        this.orario_fine_prenotazione = orarioArrivoDateTime.plusMinutes(durata_prenotazione).format(formatter).toString();
        this.idUtente = idUtente;
        this.accettata=accettata;
    }
}

