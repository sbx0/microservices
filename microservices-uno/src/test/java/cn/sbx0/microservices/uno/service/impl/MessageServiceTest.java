package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mockStatic;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@ExtendWith(SpringExtension.class)
class MessageServiceTest {
    public final static String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    @Autowired
    private IMessageService service;
    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    public void beforeEach() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(0L);
        stpUtilMock.when(StpUtil::getLoginIdAsString).thenReturn("0");
    }

    @AfterEach
    public void afterEach() {
        stpUtilMock.close();
    }

    @Test
    void subscribe() {
        service.subscribe(ROOM_CODE);
    }

    @Test
    void send() {
        service.send("*", "*", "*", "message");
        service.send(ROOM_CODE, "*", "*", "message");
    }

    @TestConfiguration
    static class MessageServiceTestConfiguration {
        @Bean
        public IMessageService service() {
            return new MessageServiceImpl();
        }
    }
}
