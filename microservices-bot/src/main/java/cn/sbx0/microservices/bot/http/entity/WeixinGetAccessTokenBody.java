package cn.sbx0.microservices.bot.http.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sbx0
 * @since 2022/8/24
 */
@Data
public class WeixinGetAccessTokenBody {
    @JsonProperty(value = "access_token")
    private String accessToken;
    @JsonProperty(value = "expires_in")
    private Long expiresIn;
}
