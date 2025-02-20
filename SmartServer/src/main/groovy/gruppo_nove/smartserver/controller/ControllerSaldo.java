package gruppo_nove.smartserver.controller;

import com.google.gson.Gson;
import gruppo_nove.smartserver.model.security.services.TokenService;
import gruppo_nove.smartserver.model.security.utils.TokenCheck;
import gruppo_nove.smartserver.model.user.UserRole;
import gruppo_nove.smartserver.repository.GlobalRepository;
import gruppo_nove.smartserver.model.request.SaldoRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/saldo")
@RequiredArgsConstructor
public class ControllerSaldo {
    private final TokenService tokenService;
    private final GlobalRepository repository;
    private static final Logger logger = LogManager.getLogger(ControllerSaldo.class.getName());

    /**
     * Aggiunge saldo per un utente specificato.
     *
     * @param param JSON contenente userId e amount.
     * @return ResponseEntity con il risultato dell'operazione.
     */
    @PostMapping(value = "/add")
    @PreAuthorize("hasAuthority('UTENTEPREMIUM') or hasAuthority('UTENTEBASE')")
    public ResponseEntity<String> addSaldo(@RequestBody String param, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.UTENTEPREMIUM) || this.tokenService.validateTokenAndRole(jwt, UserRole.UTENTEBASE)) {
            Gson gson = new Gson();
            SaldoRequest requestObj = gson.fromJson(param, SaldoRequest.class);

            try {
                boolean success = this.repository.addSaldo(requestObj.getUserId(), requestObj.getAmount());
                if (success) {
                    logger.info("Saldo aggiunto con successo per l'utente ID {}", requestObj.getUserId());
                    return ResponseEntity.ok(gson.toJson("Saldo aggiunto con successo."));
                } else {
                    logger.warn("Impossibile aggiungere il saldo per l'utente ID {}", requestObj.getUserId());
                    return ResponseEntity.badRequest().body(gson.toJson("Impossibile aggiungere il saldo."));
                }
            } catch (Exception e) {
                logger.error("Errore durante l'aggiunta di saldo per l'utente ID {}", requestObj.getUserId(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson("Errore durante l'aggiunta di saldo."));
            }
        } else {
            logger.warn("Accesso non autorizzato per aggiungere saldo");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }

    /**
     * Rimuove saldo per un utente specificato.
     *
     * @param param JSON contenente userId e amount.
     * @return ResponseEntity con il risultato dell'operazione.
     */
    @PostMapping(value = "/remove")
    @PreAuthorize("hasAuthority('UTENTEPREMIUM') or hasAuthority('UTENTEBASE')")
    public ResponseEntity<String> removeSaldo(@RequestBody String param, HttpServletRequest request) {
        String jwt = TokenCheck.extractTokenFromRequest(request);

        if (this.tokenService.validateTokenAndRole(jwt, UserRole.UTENTEPREMIUM) || this.tokenService.validateTokenAndRole(jwt, UserRole.UTENTEBASE)) {
            Gson gson = new Gson();
            SaldoRequest requestObj = gson.fromJson(param, SaldoRequest.class);

            try {
                boolean success = this.repository.removeSaldo(requestObj.getUserId(), requestObj.getAmount());
                if (success) {
                    logger.info("Saldo rimosso con successo per l'utente ID {}", requestObj.getUserId());
                    return ResponseEntity.ok(gson.toJson("Saldo rimosso con successo."));
                } else {
                    logger.warn("Impossibile rimuovere il saldo per l'utente ID {}", requestObj.getUserId());
                    return ResponseEntity.badRequest().body(gson.toJson("Impossibile rimuovere il saldo."));
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Saldo insufficiente per l'utente ID {}", requestObj.getUserId(), e);
                return ResponseEntity.badRequest().body(gson.toJson(e.getMessage()));
            } catch (Exception e) {
                logger.error("Errore durante la rimozione di saldo per l'utente ID {}", requestObj.getUserId(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson("Errore durante la rimozione di saldo."));
            }
        } else {
            logger.warn("Accesso non autorizzato per rimuovere saldo");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accesso negato");
        }
    }
}

