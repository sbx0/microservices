package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.uno.entity.MessageDTO;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static cn.sbx0.microservices.uno.TestDataProvider.ROOM_CODE;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@SuppressWarnings({"SpringJavaAutowiredMembersInspection"})
class MessageServiceTest extends BaseServiceImplTest {
    @TestConfiguration
    static class Configuration {
        @Bean
        public IMessageService service() {
            return new MessageServiceImpl();
        }
    }

    @Autowired
    private IMessageService service;

    @Test
    void subscribe() {
        service.subscribe(ROOM_CODE);
    }

    @Test
    void send() {
        service.send(new MessageDTO<>("*", "*", "*", "message"));
        service.send(new MessageDTO<>(ROOM_CODE, "*", "*", "message"));
    }

}
