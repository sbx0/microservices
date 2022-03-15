package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.impl.GameRoomUserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangh
 * @since 2022-03-15
 */
@RestController
@RequestMapping("/uno/room/user")
public class GameRoomUserController extends BaseController<GameRoomUserServiceImpl, GameRoomUserMapper, GameRoomUserEntity> {
    @GetMapping("/join/{roomCode}")
    public ResponseVO<Boolean> joinGameRoom(@PathVariable("roomCode") String roomCode) {
        boolean success = service.joinGameRoom(roomCode);
        return new ResponseVO<>(success ? ResponseVO.SUCCESS : ResponseVO.FAILED, success);
    }

    @GetMapping("/list/{roomCode}")
    public ResponseVO<List<GameRoomUserEntity>> listByGameRoom(@PathVariable("roomCode") String roomCode) {
        List<GameRoomUserEntity> data = service.listByGameRoom(roomCode);
        return new ResponseVO<>(data != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, data);
    }
}