package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@ExtendWith(SpringExtension.class)
class MatchServiceImplTest {
    @Autowired
    private IMatchService service;
    @MockBean
    private IMessageService messageService;
    @MockBean
    private IGameRoomUserService gameRoomUserService;

    @Test
    void joinOne() {
        MatchExpectDTO dto = new MatchExpectDTO(1L, 2, false);
        ResponseVO<Boolean> joinResponse = service.join(dto);
        assertEquals(ResponseVO.SUCCESS, joinResponse.getCode());
        assertEquals(true, joinResponse.getData());

        ResponseVO<Integer> response = service.getQueueInfo();
        assertEquals(ResponseVO.SUCCESS, response.getCode());
        assertEquals(1, response.getData());

        ResponseVO<Boolean> quitResponse = service.quit(dto.getUserId());
        assertEquals(ResponseVO.SUCCESS, quitResponse.getCode());
        assertEquals(true, quitResponse.getData());
    }

    @Test
    void joinN() {
        Random random = new Random();
        int size = random.nextInt(2, 6);
        for (int i = 0; i < size; i++) {
            ResponseVO<Boolean> joinResponse = service.join(new MatchExpectDTO((long) i, 2, false));
            assertEquals(ResponseVO.SUCCESS, joinResponse.getCode());
            assertEquals(true, joinResponse.getData());
        }
        ResponseVO<Integer> response = service.getQueueInfo();
        assertEquals(ResponseVO.SUCCESS, response.getCode());
        assertEquals(size, response.getData());
    }

    @Test
    void quitFailed() {
        ResponseVO<Boolean> quitResponse = service.quit(1L);
        assertEquals(ResponseVO.FAILED, quitResponse.getCode());
        assertEquals(false, quitResponse.getData());
    }

    @Test
    void match() {
        service.match();
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public IMatchService service() {
            return new MatchServiceImpl();
        }
    }
}
