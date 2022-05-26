package cn.sbx0.microservices.uno.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/5/25
 */
@Data
public class MessageDTO<T> {
    private String code;
    private String channel;
    private String userId;
    private T message;

    public MessageDTO() {
    }

    public MessageDTO(String code, String channel, String userId, T message) {
        this.code = code;
        this.channel = channel;
        this.userId = userId;
        this.message = message;
    }
}
