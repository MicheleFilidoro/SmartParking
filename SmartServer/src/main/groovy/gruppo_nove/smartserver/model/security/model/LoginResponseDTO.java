package gruppo_nove.smartserver.model.security.model;

import gruppo_nove.smartserver.model.user.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Login response dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private UserProfile user;
    private String jwt;
    private boolean enabled;
}
