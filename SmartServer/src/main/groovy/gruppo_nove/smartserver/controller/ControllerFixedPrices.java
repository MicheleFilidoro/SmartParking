package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.FixedPrice;
import gruppo_nove.smartserver.model.request.UpdatePriceRequest;
import gruppo_nove.smartserver.repository.GlobalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/fixedprices")
@RequiredArgsConstructor
public class ControllerFixedPrices {
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerAutoElettrica.class.getName());

    @GetMapping(value = "/get/all")
    public String getAllPrices() {
        Gson gson = new Gson();
        List<FixedPrice> fixedPrices= this.repository.getAllPrices();
        return gson.toJson(fixedPrices);
    }
    @PostMapping(value = "/update")
    public ResponseEntity<String> updatePrice(@RequestBody String param) {
        Gson gson = new Gson();
        UpdatePriceRequest request = gson.fromJson(param, UpdatePriceRequest.class);
        try {
            boolean success = this.repository.updatePrice(request.getOperationName(), request.getPrice());
            if (success) {
                return ResponseEntity.ok(gson.toJson("Prezzo aggiornato con successo."));
            } else {
                return ResponseEntity.badRequest().body(gson.toJson("Impossibile aggiornare il prezzo."));
            }
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del prezzo", e);
            return ResponseEntity.internalServerError().body(gson.toJson("Errore durante l'aggiornamento del prezzo."));
        }
    }
    @GetMapping(value = "/get/{operationName}")
    public ResponseEntity<String> getPriceByOperation(@PathVariable String operationName) {
        Gson gson = new Gson();
        try {
            double price = this.repository.getPriceByOperation(operationName);
            if (price > 0) {
                return ResponseEntity.ok(gson.toJson(price));
            } else {
                return ResponseEntity.badRequest().body(gson.toJson("Operazione non trovata o prezzo non valido."));
            }
        } catch (Exception e) {
            logger.error("Errore durante il recupero del prezzo per l'operazione: {}", operationName, e);
            return ResponseEntity.internalServerError().body(gson.toJson("Errore durante il recupero del prezzo."));
        }
    }


}
