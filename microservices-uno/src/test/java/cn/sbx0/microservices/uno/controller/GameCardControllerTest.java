package cn.sbx0.microservices.uno.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.impl.GameCardServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GameCardController.class)
@ActiveProfiles("test")
class GameCardControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    GameCardServiceImpl service;

    @MockBean
    GameRoomMapper gameRoomMapper;

    @MockBean
    GameRoomUserMapper gameRoomUserMapper;
    private MockedStatic<StpUtil> stpUtilMock;
    public final static Long USER_ID = 1L;

    @BeforeEach
    public void beforeEach() {
        stpUtilMock = mockStatic(StpUtil.class);
    }

    @AfterEach
    public void afterEach() {
        stpUtilMock.close();
    }

    @Test
    void myCardList() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor("red");
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.myCardList(roomCode)).willReturn(cards);

        mvc.perform(get("/uno/card/my/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value("red"))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void drawCard() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor("red");
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.drawCard(roomCode, 1)).willReturn(cards);

        mvc.perform(get("/uno/card/draw/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value("red"))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void playCard() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
        String uuid = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
        String color = "red";

        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(0L);

        given(service.playCard(roomCode, uuid, color, 0L)).willReturn(true);

        mvc.perform(get("/uno/card/play/" + roomCode + "/" + uuid)
                        .queryParam("color", "red")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("data").value(true))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void nextPlay() throws Exception {
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(USER_ID);
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor("red");
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.nextPlay(roomCode, 1L)).willReturn(cards);

        mvc.perform(get("/uno/card/next/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value("red"))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void discardCardList() throws Exception {
        String roomCode = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";

        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor("red");
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.discardCardList(roomCode)).willReturn(cards);

        mvc.perform(get("/uno/card/discard/" + roomCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value("red"))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }
}
