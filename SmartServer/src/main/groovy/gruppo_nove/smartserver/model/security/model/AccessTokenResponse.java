package gruppo_nove.smartserver.model.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
}
