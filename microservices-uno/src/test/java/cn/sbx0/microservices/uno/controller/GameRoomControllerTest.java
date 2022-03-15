package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.UnoApplication;
import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

/**
 * @author sbx0
 * @since 2022/3/15
 */
@SpringBootTest(classes = UnoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameRoomControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    private String token = "c5be2924-7f2f-4624-90b3-317af3cbcac3";

    @SuppressWarnings("unchecked")
    @Test
    void create() throws Exception {
        GameRoomCreateDTO dto = new GameRoomCreateDTO();
        dto.setRoomName("Friendship first");
        dto.setPlayersSize(4);
        dto.setPrivacyFlag(0);
        dto.setRemark("test");
        String params = objectMapper.writeValueAsString(dto);
        String response = mockMvc.perform(
                MockMvcRequestBuilders.post("/uno/room/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params)
                        .header("satoken", token)
                        .header("version", "dev")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ResponseVO<String> responseVO = (ResponseVO<String>) objectMapper.readValue(response, ResponseVO.class);
        Assertions.assertEquals("failed", responseVO.getMessage());
    }
}