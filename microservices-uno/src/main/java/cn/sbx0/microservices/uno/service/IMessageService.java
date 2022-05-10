package cn.sbx0.microservices.uno.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author sbx0
 * @since 2022/5/10
 */
public interface IMessageService {
    SseEmitter subscribe(String roomCode);

    void send(String code, String channel, String id, Object message);
}
