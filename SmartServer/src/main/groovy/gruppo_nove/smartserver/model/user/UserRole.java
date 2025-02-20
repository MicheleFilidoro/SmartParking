package gruppo_nove.smartserver.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {

    /**
     * Utente Base user role.
     */
    UTENTEBASE,
    /**
     * Utente Premium user role.
     */
    UTENTEPREMIUM,
    /**
     * Systemadmin user role.
     */
    SYSTEMADMIN,
    /**
     * Mqtt user role.
     */
    MQTT;


    @Override
    public String getAuthority() {
        return this.name();
    }
}
