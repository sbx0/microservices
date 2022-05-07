package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

/**
 * @author sbx0
 * @since 2022/5/7
 */
@ExtendWith(SpringExtension.class)
class GameRoomUserServiceImplTest {
    public final static String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    public final static Long USER_ID = 1L;
    public final static List<AccountVO> GAMERS = new ArrayList<>();
    public final static List<CardEntity> CARDS = new ArrayList<>();
    public final static String[] UUIDS = new String[10];
    public final static String[] POINTS = {"1", "2", "3", "4", "5", "skip", "wild draw four", "draw two", "reverse", "draw two"};
    public final static String[] COLORS = {"red", "red", "blue", "yellow", "blue", "blue", "blue", "blue", "blue", "blue"};
    @Autowired
    private IGameRoomUserService service;
    @MockBean
    private RedisTemplate<String, CardEntity> redisTemplate;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private ValueOperations valueOperations;
    @MockBean
    private ListOperations listOperations;
    @MockBean
    private IGameRoomService gameRoomService;
    @MockBean
    private RandomBot randomBot;
    private MockedStatic<StpUtil> stpUtilMock;
    @MockBean
    private AccountService accountService;
    @MockBean
    private GameRoomUserMapper mapper;

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

    @BeforeEach
    public void beforeEach() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(0L);
        stpUtilMock.when(StpUtil::getLoginIdAsString).thenReturn("0");
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(stringRedisTemplate.opsForList()).willReturn(listOperations);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForList()).willReturn(listOperations);
    }

    @AfterEach
    public void afterEach() {
        stpUtilMock.close();
    }

    @Test
    void botQuitGameRoom() {
        given(accountService.findByUserName(anyString())).willReturn(GAMERS.get(0));
        given(mapper.quitGameRoom(any())).willReturn(true);
        boolean result = service.botQuitGameRoom(ROOM_CODE, "botName");
        assertTrue(result);
    }

    @Test
    void botJoinGameRoom() {
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(null);
        boolean result = service.botJoinGameRoom(ROOM_CODE, "botName");
        assertFalse(result);

        GameRoomEntity room = new GameRoomEntity();
        room.setRoomStatus(1);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        result = service.botJoinGameRoom(ROOM_CODE, "botName");
        assertFalse(result);

        room.setRoomStatus(0);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        given(accountService.findByUserName(anyString())).willReturn(GAMERS.get(0));
        given(mapper.insert(any())).willReturn(0);
        result = service.botJoinGameRoom(ROOM_CODE, "botName");
        assertFalse(result);

        given(mapper.insert(any())).willReturn(1);
        result = service.botJoinGameRoom(ROOM_CODE, "botName");
        assertTrue(result);
    }

    @Test
    void joinGameRoom() {
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(null);
        boolean result = service.joinGameRoom(ROOM_CODE);
        assertFalse(result);

        GameRoomEntity room = new GameRoomEntity();
        room.setRoomStatus(1);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        result = service.joinGameRoom(ROOM_CODE);
        assertFalse(result);

        room.setRoomStatus(0);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        given(accountService.loginInfo()).willReturn(GAMERS.get(0));
        given(mapper.atomSave(any(), any())).willReturn(false);
        result = service.joinGameRoom(ROOM_CODE);
        assertFalse(result);

        given(mapper.atomSave(any(), any())).willReturn(true);
        result = service.joinGameRoom(ROOM_CODE);
        assertTrue(result);
    }

    @Test
    void quitGameRoom() {
        given(mapper.quitGameRoom(any())).willReturn(true);
        given(accountService.loginInfo()).willReturn(GAMERS.get(0));
        boolean result = service.quitGameRoom(ROOM_CODE);
        assertTrue(result);
    }

    @Test
    void listByGameRoom() {
        GameRoomEntity t = new GameRoomEntity();
        t.setId(0L);
        t.setPlayersSize(2);
        given(gameRoomService.getOneByRoomCode(any())).willReturn(t);
        given(listOperations.size(any())).willReturn(2L);
        ArrayList<GameRoomUserEntity> gamers = new ArrayList<>();
        GameRoomUserEntity user = new GameRoomUserEntity();
        user.setUserId(0L);
        gamers.add(user);
        given(mapper.listByGameRoom(any(), any())).willReturn(gamers);
        List<AccountVO> accounts = service.listByGameRoom(ROOM_CODE);
        assertFalse(CollectionUtils.isEmpty(accounts));
    }

    @Test
    void countByGameRoom() {
        given(gameRoomService.getOneByRoomCode(any())).willReturn(null);
        Integer result = service.countByGameRoom(ROOM_CODE);
        assertEquals(0, result);

        GameRoomEntity t = new GameRoomEntity();
        t.setId(1L);
        given(gameRoomService.getOneByRoomCode(any())).willReturn(t);
        given(mapper.countByGameRoom(any())).willReturn(1);
        result = service.countByGameRoom(ROOM_CODE);
        assertEquals(1, result);
    }

    @Test
    void isIAmIn() {
        given(mapper.alreadyJoinByCreateUserId(any())).willReturn(null);
        Boolean result = service.isIAmIn(1L, 1L);
        assertFalse(result);

        GameRoomUserEntity t = new GameRoomUserEntity();
        t.setRoomId(1L);
        given(mapper.alreadyJoinByCreateUserId(any())).willReturn(t);
        result = service.isIAmIn(1L, 1L);
        assertTrue(result);
    }

    @TestConfiguration
    static class GameRoomUserServiceImplTestConfiguration {
        @Bean
        public IGameRoomUserService service() {
            return new GameRoomUserServiceImpl();
        }
    }
}
