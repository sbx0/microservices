package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangh
 * @since 2022-03-10
 */
@RestController
@RequestMapping("/uno/room")
public class GameRoomController extends BaseController<GameRoomServiceImpl, GameRoomMapper, GameRoomEntity> {
    @PostMapping("/create")
    public ResponseVO<String> create(@RequestBody GameRoomCreateDTO dto) {
        String code = service.create(dto);
        return new ResponseVO<>(code != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, code);
    }

    @GetMapping("/info/{roomCode}")
    public ResponseVO<GameRoomEntity> info(@PathVariable("roomCode") String roomCode) {
        GameRoomEntity entity = service.getOneByRoomCode(roomCode);
        return new ResponseVO<>(entity != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, entity);
    }
}

