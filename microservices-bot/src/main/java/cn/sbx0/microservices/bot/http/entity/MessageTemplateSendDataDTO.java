package cn.sbx0.microservices.bot.http.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/8/24
 */
@Data
public class MessageTemplateSendDataDTO {
    private MessageTemplateSendDataDetailDTO context;
}