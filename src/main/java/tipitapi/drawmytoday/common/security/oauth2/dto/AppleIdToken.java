package tipitapi.drawmytoday.common.security.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleIdToken {

    private String iss;
    private String aud;
    private Long exp;
    private Long iat;
    private String sub;
    private String nonce;
    private String email;
    private Boolean emailVerified;
    private String[] audList;
    private Integer authTime;
    private String nonceSupported;

}
