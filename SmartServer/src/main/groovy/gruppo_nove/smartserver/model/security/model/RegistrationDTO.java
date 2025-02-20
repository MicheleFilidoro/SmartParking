package gruppo_nove.smartserver.model.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Registration dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {

    private String nome;
    private String cognome;
    private String username;
    private String email;
    private String password;
    private int role;

}
