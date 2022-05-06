package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
@WebMvcTest(controllers = BotController.class)
class BotControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    RandomBot service;

    @MockBean
    GameRoomMapper gameRoomMapper;

    @MockBean
    GameRoomUserMapper gameRoomUserMapper;

    @Test
    void joinRoom() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        given(service.joinRoom(roomCode)).willReturn(true);

        mvc.perform(get("/bot/add/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        given(service.joinRoom(roomCode)).willReturn(false);

        mvc.perform(get("/bot/add/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void quitRoom() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        given(service.quitRoom(roomCode)).willReturn(true);

        mvc.perform(get("/bot/remove/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        given(service.quitRoom(roomCode)).willReturn(false);

        mvc.perform(get("/bot/remove/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"))
                .andReturn().getResponse().getContentAsString();
    }
}



