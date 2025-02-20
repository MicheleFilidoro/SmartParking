package gruppo_nove.smartserver.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class SaldoRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(SaldoRepository.class.getName());

    protected SaldoRepository() {}

    /**
     * Aggiunge un importo al saldo dell'utente.
     *
     * @param userId l'ID dell'utente.
     * @param amount l'importo da aggiungere.
     * @return true se l'operazione ha avuto successo, false altrimenti.
     */
    protected boolean addSaldo(int userId, double amount) {
        String query = """
                UPDATE saldo
                SET saldo = saldo + ?
                WHERE userId = ?;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, amount);
            statement.setInt(2, userId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Aggiunto {} al saldo dell'utente ID {}", amount, userId);
                return true;
            } else {
                logger.warn("Nessun saldo trovato per l'utente ID {}", userId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'aggiunta di saldo per l'utente ID {}", userId, e);
            throw new RuntimeException("Errore durante l'aggiunta di saldo", e);
        }
    }

    /**
     * Rimuove un importo dal saldo dell'utente, se sufficiente.
     *
     * @param userId l'ID dell'utente.
     * @param amount l'importo da rimuovere.
     * @return true se l'operazione ha avuto successo, false altrimenti.
     * @throws IllegalArgumentException se il saldo è insufficiente.
     */
    protected boolean removeSaldo(int userId, double amount) {
        String checkQuery = "SELECT saldo FROM saldo WHERE userId = ?";
        String updateQuery = """
                UPDATE saldo
                SET saldo = saldo - ?
                WHERE userId = ?;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            // Controlla il saldo attuale
            checkStatement.setInt(1, userId);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    double currentSaldo = resultSet.getDouble("saldo");
                    if (currentSaldo < amount) {
                        throw new IllegalArgumentException("Saldo insufficiente per l'utente ID " + userId);
                    }
                } else {
                    logger.warn("Nessun saldo trovato per l'utente ID {}", userId);
                    return false;
                }
            }

            // Rimuove l'importo dal saldo
            updateStatement.setDouble(1, amount);
            updateStatement.setInt(2, userId);

            int affectedRows = updateStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Rimosso {} dal saldo dell'utente ID {}", amount, userId);
                return true;
            } else {
                logger.warn("Impossibile aggiornare il saldo per l'utente ID {}", userId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Errore durante la rimozione di saldo per l'utente ID {}", userId, e);
            throw new RuntimeException("Errore durante la rimozione di saldo", e);
        }
    }
    /**
     * Crea un nuovo record nella tabella saldo con un saldo iniziale di 0.
     *
     * @param userId l'ID dell'utente.
     * @return true se l'operazione ha avuto successo, false altrimenti.
     */
    protected int insertSaldo(int userId) {
        int id = 0;
        String queryInsert = """
            INSERT INTO saldo (saldo, userId)
            VALUES (0, ?);
            """;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(queryInsert,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, userId);

                logger.info("Creazione del saldo per l'utente con ID: {}", userId);

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                        logger.debug("Saldo creato con ID: {}", id);
                    }
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                    logger.error("Rollback effettuato durante la creazione del saldo", e);
                }
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante la creazione del saldo", e);
            return id;
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


}
