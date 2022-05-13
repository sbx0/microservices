package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.service.impl.GameRoomUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static cn.sbx0.microservices.uno.TestDataProvider.ROOM_CODE;
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
class GameRoomUserControllerTest extends BaseControllerTest {
    @MockBean
    private GameRoomUserServiceImpl service;

    @Test
    void joinGameRoom() throws Exception {
        given(service.joinGameRoom(ROOM_CODE)).willReturn(true);

        mvc.perform(get("/uno/room/user/join/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"));

        given(service.joinGameRoom(ROOM_CODE)).willReturn(false);

        mvc.perform(get("/uno/room/user/join/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"));
    }

    @Test
    void quitGameRoom() throws Exception {
        given(service.quitGameRoom(ROOM_CODE)).willReturn(true);

        mvc.perform(get("/uno/room/user/quit/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"));

        given(service.quitGameRoom(ROOM_CODE)).willReturn(false);

        mvc.perform(get("/uno/room/user/quit/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("-1"));
    }

    @Test
    void listByGameRoom() throws Exception {
        List<AccountVO> data = new ArrayList<>();

        AccountVO vo = new AccountVO();
        vo.setId(1L);
        vo.setNickname("test");
        vo.setUsername("test");

        data.add(vo);

        given(service.listByGameRoom(ROOM_CODE)).willReturn(data);

        mvc.perform(get("/uno/room/user/list/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @SpringBootApplication(scanBasePackages = {"cn.sbx0.microservices.uno"})
    static class DependencyInjection {
    }
}
