package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.MWBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

@Repository
public class MWBotRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(MWBotRepository.class.getName());

    /**
     * Instantiates a new MWBot repository.
     */
    protected MWBotRepository() {
    }

    /**
     * Adds MWBot.
     *
     * @param mwBot object MWBot
     * @return the MWBot ID
     */
    protected int addMWBot(MWBot mwBot) {
        int id = 0;
        String queryInsert = """
                INSERT INTO mwbot (stato, id_auto_elettrica, percentuale_carica)
                VALUES (?, ?, ?);
                """;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, mwBot.getStato());
                statement.setString(2, mwBot.getIdAutoElettrica());
                statement.setInt(3, mwBot.getPercentualeCarica());

                logger.info("Inserimento del MWBot con stato: {}", mwBot.getStato());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                        logger.debug("MWBot inserito con ID: {}", id);
                    }
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                    logger.error("Rollback effettuato durante l'inserimento del MWBot", e);
                }
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'inserimento del MWBot", e);
        } finally {
            closeConnection(connection);
        }
        return id;
    }

    /**
     * Deletes MWBot.
     *
     * @param idMWBot ID MWBot
     */
    protected void deleteMWBot(int idMWBot) {
        String query = """
                DELETE FROM mwbot
                WHERE id = ?;
                """;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idMWBot);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("MWBot con ID {} eliminato con successo", idMWBot);
                } else {
                    logger.warn("Nessun MWBot trovato con ID {}", idMWBot);
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                    logger.error("Rollback effettuato durante l'eliminazione del MWBot", e);
                }
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione del MWBot", e);
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * Gets all MWBots.
     *
     * @return HashSet di MWBot
     */
    protected HashSet<MWBot> getAllMWBots() {
        int columns;
        HashMap<String, Object> row;
        ResultSetMetaData resultSetMetaData;
        MWBot mwBot = null;
        HashSet<MWBot> mwBots = new HashSet<>();

        String query = """
                SELECT * FROM mwbot
                """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            logger.info("Esecuzione della query per ottenere tutti i MWBot");

            resultSetMetaData = resultSet.getMetaData();
            columns = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {
                row = new HashMap<>(columns);

                for (int i = 1; i <= columns; ++i) {
                    row.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                }
                mwBot = new MWBot(
                        resultSet.getInt("id"),
                        resultSet.getString("stato"),
                        resultSet.getString("id_auto_elettrica"),
                        resultSet.getInt("percentuale_carica")
                );

                mwBots.add(mwBot);
            }

            if (mwBots.isEmpty()) {
                logger.info("Nessun MWBot trovato");
            } else {
                logger.info("Trovati {} MWBot", mwBots.size());
            }

            return mwBots;
        } catch (SQLException e) {
            logger.error("Errore durante il recupero dei MWBot", e);
            return mwBots;
        }
    }

    /**
     * Closes the database connection.
     *
     * @param connection
     */
    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Errore nella chiusura della connessione", e);
            }
        }
    }
}
