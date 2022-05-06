package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.impl.GameRoomUserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GameRoomUserController.class)
class GameRoomUserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    @MockBean
    GameRoomUserServiceImpl service;

    @MockBean
    GameRoomMapper gameRoomMapper;

    @MockBean
    GameRoomUserMapper gameRoomUserMapper;


    @Test
    void joinGameRoom() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        given(service.joinGameRoom(roomCode)).willReturn(true);

        mvc.perform(get("/uno/room/user/join/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"));

        given(service.joinGameRoom(roomCode)).willReturn(false);

        mvc.perform(get("/uno/room/user/join/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"));
    }

    @Test
    void quitGameRoom() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        given(service.quitGameRoom(roomCode)).willReturn(true);

        mvc.perform(get("/uno/room/user/quit/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"));

        given(service.quitGameRoom(roomCode)).willReturn(false);

        mvc.perform(get("/uno/room/user/quit/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"));
    }

    @Test
    void listByGameRoom() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        List<AccountVO> data = new ArrayList<>();

        AccountVO vo = new AccountVO();
        vo.setId(1L);
        vo.setNickname("test");
        vo.setUsername("test");

        data.add(vo);

        given(service.listByGameRoom(roomCode)).willReturn(data);

        mvc.perform(get("/uno/room/user/list/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }
}
