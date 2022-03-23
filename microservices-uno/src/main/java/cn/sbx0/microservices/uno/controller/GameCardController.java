package cn.sbx0.microservices.uno.controller;


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

    @GetMapping("/my/{roomCode}")
    public ResponseVO<List<CardEntity>> myCardList(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> cards = service.myCardList(roomCode);
        return new ResponseVO<>(cards != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, cards);
    }

    @GetMapping("/draw/{roomCode}")
    public ResponseVO<List<CardEntity>> drawCard(
            @PathVariable("roomCode") String roomCode
    ) {
        List<CardEntity> result = service.drawCard(roomCode, 1);
        return new ResponseVO<>(result != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, result);
    }

    @GetMapping("/play/{roomCode}/{uuid}")
    public ResponseVO<Boolean> playCard(
            @PathVariable("roomCode") String roomCode,
            @PathVariable("uuid") String uuid,
            @RequestParam(value = "color", required = false) String color
    ) {
        Boolean result = service.playCard(roomCode, uuid, color);
        return new ResponseVO<>(result ? ResponseVO.SUCCESS : ResponseVO.FAILED, result);
    }

    @GetMapping("/discard/{roomCode}")
    public ResponseVO<List<CardEntity>> discardCardList(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> cards = service.discardCardList(roomCode);
        return new ResponseVO<>(cards != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, cards);
    }
}

