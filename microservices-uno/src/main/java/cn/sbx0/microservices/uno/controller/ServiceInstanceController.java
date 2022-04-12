package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/4/12
 */
@RestController
@RequestMapping("/service/instance")
public class ServiceInstanceController {
    @Resource
    private IGameRoomService gameRoomService;

    @GetMapping("/choose/{roomCode}")
    public ResponseVO<String> choose(@PathVariable("roomCode") String roomCode) {
        return new ResponseVO<>(ResponseVO.SUCCESS, gameRoomService.choose(roomCode));
    }
}