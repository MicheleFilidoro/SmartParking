package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.Sensore;
import gruppo_nove.smartserver.model.security.services.TokenService;
import gruppo_nove.smartserver.model.security.utils.TokenCheck;
import gruppo_nove.smartserver.model.user.UserRole;
import gruppo_nove.smartserver.repository.GlobalRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/v1/sensore")
@RequiredArgsConstructor
public class ControllerSensore {
    private final TokenService tokenService;
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerSensore.class.getName());

    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<?> addSensore(@RequestBody String param, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.SYSTEMADMIN)) {
            Gson gson = new Gson();
            Sensore sensore = gson.fromJson(param, Sensore.class);
            int result = this.repository.addSensore(sensore);
            logger.info("Sensore aggiunto con ID {}", result);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<?> deleteSensore(@PathVariable int id, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.SYSTEMADMIN)) {
            try {
                boolean isDeleted = this.repository.deleteSensore(id);
                if (isDeleted) {
                    logger.info("Sensore eliminato con successo con ID {}", id);
                    return ResponseEntity.ok("Sensore eliminato con successo");
                } else {
                    logger.warn("Sensore non trovato con ID {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sensore non trovato");
                }
            } catch (Exception e) {
                logger.error("Errore durante l'eliminazione del sensore con ID {}: {}", id, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno del server");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }

    @GetMapping(value = "/get/all")
    @PreAuthorize("hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<?> getAllSensori(HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.SYSTEMADMIN)) {
            try {
                Gson gson = new Gson();
                HashSet<Sensore> sensori = this.repository.getAllSensori();
                logger.info("Recuperati {} sensori", sensori.size());
                return ResponseEntity.ok(gson.toJson(sensori));
            } catch (Exception e) {
                logger.error("Errore durante il recupero dei sensori: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno del server");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }
}
