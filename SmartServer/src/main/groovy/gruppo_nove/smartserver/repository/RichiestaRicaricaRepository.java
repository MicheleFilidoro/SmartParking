package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.RichiestaRicarica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashSet;

@Repository
public class RichiestaRicaricaRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir")+"/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(UsersRepository.class.getName());

    protected RichiestaRicaricaRepository() {}

    protected int addRichiestaRicarica(RichiestaRicarica richiestaRicarica) {
        int id = 0;
        String query = """
                INSERT INTO richiesta_ricarica (idUtente, idAutoElettrica, PercentualeDesiderata, orarioLimite)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, richiestaRicarica.getIdUtente());
            statement.setInt(2, richiestaRicarica.getIdAutoElettrica());
            statement.setInt(3, richiestaRicarica.getPercentualeDesiderata());
            statement.setString(4, richiestaRicarica.getOrarioLimite());

            int affectedRows = statement.executeUpdate();
            logger.info("RichiestaRicarica aggiunta per l'utente ID {}", richiestaRicarica.getIdUtente());

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'aggiunta della richiesta ricarica", e);
            throw new RuntimeException("Errore durante l'aggiunta della richiesta ricarica", e);
        }
        return id;
    }
    protected boolean deleteRichiestaRicarica(int id) {
        boolean isDeleted = false;
        String query = """
                DELETE FROM richiesta_ricarica
                WHERE id = ? ;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            isDeleted = rowsAffected > 0;

            if (isDeleted) {
                logger.info("Richiesta ricarica eliminata con successo con ID {}", id);
            } else {
                logger.info("Nessuna richiesta ricarica trovata con ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'eliminazione della richiesta ricarica con ID {}", id, e);
            throw new RuntimeException("Errore durante l'eliminazione della richiesta ricarica", e);
        }
        return isDeleted;
    }
    protected HashSet<RichiestaRicarica> getAllRichiesteRicarica() {
        HashSet<RichiestaRicarica> richiesteRicarica = new HashSet<>();
        String query = """
                SELECT id, idUtente, idAutoElettrica, PercentualeDesiderata, orarioLimite, accettata
                FROM richiesta_ricarica;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            logger.info("Ricerca delle richieste ricarica");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RichiestaRicarica richiestaRicarica = new RichiestaRicarica(
                            resultSet.getInt("id"),
                            resultSet.getInt("idUtente"),
                            resultSet.getInt("idAutoElettrica"),
                            resultSet.getInt("PercentualeDesiderata"),
                            resultSet.getString("orarioLimite"),
                            resultSet.getBoolean("accettata")

                    );
                    richiesteRicarica.add(richiestaRicarica);
                }

                if (richiesteRicarica.isEmpty()) {
                    logger.info("Nessuna richiesta ricarica trovata per l'utente");
                } else {
                    logger.info("Numero di richieste ricarica trovate: {}", richiesteRicarica.size());
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero delle richieste ricarica", e);
        }
        return richiesteRicarica;
    }
}
