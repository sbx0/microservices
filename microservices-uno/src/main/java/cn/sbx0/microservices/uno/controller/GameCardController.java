package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameCardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/play/{roomCode}/{uuid}")
    public ResponseVO<Boolean> playCard(
            @PathVariable("roomCode") String roomCode,
            @PathVariable("uuid") String uuid
    ) {
        Boolean result = service.playCard(roomCode, uuid);
        return new ResponseVO<>(result ? ResponseVO.SUCCESS : ResponseVO.FAILED, result);
    }

    @GetMapping("/discard/{roomCode}")
    public ResponseVO<List<CardEntity>> discardCardList(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> cards = service.discardCardList(roomCode);
        return new ResponseVO<>(cards != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, cards);
    }
}

