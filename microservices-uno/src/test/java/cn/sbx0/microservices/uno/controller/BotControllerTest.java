package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.uno.bot.RandomBot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static cn.sbx0.microservices.uno.TestDataProvider.ROOM_CODE;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@WebMvcTest(controllers = BotController.class)
class BotControllerTest extends BaseControllerTest {
    @Test
    void joinRoom() throws Exception {

        given(service.joinRoom(ROOM_CODE)).willReturn(true);

        mvc.perform(get("/bot/add/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        given(service.joinRoom(ROOM_CODE)).willReturn(false);

        mvc.perform(get("/bot/add/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"))
                .andReturn().getResponse().getContentAsString();
    }

    @MockBean
    private RandomBot service;

    @Test
    void quitRoom() throws Exception {
        given(service.quitRoom(ROOM_CODE)).willReturn(true);

        mvc.perform(get("/bot/remove/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        given(service.quitRoom(ROOM_CODE)).willReturn(false);

        mvc.perform(get("/bot/remove/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"))
                .andReturn().getResponse().getContentAsString();
    }

    @SpringBootApplication(scanBasePackages = {"cn.sbx0.microservices.uno"})
    static class DependencyInjection {
    }
}



