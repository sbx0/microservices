package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/4/28
 */
@RestController
@RequestMapping("/bot")
public class BotController {
    @Resource
    private RandomBot randomBot;

    @GetMapping("/add/{roomCode}")
    public ResponseVO<Boolean> add(@PathVariable("roomCode") String roomCode) {
        boolean result = randomBot.join(roomCode);
        return new ResponseVO<>(result ? ResponseVO.SUCCESS : ResponseVO.FAILED, result);
    }

    @GetMapping("/remove/{roomCode}")
    public ResponseVO<Boolean> remove(@PathVariable("roomCode") String roomCode) {
        boolean result = randomBot.quit(roomCode);
        return new ResponseVO<>(result ? ResponseVO.SUCCESS : ResponseVO.FAILED, result);
    }
}
