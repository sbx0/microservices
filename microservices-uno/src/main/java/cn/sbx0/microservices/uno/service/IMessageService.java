package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.uno.entity.MessageDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author sbx0
 * @since 2022/5/10
 */
public interface IMessageService {
    SseEmitter subscribe(String roomCode);

    void send(MessageDTO dto);

    void handleOwnerMessage();
}
