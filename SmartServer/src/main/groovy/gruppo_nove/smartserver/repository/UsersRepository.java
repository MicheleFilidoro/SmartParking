package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.AutoElettrica;
import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.model.user.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * The type Users repository.
 */
@Repository
public class UsersRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());
    /**
     * Instantiates a new Users repository.
     */
    protected UsersRepository() {
    }

    /**
     * Gets user by username.
     *
     * @param username
     * 		the username
     *
     * @return the user by username
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected UserProfile getUserByUsername(String username) {
        int columns;
        HashMap<String, Object> row;
        ResultSetMetaData resultSetMetaData;
        UserProfile user = null;

        String query = """
				SELECT *
				FROM users
				WHERE username = ?;
				""";

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);

            logger.info("Esecuzione della query per ottenere l'utente con username: {}", username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int attivo = resultSet.getInt("enabled");
                    boolean enabled = attivo != 0;

                    user = new UserProfile(
                            resultSet.getInt("id"),
                            resultSet.getString("nome"),
                            resultSet.getString("cognome"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            UserRole.valueOf(resultSet.getString("role")),
                            enabled
                    );

                    logger.debug("Trovato utente: {}", user.getUsername());
                } else {
                    logger.info("Nessun utente trovato con username: {}", user.getUsername());
                }

                return user;
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero dell'utente con username: {}", username, e);

            return user;
        }
    }
    /**
     * Add user integer.
     *
     * @param user
     * 		the user
     *
     * @return the integer
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected int addUser(UserProfile user) {
        int id = 0;
        String query = """
				INSERT INTO users (nome, cognome, username, email, password, role)
				VALUES (?, ?, ?, ?, ?, ?);
				SELECT last_insert_rowid() AS newId;
				""";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getNome());
                statement.setString(2, user.getCognome());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getEmail());
                statement.setString(5, user.getPassword());
                statement.setString(6, user.getRole().toString());

                logger.info("Esecuzione della query di inserimento per l'utente con username: {}", user.getUsername());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);

                        logger.debug("Utente inserito con ID: {}", id);
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                logger.error("Errore durante il rollback dopo un fallimento nell'inserimento dell'utente", ex);
            }

            logger.error("Errore durante l'inserimento dell'utente", e);
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
     * Exists username boolean.
     *
     * @param username
     * 		the username
     *
     * @return the boolean
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected boolean existsUsername(String username) {
        String sql = """
				SELECT COUNT(*)
				FROM users
				WHERE username = ?;""";

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);

                logger.debug("Risultato della verifica esistenza username '{}': {}", username, count);

                return count > 0;
            } else {
                logger.info("Nessun risultato trovato per username '{}'", username);

                return false;
            }
        } catch (SQLException e) {
            logger.error("Errore durante la verifica dell'esistenza dell'username '{}'", username, e);

            return false;
        }
    }


    /**
     * Exists mail boolean.
     *
     * @param email
     * 		the email
     *
     * @return the boolean
     *
     * @throws SQLException
     * 		the sql exception
     */
    protected boolean existsEmail(String email) {
        String sql = """
				SELECT COUNT(*)
				FROM users
				WHERE email = ?""";

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);

                logger.debug("Risultato della verifica esistenza email '{}': {}", email, count);

                return count > 0;
            } else {
                logger.info("Nessun risultato trovato per email '{}'", email);

                return false;
            }
        } catch (SQLException e) {
            logger.error("Errore durante la verifica dell'esistenza dell'email '{}'", email, e);

            return false;
        }
    }

    /**
     * Gets all user.
     *
     * @param
     *
     * @return Users
     *     Hashset dei users
     *
     * @throws SQLException
     * 		the sql exception
     *      */

    protected HashSet<UserProfile> getAllUser() {
        HashSet<UserProfile> utenti = new HashSet<>();
        String query = """
            
                SELECT *
            FROM users
            """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            logger.info("Ricerca degli utenti.");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UserProfile userProfile = new UserProfile(
                            resultSet.getInt("id"), // ID
                            resultSet.getString("nome"), // Nome
                            resultSet.getString("cognome"), // Cognome
                            resultSet.getString("username"), // Username
                            resultSet.getString("email"), // Email
                            resultSet.getString("password"), // Password
                            UserRole.valueOf(resultSet.getString("role")), // Role (Enum)
                            resultSet.getBoolean("enabled") // Enabled (boolean)
                    );

                    utenti.add(userProfile);
                }

                if (utenti.isEmpty()) {
                    logger.info("Nessun utente trovato.");
                } else {
                    logger.info("Numero di utenti trovati: {}", utenti.size());
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero degli utenti.", e);
        }
        return
    utenti;
        }


    public boolean deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione dell'utente con ID {}: {}", id, e);
            return false;
        }
    }








}
