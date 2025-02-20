package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.Sensore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

@Repository
public class SensoreRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());
    /**
     * Instantiates a new Users repository.
     */
    protected SensoreRepository() {
    }

    /**
     * Adds sensore.
     *
     * @param sensore
     * 		object sensore
     *
     * @return the sensore id
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected int addSensore(Sensore sensore) {
        int id = 0;
        String queryInsert = """
                INSERT INTO sensore (nome, type)
                VALUES (?, ?);
                """;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(queryInsert,
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, sensore.getNome());
                statement.setString(2, sensore.getType());

                logger.info("Inserimento del sensore: {}", sensore.getNome());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);

                        logger.debug("Sensore inserito con ID: {}", id);
                    }
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();

                    logger.error("Rollback effettuato durante l'inserimento del sensore", e);
                }
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'inserimento del sensore", e);
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

    /**
     * Deletes sensore.
     *
     * @param idSensore
     * 		id sensore
     *
     * @return void
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected boolean deleteSensore(int idSensore) {
        String query = """
            DELETE FROM sensore
            WHERE id = ?;
            """;

        Connection connection = null;
        boolean isDeleted = false;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idSensore);

                int rowsAffected = statement.executeUpdate();
                isDeleted = rowsAffected > 0;

                if (isDeleted) {
                    logger.info("Sensore con ID {} eliminato con successo", idSensore);
                } else {
                    logger.warn("Nessun sensore trovato con ID {}", idSensore);
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                    logger.error("Rollback effettuato durante l'eliminazione del sensore", e);
                }
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione del sensore", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Errore nella chiusura della connessione", e);
                }
            }
        }
        return isDeleted;
    }


/**
 * Gets all sensore.
 *
 * @param
 *
 * @return Sensori
 *     Hashset dei sensori
 *
 * @throws SQLException
 * 		the sql exception
 *      */

    protected HashSet<Sensore> getAllSensori() {
        int columns;
        HashMap<String, Object> row;
        ResultSetMetaData resultSetMetaData;
        Sensore sensore = null;
        HashSet<Sensore> Sensori = new HashSet<>();
        String query = """
                SELECT *
                FROM sensore
                """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            logger.info("Esecuzione della query per ottenere gli utenti");

            resultSetMetaData = resultSet.getMetaData();
            columns = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {
                row = new HashMap<>(columns);

                for (int i = 1; i <= columns; ++i) {
                    row.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                }
                sensore = new Sensore(
                        resultSet.getInt("id"),       // Recupero ID del sensore
                        resultSet.getString("nome"),  // Nome del sensore
                        resultSet.getString("type"),
                        resultSet.getBoolean("enabled")
                );

                Sensori.add(sensore);
            }

            if (Sensori.isEmpty()) {
                logger.info("Nessun sensore trovato");
            } else {
                logger.info("Trovati {} sensori", Sensori.size());
            }

            return Sensori;
        } catch (SQLException e) {
            logger.error("Errore durante il recupero dei sensori", e);

            return Sensori;
        }
    }
}
