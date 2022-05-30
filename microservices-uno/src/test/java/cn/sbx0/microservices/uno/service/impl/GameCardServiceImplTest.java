package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.constant.GameRedisKey;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.logic.BasicGameRule;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static cn.sbx0.microservices.uno.TestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@SuppressWarnings({"unchecked", "SpringJavaAutowiredMembersInspection"})
@MockBean(classes = {IGameRoomUserService.class, IGameRoomService.class, RandomBot.class, BasicGameRule.class, IMessageService.class})
class GameCardServiceImplTest extends BaseServiceImplTest {
    @Autowired
    private IGameRoomUserService userService;
    @Autowired
    private IGameCardService service;
    @Autowired
    private BasicGameRule gameRule;

    @Test
    void drawCard() {
        stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(USER_ID);

        given(listOperations.rightPop(anyString())).willReturn(CARDS.get(0));

        List<CardEntity> cards = service.drawCard(ROOM_CODE);

        assertNotNull(cards);
        assertEquals(7, cards.size());

        given(valueOperations.get(anyString())).willReturn("4");
        cards = service.drawCard(ROOM_CODE, 1);
        assertNotNull(cards);
        assertEquals(4, cards.size());

        given(valueOperations.get(anyString())).willReturn("4");
        cards = service.drawCard(ROOM_CODE, 7);
        assertNotNull(cards);
        assertEquals(11, cards.size());

        given(valueOperations.get(anyString())).willReturn("0");
        given(listOperations.rightPop(anyString())).willReturn(null);
        cards = service.drawCard(ROOM_CODE, 0L, 0);
        assertNull(cards);
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public IGameCardService service() {
            return new GameCardServiceImpl();
        }
    }

    @Test
    void playCardMain() {
        given(gameRule.canIPlayNow(any(), any())).willReturn(true);
        given(gameRule.judgeIsCanPlay(any(), any(), any())).willReturn(true);
        given(gameRule.judgePenaltyCards(any(), any(), any())).willReturn(true);

        given(listOperations.size(any())).willReturn((long) CARDS.size());
        given(listOperations.range(any(), anyLong(), anyLong())).willReturn(CARDS);
        given(listOperations.index(any(), anyLong())).willReturn(CARDS.get(1));

        given(userService.getGamerByCode(anyString())).willReturn(GAMERS);

        service.playCard(ROOM_CODE, CARDS.get(0).getUuid(), CARDS.get(0).getColor(), USER_ID);
    }

    @Test
    void nextPlay() {
        given(listOperations.rightPop(anyString())).willReturn(CARDS.get(0));
        given(userService.getGamerByCode(ROOM_CODE)).willReturn(GAMERS);
        given(valueOperations.get(anyString())).willReturn("1");
        List<CardEntity> cards = service.nextPlay(ROOM_CODE, USER_ID);
        assertNotNull(cards);
        assertEquals(1, cards.size());

        given(valueOperations.get(anyString())).willReturn("0");
        String drawKey = GameRedisKey.ROOM_DRAW.replaceAll(GameRedisKey.ROOM_CODE, ROOM_CODE);
        given(valueOperations.get(drawKey)).willReturn("2");
        cards = service.nextPlay(ROOM_CODE, USER_ID);
        assertNotNull(cards);
        assertEquals(1, cards.size());

        given(valueOperations.get(drawKey)).willReturn("1");
        cards = service.nextPlay(ROOM_CODE, USER_ID);
        assertEquals(0, cards.size());
    }

    @Test
    void myCardList() {
        given(listOperations.size(anyString())).willReturn(null);
        given(listOperations.range(anyString(), anyLong(), anyLong())).willReturn(CARDS);
        List<CardEntity> cards = service.myCardList(ROOM_CODE);
        assertEquals(CARDS.size(), cards.size());
        given(listOperations.size(anyString())).willReturn((long) CARDS.size());
        given(listOperations.range(anyString(), anyLong(), anyLong())).willReturn(CARDS);
        cards = service.myCardList(ROOM_CODE);
        assertEquals(CARDS.size(), cards.size());
    }

    @Test
    void initGame() {
        service.initGame(ROOM_CODE);
    }

    @Test
    void discardCardList() {
        given(listOperations.size(anyString())).willReturn(null);
        given(listOperations.range(anyString(), anyLong(), anyLong())).willReturn(CARDS);
        List<CardEntity> cards = service.getDiscardCards(ROOM_CODE);
        assertEquals(CARDS.size(), cards.size());
        given(listOperations.size(anyString())).willReturn((long) CARDS.size());
        given(listOperations.range(anyString(), anyLong(), anyLong())).willReturn(CARDS);
        cards = service.getDiscardCards(ROOM_CODE);
        assertEquals(CARDS.size(), cards.size());
    }
}
