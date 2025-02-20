package gruppo_nove.smartserver.controller;


import com.google.gson.Gson;
import gruppo_nove.smartserver.model.item.Prenotazione;
import gruppo_nove.smartserver.model.security.services.TokenService;
import gruppo_nove.smartserver.model.security.utils.TokenCheck;
import gruppo_nove.smartserver.model.user.UserProfile;
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
@RequestMapping("/api/v1/prenotazione")
@RequiredArgsConstructor
public class ControllerPrenotazione {
    private final TokenService tokenService;
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerPrenotazione.class.getName());

    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('UTENTEPREMIUM') or hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<?> addPrenotazione(@RequestBody String param, HttpServletRequest request) {
        Gson gson = new Gson();
        UserProfile user;
        String jwt = TokenCheck.extractTokenFromRequest(request);
        String username;

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.UTENTEPREMIUM)) {
            username = tokenService.extractUsernameFromToken(jwt);
            if (username != null) {
                user = repository.getUserByUsername(username);
                double operationPrice = repository.getPriceByOperation("prenotazione");

                // Controlla se il saldo è sufficiente e rimuovilo
                if (repository.removeSaldo(user.getId(), operationPrice)) {
                    Prenotazione prenotazione = gson.fromJson(param, Prenotazione.class);
                    return ResponseEntity.ok(this.repository.addPrenotazione(prenotazione));
                } else {
                    // Risposta in caso di saldo insufficiente
                    return ResponseEntity.badRequest().body("Saldo insufficiente per completare la prenotazione.");
                }
            } else {
                return ResponseEntity.badRequest().body("Username non valido o inesistente.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('UTENTEPREMIUM') or hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<?> deletePrenotazione(@PathVariable int id, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.UTENTEPREMIUM)) {
            if (this.repository.existsPrenotazione(id)) {
                this.repository.deletePrenotazione(id);
                return ResponseEntity.ok("Prenotazione eliminata con successo.");
            } else {
                return ResponseEntity.badRequest().body("Prenotazione non trovata.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }

    @GetMapping(value = "/get/all")
    @PreAuthorize("hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<?> getAllPrenotazioni(HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.SYSTEMADMIN)) {
            HashSet<Prenotazione> prenotazioni = this.repository.getAllPrenotazioni();
            return ResponseEntity.ok(prenotazioni);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }

    @GetMapping(value = "/getbyid/{id}")
    @PreAuthorize("hasAuthority('UTENTEPREMIUM') or hasAuthority('SYSTEMADMIN')")
    public ResponseEntity<?> getPrenotazioneById(@PathVariable int id, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.UTENTEPREMIUM)) {
            Prenotazione prenotazione = this.repository.getPrenotazioneById(id);
            if (prenotazione != null) {
                return ResponseEntity.ok(prenotazione);
            } else {
                return ResponseEntity.badRequest().body("Prenotazione non trovata.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }
}
