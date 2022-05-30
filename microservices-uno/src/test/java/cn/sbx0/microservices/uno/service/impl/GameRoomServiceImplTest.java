package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomInfoVO;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import static cn.sbx0.microservices.uno.TestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

/**
 * @author sbx0
 * @since 2022/5/7
 */
@SuppressWarnings({"SpringJavaAutowiredMembersInspection"})
@MockBean(classes = {GameRoomMapper.class, IGameRoomUserService.class, ApplicationInfoManager.class, IGameCardService.class, RandomBot.class, IMessageService.class})
class GameRoomServiceImplTest extends BaseServiceImplTest {
    @Autowired
    private GameRoomMapper mapper;

    @Autowired
    private IGameRoomService service;
    @Autowired
    private IGameRoomUserService userService;
    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    @TestConfiguration
    static class Configuration {
        @Bean
        public IGameRoomService service() {
            return new GameRoomServiceImpl();
        }
    }

    @Test
    void choose() {
        given(mapper.getOneByRoomCode(ROOM_CODE)).willReturn(null);
        String choose = service.choose(ROOM_CODE);
        assertNull(choose);

        GameRoomEntity entity = new GameRoomEntity();
        entity.setInstanceId("instanceId");

        given(mapper.getOneByRoomCode(ROOM_CODE)).willReturn(entity);

        choose = service.choose(ROOM_CODE);
        assertEquals(entity.getInstanceId(), choose);
    }

    @Test
    void create() {
        GameRoomCreateDTO dto = new GameRoomCreateDTO();
        given(mapper.alreadyCreatedButUnusedRoomsByCreateUserId(USER_ID)).willReturn(null);
        given(mapper.insert(any())).willReturn(1);
        given(applicationInfoManager.getInfo()).willReturn(InstanceInfo.Builder.newBuilder().setAppName("test").build());
        String roomCode = service.create(dto, 0);
        assertTrue(StringUtils.hasText(roomCode));
    }

    @Test
    void getOneByRoomCode() {
        given(mapper.getOneByRoomCode(ROOM_CODE)).willReturn(new GameRoomEntity());
        GameRoomEntity entity = service.getOneByRoomCode(ROOM_CODE);
        assertNotNull(entity);
    }

    @Test
    void info() {
        given(mapper.getOneByRoomCode(ROOM_CODE)).willReturn(null);
        GameRoomInfoVO info = service.info(ROOM_CODE);
        assertNull(info);

        GameRoomEntity entity = new GameRoomEntity();
        entity.setId(1L);
        given(mapper.getOneByRoomCode(ROOM_CODE)).willReturn(entity);
        given(userService.isIAmIn(anyLong(), anyLong())).willReturn(true);
        info = service.info(ROOM_CODE);
        assertNotNull(info);
    }

    @Test
    void start() {
        given(mapper.getOneByRoomCode(ROOM_CODE)).willReturn(null);
        Boolean result = service.start(ROOM_CODE);
        assertFalse(result);

        GameRoomEntity entity = new GameRoomEntity();
        entity.setId(1L);
        given(mapper.getOneByRoomCode(ROOM_CODE)).willReturn(entity);
        given(mapper.updateById(any())).willReturn(0);
        result = service.start(ROOM_CODE);
        assertFalse(result);

        given(mapper.updateById(any())).willReturn(1);
        result = service.start(ROOM_CODE);
        assertFalse(result);

        given(userService.getGamerByCode(ROOM_CODE)).willReturn(GAMERS);
        result = service.start(ROOM_CODE);
        assertTrue(result);
    }
}
