package cn.sbx0.microservices.uno.logic;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.RedisTestSetup;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.constant.CardColor;
import cn.sbx0.microservices.uno.constant.GameRedisKey;
import cn.sbx0.microservices.uno.constant.CardPoint;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static cn.sbx0.microservices.uno.TestDataProvider.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.UUID;

/**
 * @author sbx0
 * @since 2022/5/13
 */
@SuppressWarnings({ "SpringJavaAutowiredMembersInspection" })
@ExtendWith(SpringExtension.class)
@MockBean(classes = { IGameRoomUserService.class, IMessageService.class, RandomBot.class })
class BasicGameRuleTest extends RedisTestSetup {
    @Autowired
    private IGameRoomUserService userService;

    @Autowired
    private BasicGameRule gameRule;

    @TestConfiguration
    static class Configuration {
        @Bean
        public BasicGameRule service() {
            return new BasicGameRule();
        }
    }

    @Test
    void getCurrentGamer() {
        given(valueOperations.get(anyString())).willReturn("1");
        given(userService.listByGameRoom(anyString())).willReturn(GAMERS);

        AccountVO currentGamer = gameRule.getCurrentGamer(ROOM_CODE);
        assertNotNull(currentGamer);
    }

    @Test
    void canIPlayNow() {
        getCurrentGamer();
        boolean result = gameRule.canIPlayNow(ROOM_CODE, USER_ID);
        assertTrue(result);
    }

    @Test
    void judgeIsCanPlay() {
        CardEntity previous = new CardEntity(UUID.randomUUID().toString(), CardColor.BLUE, CardPoint.FIVE, 1L);
        CardEntity current = new CardEntity(UUID.randomUUID().toString(), CardColor.BLUE, CardPoint.NINE, 2L);
        assertTrue(gameRule.judgeIsCanPlay(previous, current, 2L));
    }

    @Test
    void judgePenaltyCards() {
        CardEntity previous = new CardEntity(UUID.randomUUID().toString(), CardColor.BLUE, CardPoint.WILD_DRAW_FOUR, 1L);
        CardEntity current = new CardEntity(UUID.randomUUID().toString(), CardColor.BLUE, CardPoint.NINE, 2L);
        given(valueOperations.get(anyString())).willReturn("1");
        assertFalse(gameRule.judgePenaltyCards(previous, current, ROOM_CODE));

        assertTrue(gameRule.judgePenaltyCards(null, current, ROOM_CODE));
    }

    @Test
    void discardCard() {
        CardEntity card = new CardEntity(UUID.randomUUID().toString(), CardColor.BLUE, CardPoint.FIVE, 1L);
        given(valueOperations.get(anyString())).willReturn("1");
        gameRule.discardCard(ROOM_CODE, card);
    }

    @Test
    void functionCard() {
        CardEntity card = new CardEntity(UUID.randomUUID().toString(), CardColor.BLUE, CardPoint.REVERSE, 1L);
        given(valueOperations.get(GameRedisKey.ROOM_DIRECTION.replaceAll(GameRedisKey.ROOM_CODE, ROOM_CODE))).willReturn(CardPoint.REVERSE);
        given(userService.listByGameRoom(anyString())).willReturn(GAMERS);
        gameRule.functionCard(ROOM_CODE, card);
    }
}
