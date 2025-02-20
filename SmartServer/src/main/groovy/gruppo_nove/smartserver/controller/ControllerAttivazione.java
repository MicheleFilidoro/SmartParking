package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.Attivazione;
import gruppo_nove.smartserver.model.item.Sensore;
import gruppo_nove.smartserver.repository.GlobalRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/v1/attivazione")
@RequiredArgsConstructor
public class ControllerAttivazione {
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerSensore.class.getName());

    @PostMapping(value = "/add")
    public ResponseEntity<Integer> addAttivazione(@RequestBody String param) {
        Gson gson = new Gson();
        Attivazione attivazione = gson.fromJson(param, Attivazione.class);
        return ResponseEntity.ok(this.repository.addAttivazione(attivazione));
    }

    @DeleteMapping(value = "/delete/{id}")
    public String deleteAttivazione(@PathVariable int id) {
        Gson gson = new Gson();
        this.repository.deleteAttivazione(id);
        return gson.toJson("OK");
    }

    @GetMapping(value = "/getAll")
    public String getAllAttivazioni(HttpServletRequest request) {
        Gson gson = new Gson();
        HashSet<Attivazione> attivazioni = this.repository.getAllAttivazioni();
        return gson.toJson(attivazioni);
    }

}
