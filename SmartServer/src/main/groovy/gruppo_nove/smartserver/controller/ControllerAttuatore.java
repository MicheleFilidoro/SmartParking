package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.Attuatore;
import gruppo_nove.smartserver.model.item.Sensore;
import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.repository.GlobalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/v1/attuatore")
@RequiredArgsConstructor
public class ControllerAttuatore {

    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerAttuatore.class.getName());


    @PostMapping(value = "/add")
    public ResponseEntity<Integer> addAttuatore(@RequestBody String param) {
        Gson gson = new Gson();
        Attuatore attuatore = gson.fromJson(param, Attuatore.class);
        return ResponseEntity.ok(this.repository.addAttuatore(attuatore));
    }
    @GetMapping(value = "/get/all")
    public String getAllAttuatore() {
        Gson gson = new Gson();
        HashSet<Attuatore> attuatore = this.repository.getAllAttuatori();
        return gson.toJson(attuatore);
    }
    @DeleteMapping(value = "/delete/{id}")
    public String deleteAttuatore(@PathVariable int id) {
        Gson gson = new Gson();
        this.repository.deleteAttuatore(id);
        return gson.toJson("Ok");
    }


}

