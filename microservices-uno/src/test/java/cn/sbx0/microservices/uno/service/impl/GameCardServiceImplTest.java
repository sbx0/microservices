package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@ExtendWith(SpringExtension.class)
class GameCardServiceImplTest {
    public final static String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    public final static List<AccountVO> GAMERS = new ArrayList<>();
    public final static List<CardEntity> CARDS = new ArrayList<>();
    public final static String[] UUIDS = new String[10];
    public final static String[] POINTS = {"1", "2", "3", "4", "5", "6", "7", "draw two", "9", "draw two"};
    public final static String[] COLORS = {"red", "red", "blue", "yellow", "green", "red", "green", "black", "red", "red"};
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private IGameCardService service;
    @MockBean
    private RedisTemplate<String, CardEntity> redisTemplate;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private ValueOperations valueOperations;
    @MockBean
    private ListOperations listOperations;
    @MockBean
    private IGameRoomUserService userService;
    @MockBean
    private IGameRoomService gameRoomService;
    @MockBean
    private RandomBot randomBot;

    @BeforeAll
    static void beforeAll() {
        for (int i = 0; i < 6; i++) {
            AccountVO account = new AccountVO();
            account.setId((long) i);
            account.setUsername("username" + i);
            account.setNickname("nickname" + i);
            account.setNumberOfCards(10);
            account.setEmail("email" + i + "@sbx0.cn");
            GAMERS.add(account);
        }

        for (int i = 0; i < 10; i++) {
            CardEntity card = new CardEntity();
            UUIDS[i] = UUID.randomUUID().toString();
            card.setUuid(UUIDS[i]);
            card.setPoint(POINTS[i]);
            card.setColor(COLORS[i]);
            card.setUserId(0L);
            CARDS.add(card);
        }
    }

    @Test
    void botPlayCard() {
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);

        // currentGamerStr is null
        given(valueOperations.get("currentGamer:" + ROOM_CODE)).willReturn(null);
        boolean result = service.botPlayCard(ROOM_CODE, "test", "red", 1L);
        assertFalse(result);

        // gamers are null
        given(userService.listByGameRoom(ROOM_CODE)).willReturn(Collections.emptyList());
        result = service.botPlayCard(ROOM_CODE, "test", "red", 1L);
        assertFalse(result);

        // currentGamer is null
        List<AccountVO> nullList = new ArrayList<>();
        nullList.add(null);
        given(userService.listByGameRoom(ROOM_CODE)).willReturn(nullList);
        result = service.botPlayCard(ROOM_CODE, "test", "red", 1L);
        assertFalse(result);

        // currentGamer is not bot
        given(userService.listByGameRoom(ROOM_CODE)).willReturn(GAMERS);
        result = service.botPlayCard(ROOM_CODE, "test", "red", 2L);
        assertFalse(result);

        given(redisTemplate.opsForList()).willReturn(listOperations);

        // currentCard is null
        given(listOperations.size("cards:" + ROOM_CODE + ":" + 0L)).willReturn(7L);
        given(listOperations.range("cards:" + ROOM_CODE + ":" + 0, 0, 7)).willReturn(CARDS);
        result = service.botPlayCard(ROOM_CODE, "test", "red", 0L);
        assertFalse(result);

        // judgeIsCanPlay false
        CardEntity previousCard = new CardEntity();
        previousCard.setUuid(UUID.randomUUID().toString());
        previousCard.setPoint("draw two");
        previousCard.setColor("blue");
        previousCard.setUserId(1L);
        given(listOperations.index("cards:" + ROOM_CODE + ":discard", 0)).willReturn(previousCard);
        result = service.botPlayCard(ROOM_CODE, UUIDS[0], "red", 0L);
        assertFalse(result);

        // judgePenaltyCards false
        given(valueOperations.get("penaltyCards:" + ROOM_CODE)).willReturn("4");
        result = service.botPlayCard(ROOM_CODE, UUIDS[2], "blue", 0L);
        assertFalse(result);

        // canPlay true
        result = service.botPlayCard(ROOM_CODE, UUIDS[7], "blue", 0L);
        assertTrue(result);
    }

    @Test
    void getCardListById() {
    }

    @Test
    void initCardDeck() {
    }

    @Test
    void drawCard() {
    }

    @Test
    void testDrawCard() {
    }

    @Test
    void myCardList() {
    }

    @Test
    void playCard() {
    }

    @Test
    void botNextPlay() {
    }

    @Test
    void nextPlay() {
    }

    @Test
    void discardCard() {
    }

    @Test
    void discardCardList() {
    }

    @Test
    void initGame() {
    }

    @TestConfiguration
    static class GameCardServiceImplTestConfiguration {
        @Bean
        public IGameCardService service() {
            return new GameCardServiceImpl();
        }
    }
}
