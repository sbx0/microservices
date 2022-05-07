package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.constant.GameRedisKeyConstant;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@SuppressWarnings({"unchecked", "rawtypes", "SpringJavaAutowiredMembersInspection"})
@ExtendWith(SpringExtension.class)
class GameCardServiceImplTest {
    public final static String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    public final static Long USER_ID = 1L;
    public final static List<AccountVO> GAMERS = new ArrayList<>();
    public final static List<CardEntity> CARDS = new ArrayList<>();
    public final static String[] UUIDS = new String[10];
    public final static String[] POINTS = {"1", "2", "3", "4", "5", "skip", "wild draw four", "draw two", "reverse", "draw two"};
    public final static String[] COLORS = {"red", "red", "blue", "yellow", "blue", "blue", "blue", "blue", "blue", "blue"};
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
    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    public void beforeEach() {
        stpUtilMock = mockStatic(StpUtil.class);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForList()).willReturn(listOperations);
    }

    @AfterEach
    public void afterEach() {
        stpUtilMock.close();
    }

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
    void drawCard() {
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(USER_ID);

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

    @Test
    void playCard() {
        // currentGamerStr is null
        String key = GameRedisKeyConstant.CURRENT_GAMER.replaceAll(GameRedisKeyConstant.ROOM_CODE, ROOM_CODE);
        given(valueOperations.get(key)).willReturn(null);
        boolean result = service.playCard(ROOM_CODE, "test", "red", USER_ID);
        assertFalse(result);

        // gamers are null
        given(userService.listByGameRoom(ROOM_CODE)).willReturn(Collections.emptyList());
        result = service.playCard(ROOM_CODE, "test", "red", USER_ID);
        assertFalse(result);

        // currentGamer is null
        List<AccountVO> nullList = new ArrayList<>();
        nullList.add(null);
        given(userService.listByGameRoom(ROOM_CODE)).willReturn(nullList);
        result = service.playCard(ROOM_CODE, "test", "red", USER_ID);
        assertFalse(result);

        // currentGamer is not bot
        given(userService.listByGameRoom(ROOM_CODE)).willReturn(GAMERS);
        result = service.playCard(ROOM_CODE, "test", "red", 2L);
        assertFalse(result);

        // currentCard is null
        String userCards = GameRedisKeyConstant.USER_CARDS.replaceAll(GameRedisKeyConstant.ROOM_CODE, ROOM_CODE)
                .replaceAll(GameRedisKeyConstant.USER_ID, "0");
        given(listOperations.size(userCards)).willReturn(7L);
        given(listOperations.range(userCards, 0, 7)).willReturn(CARDS);
        result = service.playCard(ROOM_CODE, "test", "red", 0L);
        assertFalse(result);

        // judgeIsCanPlay false
        CardEntity previousCard = new CardEntity();
        previousCard.setUuid(UUID.randomUUID().toString());
        previousCard.setPoint("draw two");
        previousCard.setColor("blue");
        previousCard.setUserId(USER_ID);
        given(listOperations.index(GameRedisKeyConstant.ROOM_DISCARDS.replaceAll(GameRedisKeyConstant.ROOM_CODE, ROOM_CODE), 0))
                .willReturn(previousCard);
        result = service.playCard(ROOM_CODE, UUIDS[0], "red", 0L);
        assertFalse(result);

        // judgePenaltyCards false
        given(valueOperations.get(GameRedisKeyConstant.ROOM_PENALTY.replaceAll(GameRedisKeyConstant.ROOM_CODE, ROOM_CODE)))
                .willReturn("4");
        result = service.playCard(ROOM_CODE, UUIDS[2], "blue", 0L);
        assertFalse(result);

        // canPlay true
        result = service.playCard(ROOM_CODE, UUIDS[7], "blue", 0L);
        assertTrue(result);

        given(valueOperations.get(GameRedisKeyConstant.ROOM_PENALTY.replaceAll(GameRedisKeyConstant.ROOM_CODE, ROOM_CODE)))
                .willReturn("0");

        // reverse
        result = service.playCard(ROOM_CODE, UUIDS[8], "blue", 0L);
        assertTrue(result);

        // wild draw four
        result = service.playCard(ROOM_CODE, UUIDS[6], "blue", 0L);
        assertTrue(result);

        // skip
        result = service.playCard(ROOM_CODE, UUIDS[5], "blue", 0L);
        assertTrue(result);

        // normal
        previousCard = new CardEntity();
        previousCard.setUuid(UUID.randomUUID().toString());
        previousCard.setPoint("2");
        previousCard.setColor("blue");
        previousCard.setUserId(USER_ID);
        given(listOperations.index(GameRedisKeyConstant.ROOM_DISCARDS.replaceAll(GameRedisKeyConstant.ROOM_CODE, ROOM_CODE), 0))
                .willReturn(previousCard);
        result = service.playCard(ROOM_CODE, UUIDS[4], "blue", 0L);
        assertTrue(result);
    }

    @Test
    void nextPlay() {
        given(listOperations.rightPop(anyString())).willReturn(CARDS.get(0));
        given(userService.listByGameRoom(ROOM_CODE)).willReturn(GAMERS);
        given(valueOperations.get(anyString())).willReturn("1");
        List<CardEntity> cards = service.nextPlay(ROOM_CODE, USER_ID);
        assertNotNull(cards);
        assertEquals(1, cards.size());

        given(valueOperations.get(anyString())).willReturn("0");
        String drawKey = GameRedisKeyConstant.ROOM_DRAW.replaceAll(GameRedisKeyConstant.ROOM_CODE, ROOM_CODE);
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
        List<CardEntity> cards = service.discardCardList(ROOM_CODE);
        assertEquals(CARDS.size(), cards.size());
        given(listOperations.size(anyString())).willReturn((long) CARDS.size());
        given(listOperations.range(anyString(), anyLong(), anyLong())).willReturn(CARDS);
        cards = service.discardCardList(ROOM_CODE);
        assertEquals(CARDS.size(), cards.size());
    }

    @TestConfiguration
    static class GameCardServiceImplTestConfiguration {
        @Bean
        public IGameCardService service() {
            return new GameCardServiceImpl();
        }
    }
}
