package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.*;
import gruppo_nove.smartserver.model.user.Saldo;
import gruppo_nove.smartserver.model.user.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public class GlobalRepository {
    /**
     * The constant logger.
     */
    public static final Logger logger = LogManager.getLogger(Repository.class.getName());
    private final UsersRepository usersRepository;
    private final SensoreRepository sensoreRepository;
    private final AttivazioneRepository attivazioneRepository;
    private final AttuatoreRepository attuatoreRepository;
    private final MisuraRepository misuraRepository;
    private final PostoAutoRepository postoAutoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final AutoElettricaRepository autoElettricaRepository;
    private final RichiestaRicaricaRepository richiestaRicaricaRepository;
    private final MWBotRepository mwBotRepository;
    private final SaldoRepository saldoRepository;
    private final FixedPriceRepository fixedPriceRepository;

    public GlobalRepository(UsersRepository usersRepository, SensoreRepository sensoreRepository,
                            AttivazioneRepository attivazioneRepository, AttuatoreRepository attuatoreRepository,
                            PostoAutoRepository postoAutoRepository, MisuraRepository misuraRepository,
                            PrenotazioneRepository prenotazioneRepository,AutoElettricaRepository autoElettricaRepository,
                            RichiestaRicaricaRepository richiestaRicaricaRepository, MWBotRepository mwBotRepository, SaldoRepository saldoRepository,
                            FixedPriceRepository fixedPriceRepository) {
        this.usersRepository = usersRepository;
        this.sensoreRepository = sensoreRepository;
        this.attivazioneRepository = attivazioneRepository;
        this.attuatoreRepository = attuatoreRepository;
        this.misuraRepository = misuraRepository;
        this.postoAutoRepository = postoAutoRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.autoElettricaRepository = autoElettricaRepository;
        this.richiestaRicaricaRepository=richiestaRicaricaRepository;
        this.mwBotRepository=mwBotRepository;
        this.saldoRepository=saldoRepository;
        this.fixedPriceRepository=fixedPriceRepository;
    }

    //<editor-fold desc=“USER">
    /**
     * Gets user by username.
     *
     * @param username
     * 		the username
     *
     * @return user by username
     */
    public UserProfile getUserByUsername(String username) {
        try {
            if (username != null && !username.isEmpty()) {
                return this.usersRepository.getUserByUsername(username);
            } else {
                return null;
            }
        }catch(Exception e) {
            return null;
        }
    }

    /**
     * Add user integer.
     *
     * @param user
     * 		the user
     *
     * @return integer integer
     */
    public Integer addUser(UserProfile user) {
        if (user != null) {
            try {
                return this.usersRepository.addUser(user);
            } catch (Exception e){
                return null;
            }
        }

        return null;
    }

    /**
     * Exists by username boolean.
     *
     * @param username
     * 		the username
     *
     * @return boolean boolean
     */
    public Boolean existsByUsername(String username) {
        if (username != null && !username.isEmpty()) {
            try {
                return !this.usersRepository.existsUsername(username);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }


    /**
     * Exists by email boolean.
     *
     * @param email
     * 		the email
     *
     * @return boolean boolean
     */
    public Boolean existsByEmail(String email) {
        if (email != null && !email.isEmpty()) {
            try {
                return this.usersRepository.existsEmail(email);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public HashSet<UserProfile> getAllUser() {
        return this.usersRepository.getAllUser();
    }
    /**
     * Elimina un utente dal database.
     *
     * @param id L'ID dell'utente da eliminare.
     * @return true se l'eliminazione è stata eseguita con successo, altrimenti false.
     */
    public boolean deleteUser(int id) {
        if (id > 0) {
            try {
                return this.usersRepository.deleteUser(id);
            } catch (Exception e) {
                logger.error("Errore durante l'eliminazione dell'utente con ID {}: {}", id, e.getMessage());
            }
        } else {
            logger.warn("ID utente non valido: {}", id);
        }
        return false;
    }


    // </editor-fold>
    //<editor-fold desc=“SENSORE">


    public Integer addSensore(Sensore sensore) {
        if (sensore != null) {
            try {
                return this.sensoreRepository.addSensore(sensore);
            } catch (Exception e){
                return null;
            }
        }
        return null;
    }

    public boolean deleteSensore(int idSensore) {
        if (idSensore >0) {
            return this.sensoreRepository.deleteSensore(idSensore);
        }
        return false;
    }
    public HashSet<Sensore> getAllSensori() {
        return this.sensoreRepository.getAllSensori();
    }
    // </editor-fold>

    //<editor-fold desc=“ATTIVAZIONE">

    public Integer addAttivazione(Attivazione attivazione) {
        if(attivazione != null) {
            try{
                return this.attivazioneRepository.addAttivazione(attivazione);
            }catch(Exception e){
                return null;
            }
        }
        return null;
    }

    public void deleteAttivazione(int idAttivazione) {
        if (idAttivazione > 0) {
            try{
                this.attivazioneRepository.deleteAttivazione(idAttivazione);
            }catch(Exception e){
            }
        }
    }

    public HashSet<Attivazione> getAllAttivazioni() {
        return this.attivazioneRepository.getAllAttivazioni();
    }
    // </editor-fold>
    //<editor-fold desc=“Attuatore">
    public Integer addAttuatore(Attuatore attuatore) {
        if (attuatore != null) {
            try {
                return this.attuatoreRepository.addAttuatore(attuatore);
            } catch (Exception e){
                return null;
            }
        }
        return null;
    }
    public HashSet<Attuatore> getAllAttuatori() {
        return this.attuatoreRepository.getAllAttuatori();
    }
    public void deleteAttuatore(int idAttuatore) {
        if (idAttuatore >0) {
            try {
                this.attuatoreRepository.deleteAttuatore(idAttuatore);
            } catch (Exception e){
            }
        }
    }
    // </editor-fold>
    //<editor-fold desc=“MISURA">
    public Integer addMisura(Misura misura) {
        if (misura != null) {
            try {
                return this.misuraRepository.addMisura(misura);
            } catch (Exception e){
            }
        }
        return 0;
    }
    public HashSet<Misura> getAllMisure() {
        return this.misuraRepository.getAllMisure();
    }
    public void deleteMisura(int idMisura) {
        if (idMisura >0) {
            try {
                this.misuraRepository.deleteMisura(idMisura);
            } catch (Exception e){
            }
        }
    }
    // </editor-fold>
    //<editor-fold desc=“PostoAuto">
    public Integer addPostoAuto(PostoAuto postoAuto) {
        if (postoAuto != null) {
            try {
                return this.postoAutoRepository.addPostoAuto(postoAuto);
            } catch (Exception e){
            }
        }
        return 0;
    }
    public HashSet<PostoAuto> getAllPostiAuto() {
        try {
            // Utilizza il repository per ottenere i dati
            return postoAutoRepository.getAllPostiAuto();
        } catch (Exception e) {
            logger.error("Errore durante il recupero di tutti i posti auto", e);
            return new HashSet<>();
        }
    }    public void deletePostoAuto(int idPostoAuto) {
        if (idPostoAuto >0) {
            try {
                this.postoAutoRepository.deletePostoAuto(idPostoAuto);
            } catch (Exception e){
            }
        }
    }

    public int countPostiOccupati() {
        try {
            return postoAutoRepository.countPostiOccupati();
        } catch (Exception e) {
            logger.error("Errore durante il conteggio dei posti auto occupati", e);
            return 0;
        }
    }


    public Map<String, Object> getStatoParcheggio() {
        Map<String, Object> statoParcheggio = new HashMap<>();
        try {
            int postiOccupati = this.postoAutoRepository.countPostiOccupati();
            int postiLiberi = this.postoAutoRepository.countPostiLiberi();

            statoParcheggio.put("postiOccupati", postiOccupati);
            statoParcheggio.put("postiLiberi", postiLiberi);

            logger.info("Stato del parcheggio recuperato con successo: Occupati={}, Liberi={}", postiOccupati, postiLiberi);
        } catch (Exception e) {
            logger.error("Errore durante il recupero dello stato del parcheggio", e);
        }

        return statoParcheggio;
    }





    public boolean updatePostoAuto(PostoAuto postoAuto) {
        try {
            PostoAutoRepository postoAutoRepository = new PostoAutoRepository();
            return postoAutoRepository.updatePostoAuto(postoAuto);
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del PostoAuto nel GlobalRepository", e);
            return false;
        }
    }

    // </editor-fold>
    //<editor-fold desc=“Prenotazione">
    public Integer addPrenotazione(Prenotazione prenotazione) {
        if (prenotazione != null) {
            try {
                return this.prenotazioneRepository.addPrenotazione(prenotazione);
            } catch (Exception e){
            }
        }
        return 0;
    }
    public HashSet<Prenotazione> getAllPrenotazioni() {
        return this.prenotazioneRepository.getAllPrenotazioni();
    }
    public void deletePrenotazione(int id) {
        if (id >0) {
            try {
                this.prenotazioneRepository.deletePrenotazione(id);
            } catch (Exception e){
            }
        }
    }
    public Prenotazione getPrenotazioneById(int id) {
        return this.prenotazioneRepository.getPrenotazioneById(id);
    }
    public boolean existsPrenotazione(int id) {
        if (id >0) {
            return this.prenotazioneRepository.existsPrenotazione(id);
        }
        return false;
    }
    // </editor-fold>

    //<editor-fold desc=“RICHIESTARICARICA">

    public Integer addRichiestaRicarica(RichiestaRicarica richiestaRicarica) {
        if (richiestaRicarica != null) {
            try {
                return this.richiestaRicaricaRepository.addRichiestaRicarica(richiestaRicarica);
            } catch (Exception e){
            }
        }
        return 0;
    }
    public HashSet<RichiestaRicarica> getAllRichiesteRicarica() {
        return this.richiestaRicaricaRepository.getAllRichiesteRicarica();
    }
    public void deleteRichiestaRicarica(int id) {
        if (id >0) {
            try {
                this.richiestaRicaricaRepository.deleteRichiestaRicarica(id);
            } catch (Exception e){
            }
        }
    }

    // </editor-fold>

    //<editor-fold desc=“AUTOELETTRICA">
    public Integer addAutoElettrica(AutoElettrica autoElettrica) {
        if (autoElettrica != null) {
            try {
                return this.autoElettricaRepository.addAutoElettrica(autoElettrica);
            } catch (Exception e){
                return null;
            }
        }
        return null;
    }


    public void deleteAutoElettrica(int idAutoElettrica) {
        if (idAutoElettrica >0) {
            try {
                this.autoElettricaRepository.deleteAutoElettrica(idAutoElettrica);
            } catch (Exception e){
            }
        }
    }
    public HashSet<AutoElettrica> getAllAutoElettrica() {
        return this.autoElettricaRepository.getAllAutoElettriche();
    }
    // </editor-fold>
    //<editor-fold desc=“MWBot">


    public Integer addMWBot(MWBot mwBot) {
        if (mwBot != null) {
            try {
                return this.mwBotRepository.addMWBot(mwBot);
            } catch (Exception e){
                return null;
            }
        }
        return null;
    }

    public void deleteMWBot(int idMWBot) {
        if (idMWBot >0) {
            try {
                this.mwBotRepository.deleteMWBot(idMWBot);
            } catch (Exception e){
            }
        }
    }
    public HashSet<MWBot> getAllMWBots() {
        return this.mwBotRepository.getAllMWBots();
    }
    // </editor-fold>
    //<editor-fold desc=“Saldo">
    public boolean addSaldo(int userId, double amount) {
        return this.saldoRepository.addSaldo(userId, amount);
    }
    public boolean removeSaldo(int userId, double amount) {
        return this.saldoRepository.removeSaldo(userId, amount);
    }
    public int insertSaldo(int userId){
        return this.saldoRepository.insertSaldo(userId);
    }
    // </editor-fold>
    //<editor-fold desc=“Prices">
    public List<FixedPrice> getAllPrices(){
        return this.fixedPriceRepository.getAllPrices();
    }
    public boolean updatePrice(String operationName, double newPrice){
        if(operationName != null && operationName != "" && newPrice > 0) {
            return this.fixedPriceRepository.updatePrice(operationName, newPrice);
        }
        return false;
    }
    public double getPriceByOperation(String operationName){
        if(operationName != null && operationName != "") {
            return this.fixedPriceRepository.getPriceByOperation(operationName);
        }
        return 0;
    }
    // </editor-fold>



}
