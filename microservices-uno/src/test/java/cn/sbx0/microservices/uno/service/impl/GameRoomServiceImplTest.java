package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.entity.CardEntity;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

/**
 * @author sbx0
 * @since 2022/5/7
 */
@SuppressWarnings({"unchecked", "rawtypes", "SpringJavaAutowiredMembersInspection"})
@ExtendWith(SpringExtension.class)
class GameRoomServiceImplTest {
    public final static String ROOM_CODE = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
    public final static Long USER_ID = 1L;
    public final static List<AccountVO> GAMERS = new ArrayList<>();
    @Autowired
    private IGameRoomService service;
    @MockBean
    private GameRoomMapper mapper;
    @MockBean
    private IGameRoomUserService userService;
    @MockBean
    private ApplicationInfoManager applicationInfoManager;
    @MockBean
    private RedisTemplate<String, CardEntity> redisTemplate;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private ValueOperations valueOperations;
    @MockBean
    private ListOperations listOperations;
    private MockedStatic<StpUtil> stpUtilMock;
    @MockBean
    private IGameCardService gameCardService;
    @MockBean
    private RandomBot randomBot;
    @MockBean
    private IMessageService messageService;

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
    }

    @BeforeEach
    public void beforeEach() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(0L);
        stpUtilMock.when(StpUtil::getLoginIdAsString).thenReturn("0");
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForList()).willReturn(listOperations);
    }

    @AfterEach
    public void afterEach() {
        stpUtilMock.close();
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
        String roomCode = service.create(dto);
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

        given(userService.listByGameRoom(ROOM_CODE)).willReturn(GAMERS);
        result = service.start(ROOM_CODE);
        assertTrue(result);
    }

    @TestConfiguration
    static class GameRoomServiceImplTestConfiguration {
        @Bean
        public IGameRoomService service() {
            return new GameRoomServiceImpl();
        }
    }
}
