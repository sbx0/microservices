package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomInfoVO;
import cn.sbx0.microservices.uno.entity.GameRoomVO;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangh
 * @since 2022-03-10
 */
@RestController
@RequestMapping("/uno/room")
public class GameRoomController extends BaseController<GameRoomServiceImpl, GameRoomMapper, GameRoomEntity> {
    @Resource
    private IGameRoomUserService gameRoomUserService;

    @PostMapping("/create")
    public ResponseVO<String> create(@RequestBody GameRoomCreateDTO dto) {
        String code = service.create(dto);
        return new ResponseVO<>(code != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, code);
    }

    @GetMapping("/info/{roomCode}")
    public ResponseVO<GameRoomInfoVO> info(@PathVariable("roomCode") String roomCode) {
        GameRoomInfoVO entity = service.info(roomCode);
        return new ResponseVO<>(entity != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, entity);
    }

    @GetMapping("/start/{roomCode}")
    public ResponseVO<Boolean> start(@PathVariable("roomCode") String roomCode) {
        Boolean result = service.start(roomCode);
        return new ResponseVO<>(result ? ResponseVO.SUCCESS : ResponseVO.FAILED, result);
    }

    @GetMapping("/list")
    public Paging<GameRoomVO> pagingList(PageQueryDTO dto) {
        Paging<GameRoomEntity> source = super.list(dto);
        Paging<GameRoomVO> target = new Paging<>();
        BeanUtils.copyProperties(source, target);
        List<GameRoomEntity> data = source.getData();
        List<Object> objects = desensitization(data, GameRoomVO.class);
        List<GameRoomVO> vos = new ArrayList<>();
        for (Object object : objects) {
            GameRoomVO vo = (GameRoomVO) object;
            vo.setPlayersInSize(gameRoomUserService.countByGameRoom(vo.getRoomCode()));
            vos.add(vo);
        }
        target.setData(vos);
        return target;
    }
}

