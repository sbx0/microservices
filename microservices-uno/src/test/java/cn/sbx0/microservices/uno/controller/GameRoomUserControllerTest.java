package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.uno.UnoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author sbx0
 * @since 2022/3/15
 */
@SpringBootTest(classes = UnoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class GameRoomUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void joinGameRoom() throws Exception {

    }

    @Test
    void listByGameRoom() {
    }
}