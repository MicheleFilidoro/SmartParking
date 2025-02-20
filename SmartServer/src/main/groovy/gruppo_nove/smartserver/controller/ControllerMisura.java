package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.Misura;
import gruppo_nove.smartserver.repository.GlobalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/v1/misura")
@RequiredArgsConstructor
public class ControllerMisura {
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerAttuatore.class.getName());

    @PostMapping(value = "/add")
    public ResponseEntity<Integer> addMisura(@RequestBody String param) {
        Gson gson = new Gson();
        Misura misura = gson.fromJson(param, Misura.class);
        return ResponseEntity.ok(this.repository.addMisura(misura));
    }
    @GetMapping(value = "/get/all")
    public String getAllMisure() {
        Gson gson = new Gson();
        HashSet<Misura> misure = this.repository.getAllMisure();
        return gson.toJson(misure);
    }
    @DeleteMapping(value = "/delete/{id}")
    public String deleteMisura(@PathVariable int id) {
        Gson gson = new Gson();
        this.repository.deleteMisura(id);
        return gson.toJson("Ok");
    }
}
