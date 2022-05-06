package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
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
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ServiceInstanceController.class)
@ActiveProfiles("test")
class ServiceInstanceControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    GameRoomServiceImpl service;

    @MockBean
    GameRoomMapper gameRoomMapper;

    @MockBean
    GameRoomUserMapper gameRoomUserMapper;

    @Test
    void choose() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        given(service.choose(roomCode)).willReturn(roomCode);

        String response = mvc.perform(get("/service/instance/choose/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        System.out.println("response = " + response);
    }
}
