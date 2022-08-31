package cn.sbx0.microservices.bot.http.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sbx0
 * @since 2022/8/24
 */
@Data
public class MessageTemplateSendDTO {
    @JsonProperty(value = "touser")
    private String touser;
    @JsonProperty(value = "template_id")
    private String templateId;
    @JsonProperty(value = "topcolor")
    private String topcolor;
    @JsonProperty(value = "url")
    private String url;
    private MessageTemplateSendDataDTO data;
}
