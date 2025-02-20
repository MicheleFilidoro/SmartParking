package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.Misura;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashSet;

@Repository
public class MisuraRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());

    protected MisuraRepository() {}

    protected int addMisura(Misura misura) {
        int id=0;
        String query = """
                INSERT INTO misura (value, time, id_sensore)
                VALUES (?, ?, ?);
                """;

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDouble(1, misura.getValue());
                statement.setString(2, misura.getTime());
                statement.setInt(3, misura.getIdSensore());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);

                        logger.info("Misura aggiunta con ID {}", id);
                    }
                }

                connection.commit();
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }

                logger.error("Errore durante l'aggiunta della misura: {}", misura, e);
            } catch (SQLException ex) {
                logger.error("Errore durante il rollback", ex);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Errore durante la chiusura della connessione", e);
                }
            }
        }
        return id;
    }

    protected void deleteMisura(int idMisura) {
        String query = """
                DELETE FROM misura
                WHERE id = ? ;
                """;

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idMisura);

                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    logger.debug("Misura con ID {} eliminata correttamente", idMisura);
                } else {
                    logger.info("Nessuna misura trovata con ID {}", idMisura);
                }

                connection.commit();
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione della coltivazione con ID: {}", idMisura, e);

            try {
                if (connection != null) {
                    connection.rollback();

                    logger.info("Eseguito rollback a seguito di un errore");
                }
            } catch (SQLException ex) {
                logger.error("Errore durante il rollback dell'eliminazione della coltivazione", ex);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Errore durante la chiusura della connessione al database", e);
                }
            }
        }
    }

    protected HashSet<Misura> getAllMisure() {
        HashSet<Misura> misure = new HashSet<>();
        String query = """
                SELECT * 
                FROM misura;
                """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            logger.info("Recupero di tutte le misure");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Misura misura = new Misura(
                            resultSet.getInt("id"),
                            resultSet.getDouble("value"),
                            resultSet.getString("time"),
                            resultSet.getInt("id_sensore")
                    );

                    misure.add(misura);
                }

                if (misure.isEmpty()) {
                    logger.info("Nessuna misura trovata nel database.");
                } else {
                    logger.debug("Numero totale di misure trovate: {}", misure.size());
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero di tutte le misure", e);
        }

        return misure;
    }
}
