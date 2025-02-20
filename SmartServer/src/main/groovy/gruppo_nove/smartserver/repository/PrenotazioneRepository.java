package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.Prenotazione;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

@Repository
public class PrenotazioneRepository {

    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());

    protected PrenotazioneRepository() {}

    protected Prenotazione getPrenotazioneById(int id) {
        Prenotazione prenotazione = null;
        String query = """
                SELECT *
                FROM prenotazione
                WHERE id = ? ;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            logger.info("Ricerca della prenotazione con ID {}", id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    prenotazione = new Prenotazione(
                            resultSet.getInt("id"),
                            resultSet.getInt("idPostoAuto"),
                            resultSet.getString("orario_arrivo"),
                            resultSet.getInt("durata_prenotazione"),
                            resultSet.getInt("idUtente"),
                            resultSet.getBoolean("accettata")
                    );
                    // Calcolo orario fine prenotazione
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

                    // Converte la stringa in LocalDateTime
                    LocalDateTime orarioArrivoDateTime = LocalDateTime.parse(prenotazione.getOrario_arrivo(), formatter);

                    prenotazione.setOrario_fine_prenotazione(orarioArrivoDateTime.plusMinutes(prenotazione.getDurata_prenotazione()).toString());

                    logger.info("Prenotazione trovata con ID {}", id);
                } else {
                    logger.info("Nessuna prenotazione trovata con ID {}", id);
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero della prenotazione con ID {}", id, e);
        }
        return prenotazione;
    }

    protected HashSet<Prenotazione> getAllPrenotazioni() {
        HashSet<Prenotazione> prenotazioni = new HashSet<>();
        String query = """
                SELECT *
                FROM prenotazione
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            logger.info("Ricerca delle prenotazioni {}");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Prenotazione prenotazione = new Prenotazione(
                            resultSet.getInt("id"),
                            resultSet.getInt("idPostoAuto"),
                            resultSet.getString("orario_arrivo"),
                            resultSet.getInt("durata_prenotazione"),
                            resultSet.getInt("idUtente"),
                            resultSet.getBoolean("accettata")
                    );
                    // Calcolo orario fine prenotazione
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

                    // Converte la stringa in LocalDateTime
                    LocalDateTime orarioArrivoDateTime = LocalDateTime.parse(prenotazione.getOrario_arrivo(), formatter);

                    prenotazione.setOrario_fine_prenotazione(orarioArrivoDateTime.plusMinutes(prenotazione.getDurata_prenotazione()).toString());
                    prenotazioni.add(prenotazione);
                }

                if (prenotazioni.isEmpty()) {
                    logger.info("Nessuna prenotazione trovata{}");
                } else {
                    logger.info("Numero di prenotazioni trovate: {}", prenotazioni.size());
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero delle prenotazioni{}", e);
        }
        return prenotazioni;
    }

    protected int addPrenotazione(Prenotazione prenotazione) {
        int id = 0;
        String query = """
                INSERT INTO prenotazione (idPostoAuto, orario_arrivo, durata_prenotazione, idUtente)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, prenotazione.getIdPostoAuto());
            statement.setObject(2, prenotazione.getOrario_arrivo());
            statement.setInt(3, prenotazione.getDurata_prenotazione());
            statement.setInt(4, prenotazione.getIdUtente());

            int affectedRows = statement.executeUpdate();
            logger.info("Prenotazione aggiunta per l'utente ID {}", prenotazione.getIdUtente());

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'aggiunta della prenotazione", e);
            throw new RuntimeException("Errore durante l'aggiunta della prenotazione", e);
        }
        return id;
    }

    protected void deletePrenotazione(int id) {
        boolean isDeleted = false;
        String query = """
                DELETE FROM prenotazione
                WHERE id = ? ;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            isDeleted = rowsAffected > 0;

            if (isDeleted) {
                logger.info("Prenotazione eliminata con successo con ID {}", id);
            } else {
                logger.info("Nessuna prenotazione trovata con ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione della prenotazione con ID {}", id, e);
            throw new RuntimeException("Errore durante l'eliminazione della prenotazione", e);
        }
    }
    protected boolean existsPrenotazione(int id) {
        String query = """
            SELECT 1
            FROM prenotazione
            WHERE id = ?;
            """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            logger.info("Verifica esistenza della prenotazione con ID {}", id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Restituisce true se esiste almeno una riga
            }
        } catch (SQLException e) {
            logger.error("Errore durante la verifica dell'esistenza della prenotazione con ID {}", id, e);
            throw new RuntimeException("Errore durante la verifica dell'esistenza della prenotazione", e);
        }
    }

}
