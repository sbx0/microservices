package cn.sbx0.microservices.bot.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/7/26
 */
@Data
public class MessageEntity {
    private String title;
    private String text;
    private String buttonText;
    private String buttonUrl;
}
