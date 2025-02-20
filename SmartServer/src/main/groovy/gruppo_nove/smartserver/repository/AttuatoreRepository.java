package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.Attuatore;
import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.model.user.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
@Repository
public class AttuatoreRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());

    protected AttuatoreRepository() {}
    /**
     * Gets attuatore by id.
     *
     * @param idAttuatore
     * 		the id
     *
     * @return the attuatore by id
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected Attuatore getAttuatorebyId(int idAttuatore) {
        Attuatore attuatore = null;
        String query = """
				SELECT *
				FROM attuatore
				WHERE id = ? ;
				""";

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, idAttuatore);

            logger.info("Recupero dell'attuatore con ID {}", idAttuatore);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    attuatore = new Attuatore(
                            resultSet.getInt("id"),
                            resultSet.getString("nome")
                    );

                    logger.info("Attuatore trovato: {}", attuatore.getNome());
                } else {
                    logger.info("Nessun attuatore trovato con ID {}", idAttuatore);
                }

                return attuatore;
            }

        } catch (SQLException e) {
            logger.error("Errore durante il recupero dell'attuatore con ID {}", idAttuatore, e);

            return attuatore;
        }
    }
    /**
     * Add user integer.
     *
     * @param attuatore
     * 		the user
     *
     * @return the integer
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected int addAttuatore(Attuatore attuatore) {
        Connection connection = null;
        int id = 0;
        String query = """
				INSERT INTO attuatore (nome)
				VALUES (?);
				""";

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, attuatore.getNome());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                    }
                }
            }

            connection.commit();

            logger.info("Attuatore aggiunto con successo con ID: {}", id);
        } catch (Exception e) {
            logger.error("Errore durante l'inserimento dell'attuatore", e);

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    logger.error("Errore durante l'esecuzione del rollback", ex);
                }
            }

            throw new RuntimeException("Errore durante l'aggiunta dell'attuatore", e);
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
    /**
     * Gets all attuatore.
     *
     *
     *
     * @return all the attuatore by id
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected HashSet<Attuatore> getAllAttuatori() {
        HashSet<Attuatore> attuatori = new HashSet<>();
        String query = """
            SELECT * 
            FROM attuatore;
            """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            logger.info("Recupero di tutti gli attuatori");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Attuatore attuatore = new Attuatore(
                            resultSet.getInt("id"),
                            resultSet.getString("nome")
                    );

                    attuatori.add(attuatore);
                }

                if (attuatori.isEmpty()) {
                    logger.info("Nessun attuatore trovato.");
                } else {
                    logger.debug("Numero totale di attuatori trovati: {}", attuatori.size());
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero di tutti gli attuatori", e);
        }

        return attuatori;
    }
    /**
     * delete attuatore by id.
     * @param idAttuatore
      * 		id attuatore
      *
      * @return void
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected void deleteAttuatore(int idAttuatore) {
        String query = """
                DELETE FROM attuatore
                WHERE id = ?;
                """;

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idAttuatore);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("Attuatore con ID {} eliminato con successo", idAttuatore);
                } else {
                    logger.warn("Nessun attuatore trovato con ID {}", idAttuatore);
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();

                    logger.error("Rollback effettuato durante l'eliminazione dell'attuatore", e);
                }

                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione dell'attuatore", e);
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

}
