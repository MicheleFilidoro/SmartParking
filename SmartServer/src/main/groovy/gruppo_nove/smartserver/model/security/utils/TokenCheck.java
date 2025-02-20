package gruppo_nove.smartserver.model.security.utils;

import jakarta.servlet.http.HttpServletRequest;

public class TokenCheck {
    public static String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
