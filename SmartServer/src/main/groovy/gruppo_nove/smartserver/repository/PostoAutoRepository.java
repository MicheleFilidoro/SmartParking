package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.Attuatore;
import gruppo_nove.smartserver.model.item.PostoAuto;
import gruppo_nove.smartserver.model.item.Sensore;
import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.model.user.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
@Repository
public class PostoAutoRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());

    protected PostoAutoRepository() {}

    protected int addPostoAuto(PostoAuto postoAuto) {
        Connection connection = null;
        int id = 0;
        String query = """
            INSERT INTO postoauto (sensoreOccupazioneId, attuatoreOccupazioneId, occupato)
            VALUES (?, ?, ?);
            """;

        try {
            if (postoAuto.getSensoreOccupazione() == null || postoAuto.getAttuatoreOccupazione() == null) {
                throw new IllegalArgumentException("Sensore o Attuatore del PostoAuto non può essere nullo");
            }

            int sensoreId = (postoAuto.getSensoreOccupazione() != null) ? postoAuto.getSensoreOccupazione().getId() : 0;
            int attuatoreId = (postoAuto.getAttuatoreOccupazione() != null) ? postoAuto.getAttuatoreOccupazione().getId() : 0;

            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, sensoreId);
                statement.setInt(2, attuatoreId);
                statement.setBoolean(3, postoAuto.isOccupato());

                statement.executeUpdate();

                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                    }
                }
            }

            connection.commit();

            logger.info("PostoAuto aggiunto con successo con ID: {}", id);
        } catch (Exception e) {
            logger.error("Errore durante l'inserimento del PostoAuto", e);

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    logger.error("Errore durante l'esecuzione del rollback", ex);
                }
            }

            throw new RuntimeException("Errore durante l'aggiunta del PostoAuto", e);
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
    protected void deletePostoAuto(int idPostoAuto) {
        String query = """
                DELETE FROM postoauto
                WHERE id = ?;
                """;

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idPostoAuto);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("Posto Auto con ID {} eliminato con successo", idPostoAuto);
                } else {
                    logger.warn("Nessun posto auto trovato con ID {}", idPostoAuto);
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();

                    logger.error("Rollback effettuato durante l'eliminazione del posto auto", e);
                }

                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione del posto auto", e);
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

    protected HashSet<PostoAuto> getAllPostiAuto() {
        HashSet<PostoAuto> postiAuto = new HashSet<>();
        String query = """
        SELECT * 
        FROM postoauto;
        """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            logger.info("Recupero di tutti i posti auto");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PostoAuto postoAuto = new PostoAuto();
                    postoAuto.setId(resultSet.getInt("id"));

                    Sensore sensore = new Sensore();
                    sensore.setId(resultSet.getInt("sensoreOccupazioneId"));
                    postoAuto.setSensoreOccupazione(sensore);

                    Attuatore attuatore = new Attuatore();
                    attuatore.setId(resultSet.getInt("attuatoreOccupazioneId"));
                    postoAuto.setAttuatoreOccupazione(attuatore);

                    postoAuto.setOccupato(resultSet.getBoolean("occupato"));

                    postiAuto.add(postoAuto);
                }

                if (postiAuto.isEmpty()) {
                    logger.info("Nessun posto auto trovato.");
                } else {
                    logger.debug("Numero totale di posti auto trovati: {}", postiAuto.size());
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero di tutti i posti auto", e);
        }

        return postiAuto;
    }

    protected boolean updatePostoAuto(PostoAuto postoAuto) {
        String query = """
        UPDATE postoauto
        SET sensoreOccupazioneId = ?, attuatoreOccupazioneId = ?, occupato = ?
        WHERE id = ?;
    """;

        Connection connection = null;
        boolean updated = false;

        try {
            connection = DriverManager.getConnection(this.url);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Recupera gli ID di sensore e attuatore
                int sensoreId = (postoAuto.getSensoreOccupazione() != null) ? postoAuto.getSensoreOccupazione().getId() : 0;
                int attuatoreId = (postoAuto.getAttuatoreOccupazione() != null) ? postoAuto.getAttuatoreOccupazione().getId() : 0;

                // Imposta i parametri nella query
                statement.setInt(1, sensoreId);
                statement.setInt(2, attuatoreId);
                statement.setBoolean(3, postoAuto.isOccupato());
                statement.setInt(4, postoAuto.getId());

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    updated = true;
                    logger.info("PostoAuto con ID {} aggiornato con successo", postoAuto.getId());
                } else {
                    logger.warn("Nessun PostoAuto trovato con ID {}", postoAuto.getId());
                }


                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                    logger.error("Rollback effettuato durante l'aggiornamento del PostoAuto", e);
                }
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'aggiornamento del PostoAuto", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Errore nella chiusura della connessione", e);
                }
            }
        }

        return updated;
    }


    public int countPostiOccupati() {
        String query = """
        SELECT COUNT(*) AS total
        FROM postoauto
        WHERE occupato = true;
    """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            logger.error("Errore durante il conteggio dei posti auto occupati", e);
        }

        return 0; // Valore di default in caso di errore
    }



    public int countPostiLiberi() {
        String query = """
        SELECT COUNT(*) AS total
        FROM postoauto
        WHERE occupato = false;
    """;

        try (Connection connection = DriverManager.getConnection(this.url);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            logger.error("Errore durante il conteggio dei posti auto liberi", e);
        }

        return 0; // Valore di default in caso di errore
    }









}
