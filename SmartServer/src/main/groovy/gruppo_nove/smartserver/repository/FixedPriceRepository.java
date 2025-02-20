package gruppo_nove.smartserver.repository;

import gruppo_nove.smartserver.model.item.FixedPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FixedPriceRepository {
    private final String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/DATABASESMART.db";
    private static final Logger logger = LogManager.getLogger(FixedPriceRepository.class);

    protected List<FixedPrice> getAllPrices() {
        List<FixedPrice> prices = new ArrayList<>();
        String query = "SELECT id, operationName, price FROM fixedPrice";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                prices.add(new FixedPrice(
                        resultSet.getInt("id"),
                        resultSet.getString("operationName"),
                        resultSet.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero dei prezzi fissi", e);
        }
        return prices;
    }

    protected boolean updatePrice(String operationName, double newPrice) {
        String query = "UPDATE fixedPrice SET price = ? WHERE operationName = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, newPrice);
            statement.setString(2, operationName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Prezzo aggiornato per l'operazione {}", operationName);
                return true;
            } else {
                logger.warn("Nessuna operazione trovata con il nome {}", operationName);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'aggiornamento del prezzo per l'operazione {}", operationName, e);
            throw new RuntimeException("Errore durante l'aggiornamento del prezzo", e);
        }
    }

    protected double getPriceByOperation(String operationName) {
        String query = "SELECT price FROM fixedPrice WHERE operationName = ?";
        double price = -1; // Valore di default, utile per indicare che l'operazione non è stata trovata

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, operationName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    price = resultSet.getDouble("price");
                    logger.info("Prezzo trovato per l'operazione {}: {}", operationName, price);
                } else {
                    logger.warn("Nessun prezzo trovato per l'operazione {}", operationName);
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero del prezzo per l'operazione {}", operationName, e);
            throw new RuntimeException("Errore durante il recupero del prezzo", e);
        }

        return price;
    }

}
