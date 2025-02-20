package gruppo_nove.smartserver.controller;


import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.AutoElettrica;
import gruppo_nove.smartserver.model.item.Sensore;
import gruppo_nove.smartserver.repository.GlobalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping(value = "/api/v1/autoelettrica")
@RequiredArgsConstructor

public class ControllerAutoElettrica {

    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerAutoElettrica.class.getName());

    @PostMapping(value = "/add")
    public ResponseEntity<Integer> addAutoElettrica(@RequestBody String param) {
        Gson gson = new Gson();
        AutoElettrica autoElettrica = gson.fromJson(param, AutoElettrica.class);
        return ResponseEntity.ok(this.repository.addAutoElettrica(autoElettrica));
    }
    @DeleteMapping(value = "/delete/{id}")
    public String deleteAutoElettrica(@PathVariable int id) {
        Gson gson = new Gson();
        this.repository.deleteAutoElettrica(id);
        return gson.toJson("Ok");
    }

    @GetMapping(value = "/get/all")
    public String getAllAutoElettrica() {
        Gson gson = new Gson();
        HashSet<AutoElettrica> autoElettriche = this.repository.getAllAutoElettrica();
        return gson.toJson(autoElettriche);
    }

}
