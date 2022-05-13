package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static cn.sbx0.microservices.uno.TestDataProvider.ROOM_CODE;
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
class ServiceInstanceControllerTest extends BaseControllerTest {
    @MockBean
    private GameRoomServiceImpl service;

    @Test
    void choose() throws Exception {
        given(service.choose(ROOM_CODE)).willReturn(ROOM_CODE);

        String response = mvc.perform(get("/service/instance/choose/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andReturn().getResponse().getContentAsString();

        System.out.println("response = " + response);
    }

    @SpringBootApplication(scanBasePackages = {"cn.sbx0.microservices.uno"})
    static class DependencyInjection {
    }
}
