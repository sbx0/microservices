package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.entity.QueueInfoVO;
import cn.sbx0.microservices.uno.logic.BasicGameRule;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "unchecked"})
@MockBean(classes = {IGameRoomUserService.class, RandomBot.class, BasicGameRule.class, IMessageService.class})
class MatchServiceImplTest extends BaseServiceImplTest {
    @TestConfiguration
    static class Configuration {
        @Bean
        public IMatchService service() {
            return new MatchServiceImpl();
        }
    }

    @Autowired
    private IMatchService service;

    @Test
    void joinOne() {
        MatchExpectDTO dto = new MatchExpectDTO(1L, 2, false);
        ResponseVO<Boolean> joinResponse = service.join(dto);
        assertEquals(ResponseVO.SUCCESS, joinResponse.getCode());
        assertEquals(true, joinResponse.getData());

        given(setOperations.size(any())).willReturn(1L);
        ResponseVO<QueueInfoVO> response = service.getQueueInfo();
        assertEquals(ResponseVO.SUCCESS, response.getCode());
        assertEquals(1, response.getData().getSize());

        ResponseVO<Boolean> quitResponse = service.quit(dto.getUserId());
        assertEquals(ResponseVO.SUCCESS, quitResponse.getCode());
        assertEquals(true, quitResponse.getData());
    }

    @Test
    void quitFailed() {
        ResponseVO<Boolean> quitResponse = service.quit(1L);
        assertEquals(ResponseVO.SUCCESS, quitResponse.getCode());
        assertEquals(true, quitResponse.getData());
    }

    @Test
    void match() {
        service.match();
    }
}
