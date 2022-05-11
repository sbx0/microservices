package cn.sbx0.microservices.uno.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomInfoVO;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.IMessageService;
import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
import cn.sbx0.microservices.uno.service.impl.GameRoomUserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GameRoomController.class)
class GameRoomControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    MockMvc mvc;
    @MockBean
    GameRoomServiceImpl service;

    @MockBean
    GameRoomUserServiceImpl gameRoomUserService;

    @MockBean
    GameRoomMapper gameRoomMapper;

    @MockBean
    GameRoomUserMapper gameRoomUserMapper;

    @MockBean
    private IMessageService messageService;

    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    public void beforeEach() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(0L);
        stpUtilMock.when(StpUtil::getLoginIdAsString).thenReturn("0");
    }

    @AfterEach
    public void afterEach() {
        stpUtilMock.close();
    }

    @Test
    void subscribe() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        given(messageService.subscribe(roomCode)).willReturn(new SseEmitter());

        mvc.perform(get("/uno/room/subscribe/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        String code = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        GameRoomCreateDTO dto = new GameRoomCreateDTO();
        dto.setRoomName("room_name");

        given(service.create(any(), anyLong())).willReturn(code);

        mvc.perform(post("/uno/room/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("data").value(code));
    }

    @Test
    void info() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        GameRoomInfoVO result = new GameRoomInfoVO();
        result.setRoomCode(roomCode);
        given(service.info(roomCode)).willReturn(result);

        mvc.perform(get("/uno/room/info/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data.roomCode").value(roomCode));
    }

    @Test
    void start() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        given(service.start(roomCode)).willReturn(true);

        mvc.perform(get("/uno/room/start/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"));

        given(service.start(roomCode)).willReturn(false);

        mvc.perform(get("/uno/room/start/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"));
    }

    @Test
    void pagingList() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        List<GameRoomEntity> result = new ArrayList<>();
        GameRoomEntity entity = new GameRoomEntity();
        entity.setId(1L);
        entity.setRoomCode(roomCode);
        result.add(entity);
        given(service.list()).willReturn(result);

        mvc.perform(get("/uno/room/list")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("total").value(1));
    }
}
