package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.AutoElettrica;

import gruppo_nove.smartserver.model.item.Sensore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

@Repository
public class AutoElettricaRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());
    /**
     * Instantiates a new Users repository.
     */
    protected AutoElettricaRepository() {
    }

    /**
     * Adds autoElettrica
     *
     * @param autoElettrica
     * 		object autoElettrica
     *
     * @return the autoElettrica id
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected int addAutoElettrica(AutoElettrica autoElettrica) {
        int id = 0;
        String queryInsert = """
                INSERT INTO AutoElettrica (Modello,Targa,idUtente,capacitaBatteria)
                VALUES (?, ?, ?, ?);
                """;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(queryInsert,
                    Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, autoElettrica.getModello());
                statement.setString(2, autoElettrica.getTarga());
                statement.setInt(3, autoElettrica.getIdUtente());
                statement.setDouble(4, autoElettrica.getCapacitaBatteria());


                logger.info("Inserimento del modello: {}", autoElettrica.getModello());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);

                        logger.debug("Auto inserita con ID: {}", id);
                    }
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();

                    logger.error("Rollback effettuato durante l'inserimento del auto", e);
                }
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'inserimento dell'auto", e);
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
     * Deletes autoElettrica.
     *
     * @param idAutoElettrica
     * 		id autoElettrica
     *
     * @return void
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected void deleteAutoElettrica(int idAutoElettrica) {
        String query = """
                DELETE FROM AutoElettrica
                WHERE id = ?;
                """;

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idAutoElettrica);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("Auto con ID {} eliminato con successo", idAutoElettrica);
                } else {
                    logger.warn("Nessun auto trovato con ID {}", idAutoElettrica);
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();

                    logger.error("Rollback effettuato durante l'eliminazione del auto", e);
                }

                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione del auto", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Errore nella chiusura della connessione", e);
                }
            }
        }
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

    protected HashSet<AutoElettrica> getAllAutoElettriche() {
        int columns;
        HashMap<String, Object> row;
        ResultSetMetaData resultSetMetaData;
        AutoElettrica autoElettrica = null;
        HashSet<AutoElettrica> AutoElettriche = new HashSet<>();
        String query = """
                SELECT *
                FROM AutoElettrica
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
                autoElettrica = new AutoElettrica(
                        resultSet.getInt("id"),              // Recupero ID dell'auto elettrica
                        resultSet.getString("Modello"),      // Modello dell'auto
                        resultSet.getString("Targa"),        // Targa dell'auto
                        resultSet.getInt("idUtente"),        // ID dell'utente associato
                        resultSet.getInt("capacitaBatteria") // Capacità della batteria
                );


                AutoElettriche.add(autoElettrica);
            }

            if (AutoElettriche.isEmpty()) {
                logger.info("Nessun auto elettrica trovata");
            } else {
                logger.info("Trovate {} AutoElettriche", AutoElettriche.size());
            }

            return AutoElettriche;
        } catch (SQLException e) {
            logger.error("Errore durante il recupero delle auto", e);

            return AutoElettriche;
        }
    }

}
