package cn.sbx0.microservices.uno.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.CarColor;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.impl.GameCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static cn.sbx0.microservices.uno.TestDataProvider.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sbx0
 * @since 2022/5/6
 */
@WebMvcTest(controllers = GameCardController.class)
class GameCardControllerTest extends BaseControllerTest {
    @MockBean
    private GameCardServiceImpl service;

    @Test
    void myCardList() throws Exception {
        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor(CarColor.RED);
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.myCardList(ROOM_CODE)).willReturn(cards);

        mvc.perform(get("/uno/card/my/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value(CarColor.RED))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void drawCard() throws Exception {
        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor(CarColor.RED);
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.drawCard(ROOM_CODE, 1)).willReturn(cards);

        mvc.perform(get("/uno/card/draw/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value(CarColor.RED))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void playCard() throws Exception {
        String uuid = "d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a";
        String color = CarColor.RED;

        stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(0L);

        given(service.playCard(ROOM_CODE, uuid, color, 0L)).willReturn(true);

        mvc.perform(get("/uno/card/play/" + ROOM_CODE + "/" + uuid)
                        .queryParam("color", CarColor.RED)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("data").value(true))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void nextPlay() throws Exception {
        stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(USER_ID);

        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor(CarColor.RED);
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.nextPlay(ROOM_CODE, 1L)).willReturn(cards);

        mvc.perform(get("/uno/card/next/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value(CarColor.RED))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void discardCardList() throws Exception {
        List<CardEntity> cards = new ArrayList<>();

        CardEntity card = new CardEntity();
        card.setUuid("d8ffa264-497d-43ad-a1f0-b2f0b7aa9d7a");
        card.setColor(CarColor.RED);
        card.setUserId(1L);
        card.setPoint("4");
        cards.add(card);

        given(service.discardCardList(ROOM_CODE)).willReturn(cards);

        mvc.perform(get("/uno/card/discard/" + ROOM_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0"))
                .andExpect(jsonPath("$.data[0].color").value(CarColor.RED))
                .andExpect(jsonPath("$.data[0].point").value(4))
                .andReturn().getResponse().getContentAsString();
    }

    @SpringBootApplication(scanBasePackages = {"cn.sbx0.microservices.uno"})
    static class DependencyInjection {
    }
}
