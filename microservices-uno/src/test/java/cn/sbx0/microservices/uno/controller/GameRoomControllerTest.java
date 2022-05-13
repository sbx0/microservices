package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomInfoVO;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

import static cn.sbx0.microservices.uno.TestDataProvider.ROOM_CODE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
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
class GameRoomControllerTest extends BaseControllerTest {
    @MockBean
    private GameRoomServiceImpl service;
    @MockBean
    private IGameRoomUserService gameRoomUserService;

    @Test
    void subscribe() throws Exception {
        given(messageService.subscribe(ROOM_CODE)).willReturn(new SseEmitter());

        mvc.perform(get("/uno/room/subscribe/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @MockBean
    private IMessageService messageService;

    @Test
    void create() throws Exception {
        GameRoomCreateDTO dto = new GameRoomCreateDTO();
        dto.setRoomName("room_name");

        given(service.create(any(), anyLong())).willReturn(ROOM_CODE);

        mvc.perform(post("/uno/room/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("data").value(ROOM_CODE));
    }

    @Test
    void info() throws Exception {
        GameRoomInfoVO result = new GameRoomInfoVO();
        result.setRoomCode(ROOM_CODE);
        given(service.info(ROOM_CODE)).willReturn(result);

        mvc.perform(get("/uno/room/info/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data.roomCode").value(ROOM_CODE));
    }

    @Test
    void start() throws Exception {
        given(service.start(ROOM_CODE)).willReturn(true);

        mvc.perform(get("/uno/room/start/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"));

        given(service.start(ROOM_CODE)).willReturn(false);

        mvc.perform(get("/uno/room/start/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"));
    }

    @Test
    void pagingList() throws Exception {
        List<GameRoomEntity> result = new ArrayList<>();
        GameRoomEntity entity = new GameRoomEntity();
        entity.setId(1L);
        entity.setRoomCode(ROOM_CODE);
        result.add(entity);
        given(service.list()).willReturn(result);

        given(gameRoomUserService.countByGameRoom(anyString())).willReturn(2);

        mvc.perform(get("/uno/room/list")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("total").value(1));
    }

    @SpringBootApplication(scanBasePackages = {"cn.sbx0.microservices.uno"})
    static class DependencyInjection {
    }
}
