package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.Attivazione;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashSet;

@Repository
public class AttivazioneRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());

    protected AttivazioneRepository() {}

    protected int addAttivazione(Attivazione attivazione) {
        int id = 0;
        String query = """
                INSERT INTO attivazione (current, time, id_attuatore)
                VALUES (?, ?, ?);
                """;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setBoolean(1, attivazione.isCurrent());
                statement.setString(2, attivazione.getTime());
                statement.setLong(3, attivazione.getIdAttuatore());

                logger.info("Aggiunta attivazione per l'attuatore ID {}", attivazione.getIdAttuatore());

                statement.executeUpdate();

                String sqlSensore = """
                        UPDATE sensore
                        SET enabled = ?
                        WHERE id = ?;
                        """;

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                    }
                }

                try (PreparedStatement stmnt = connection.prepareStatement(sqlSensore)) {
                    stmnt.setBoolean(1, attivazione.isCurrent());
                    stmnt.setInt(2, attivazione.getIdAttuatore());

                    statement.executeUpdate();
                }

                connection.commit();
            }
        } catch (SQLException e) {
            logger.error("Errore di connessione durante l'aggiunta dell'attivazione", e);

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    logger.error("Errore durante l'esecuzione del rollback", ex);
                }
            }

            throw new RuntimeException("Errore di connessione", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Errore nella chiusura della connessione", e);
                }
            }
        }

        return id;
    }

    protected void deleteAttivazione(int idAttivazione) {
        String query = """
                DELETE FROM attivazione
                WHERE id = ? ;
                """;

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            logger.info("Tentativo di eliminazione della attivazione: {}", idAttivazione);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idAttivazione);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.debug("Attivazione '{}' eliminata con successo.", idAttivazione);
                } else {
                    logger.info("Nessuna attivazione trovato con id '{}'.", idAttivazione);
                }
            }

            connection.commit();
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione della attivazione '{}'", idAttivazione, e);

            if (connection != null) {
                try {
                    connection.rollback();

                    logger.info("Rollback eseguito a seguito di un errore.");
                } catch (SQLException ex) {
                    logger.error("Errore durante l'esecuzione del rollback", ex);
                }
            }

            throw new RuntimeException("Errore durante l'eliminazione del posto auto", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();

                    logger.info("Connessione chiusa.");
                } catch (SQLException e) {
                    logger.error("Errore nella chiusura della connessione", e);
                }
            }
        }
    }

    protected HashSet<Attivazione> getAllAttivazioni() {
        HashSet<Attivazione> attivazioni = new HashSet<>();
        String query = """
                SELECT * 
                FROM attivazione;
                """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            logger.info("Recupero di tutte le attivazioni");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Attivazione attivazione = new Attivazione(
                            resultSet.getInt("id"),
                            resultSet.getString("time"),
                            resultSet.getBoolean("current"),
                            resultSet.getInt("id_attuatore")
                    );

                    attivazioni.add(attivazione);
                }

                if (attivazioni.isEmpty()) {
                    logger.info("Nessuna attivazione trovata.");
                } else {
                    logger.debug("Numero totale di attivazioni trovate: {}", attivazioni.size());
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero di tutte le attivazioni", e);
        }

        return attivazioni;
    }
}
