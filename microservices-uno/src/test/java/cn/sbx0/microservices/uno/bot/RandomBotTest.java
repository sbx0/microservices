package cn.sbx0.microservices.uno.bot;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/9
 */
@ExtendWith(SpringExtension.class)
class RandomBotTest {
    public final static String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    public final static Long USER_ID = 1L;
    @Autowired
    private RandomBot bot;

    @MockBean
    private AccountService accountService;
    @MockBean
    private IGameRoomUserService gameRoomUserService;
    @MockBean
    private IGameCardService gameCardService;
    @MockBean
    private RedisTemplate<String, CardEntity> redisTemplate;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private ValueOperations valueOperations;
    @MockBean
    private ListOperations listOperations;

    @BeforeEach
    void beforeEach() {
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForList()).willReturn(listOperations);

        AccountVO account = new AccountVO();
        account.setId(1L);
        given(accountService.findByUserName(anyString())).willReturn(account);
    }

    @Test
    void initId() {
        bot.initId();
    }

    @Test
    void playCard() {
        bot.playCard(ROOM_CODE);
    }

    @Test
    void notifyTest() {
        bot.notify(ROOM_CODE, USER_ID);
    }

    @Test
    void drawCard() {
        bot.drawCard(ROOM_CODE, 7);
    }

    @Test
    void joinRoom() {
        bot.joinRoom(ROOM_CODE);
    }

    @Test
    void quitRoom() {
        bot.quitRoom(ROOM_CODE);
    }

    @TestConfiguration
    static class RandomBotTestTestConfiguration {
        @Bean
        public RandomBot service() {
            return new RandomBot();
        }
    }
}
