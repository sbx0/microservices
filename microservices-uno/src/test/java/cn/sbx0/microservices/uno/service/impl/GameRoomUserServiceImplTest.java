package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.constant.GameRoomStatus;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.sbx0.microservices.uno.TestDataProvider.GAMERS;
import static cn.sbx0.microservices.uno.TestDataProvider.ROOM_CODE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/7
 */
@SuppressWarnings({"unchecked", "SpringJavaAutowiredMembersInspection"})
@MockBean(classes = {IGameRoomService.class, RandomBot.class, RandomBot.class, AccountService.class, GameRoomUserMapper.class, IMessageService.class})
class GameRoomUserServiceImplTest extends BaseServiceImplTest {
    @Autowired
    private IGameRoomService gameRoomService;

    @Autowired
    private IGameRoomUserService service;
    @Autowired
    private AccountService accountService;

    @TestConfiguration
    static class GameRoomUserServiceImplTestConfiguration {
        @Bean
        public IGameRoomUserService service() {
            return new GameRoomUserServiceImpl();
        }
    }

    @MockBean
    private GameRoomUserMapper mapper;

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
        room.setRoomStatus(GameRoomStatus.BEGINNING);
        room.setPlayersSize(2);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        result = service.botJoinGameRoom(ROOM_CODE, "botName");
        assertFalse(result);

        room.setRoomStatus(GameRoomStatus.INITIAL);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        given(accountService.findByUserName(anyString())).willReturn(GAMERS.get(0));
        given(mapper.atomSave(any(), anyInt())).willReturn(false);
        result = service.botJoinGameRoom(ROOM_CODE, "botName");
        assertFalse(result);

        given(mapper.atomSave(any(), anyInt())).willReturn(true);
        result = service.botJoinGameRoom(ROOM_CODE, "botName");
        assertTrue(result);
    }

    @Test
    void joinGameRoom() {
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(null);
        boolean result = service.joinGameRoom(ROOM_CODE);
        assertFalse(result);

        GameRoomEntity room = new GameRoomEntity();
        room.setRoomStatus(GameRoomStatus.BEGINNING);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        result = service.joinGameRoom(ROOM_CODE);
        assertFalse(result);

        room.setRoomStatus(GameRoomStatus.INITIAL);
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
        List<AccountVO> accounts = service.getGamerByCode(ROOM_CODE);
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

    @Test
    void createGameRoomByUserIds() {
        given(gameRoomService.create(any(), anyLong())).willReturn(ROOM_CODE);
        GameRoomEntity room = new GameRoomEntity();
        room.setId(1L);
        room.setRoomCode(ROOM_CODE);
        room.setPlayersSize(1);
        given(gameRoomService.getOneByRoomCode(ROOM_CODE)).willReturn(room);
        AccountVO account = new AccountVO();
        account.setNickname("test");
        given(accountService.findById(anyLong())).willReturn(account);
        Set<Long> ids = new HashSet<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        String result = service.createGameRoomByUserIds(ids);
        assertEquals(ROOM_CODE, result);
    }
}
