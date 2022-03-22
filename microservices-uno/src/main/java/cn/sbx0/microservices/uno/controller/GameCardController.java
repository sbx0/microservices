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

    @GetMapping("/draw/begin/{roomCode}")
    public ResponseVO<List<CardEntity>> drawCardOnBeginning(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> cards = service.drawCardOnBeginning(roomCode);
        return new ResponseVO<>(cards != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, cards);
    }

    @GetMapping("/my/{roomCode}")
    public ResponseVO<List<CardEntity>> myCard(@PathVariable("roomCode") String roomCode) {
        List<CardEntity> cards = service.myCard(roomCode);
        return new ResponseVO<>(cards != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, cards);
    }
}

