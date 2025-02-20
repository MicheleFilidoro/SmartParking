package gruppo_nove.smartserver.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserProfile {
    private int id;
    private String nome;
    private String cognome;
    private String username;
    private String email;
    private String password;
    private UserRole role;
    private boolean enabled;


    /**
     * Instantiates a new User profile.
     *
     * @param id
     * 		the id
     * @param nome
     * 		the nome
     * @param cognome
     * 		the cognome
     * @param username
     * 		the username
     * @param email
     * 		the mail
     * @param password
     * 		the password
     * @param role
     * 		the role
     * @param enabled
     * 		the enabled
     * @param saldo
     *      the saldo
     */
    public UserProfile(int id, String nome, String cognome, String username, String email, String password,
                       UserRole role, boolean enabled) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }
    /**
     * Instantiates a new User profile.
     *
     * @param nome
     * 		the nome
     * @param cognome
     * 		the cognome
     * @param username
     * 		the username
     * @param email
     * 		the mail
     * @param password
     * 		the password
     * @param role
     * 		the role
     * @param enabled
     * 		the enabled
     * @param saldo
     *      the saldo
     */
    public UserProfile(String nome, String cognome, String username, String email, String password, UserRole role,
                       boolean enabled) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }
}
