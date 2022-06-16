package cn.sbx0.microservices.uno.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameCardService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangh
 * @since 2022-03-22
 */
@RestController
@RequestMapping("/uno/card")
public class GameCardController {
    @Resource
    private IGameCardService service;

    /**
     * query the hand card of the current user
     */
    @GetMapping("/my/{roomCode}")
    public ResponseVO<List<CardEntity>> myCardList(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> cards = service.myCardList(roomCode);
        return ResponseVO.judge(cards != null, cards);
    }

    /**
     * draw card
     */
    @GetMapping("/draw/{roomCode}")
    public ResponseVO<List<CardEntity>> drawCard(
            @PathVariable("roomCode") String roomCode
    ) {
        List<CardEntity> result = service.drawCard(roomCode, 1);
        return ResponseVO.judge(result != null, result);
    }

    /**
     * play card
     *
     * @param roomCode unique room code
     * @param uuid     unique card number
     * @param color    [Optional] The color of the color plate can be specified
     * @return message
     */
    @GetMapping("/play/{roomCode}/{uuid}")
    public ResponseVO<Boolean> playCard(
            @PathVariable("roomCode") String roomCode,
            @PathVariable("uuid") String uuid,
            @RequestParam(value = "color", required = false) String color
    ) {
        boolean result = service.playCard(roomCode, uuid, color, StpUtil.getLoginIdAsLong());
        return ResponseVO.judge(result, result);
    }

    /**
     * turn to next player
     */
    @GetMapping("/next/{roomCode}")
    public ResponseVO<List<CardEntity>> nextPlay(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> result = service.nextPlay(roomCode, StpUtil.getLoginIdAsLong());
        return ResponseVO.judge(result != null, result);
    }

    /**
     * discard pile
     */
    @GetMapping("/discard/{roomCode}")
    public ResponseVO<List<CardEntity>> discardCardList(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> cards = service.getDiscardCards(roomCode);
        return ResponseVO.judge(cards != null, cards);
    }
}

