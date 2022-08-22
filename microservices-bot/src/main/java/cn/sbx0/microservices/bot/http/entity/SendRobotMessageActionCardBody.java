package cn.sbx0.microservices.bot.http.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sbx0
 * @since 2022/7/25
 */
@Data
public class SendRobotMessageActionCardBody {
    @JsonProperty(value = "title")
    private String title;
    @JsonProperty(value = "text")
    private String text;
    @JsonProperty(value = "hideAvatar")
    private String hideAvatar;
    @JsonProperty(value = "btnOrientation")
    private String btnOrientation;
    @JsonProperty(value = "singleTitle")
    private String singleTitle;
    @JsonProperty(value = "singleURL")
    private String singleUrl;
}
