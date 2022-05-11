package cn.sbx0.microservices.uno.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.*;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangh
 * @since 2022-03-10
 */
@Slf4j
@Controller
@RequestMapping("/uno/room")
public class GameRoomController extends BaseController<GameRoomServiceImpl, GameRoomMapper, GameRoomEntity> {
    @Resource
    private IGameRoomUserService gameRoomUserService;
    @Resource
    private IMessageService messageService;

    @GetMapping(value = "/subscribe/{roomCode}", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@PathVariable("roomCode") String roomCode) {
        return messageService.subscribe(roomCode);
    }

    @ResponseBody
    @PostMapping("/create")
    public ResponseVO<String> create(@RequestBody GameRoomCreateDTO dto) {
        String code = service.create(dto, StpUtil.getLoginIdAsLong());
        return new ResponseVO<>(code != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, code);
    }

    @ResponseBody
    @GetMapping("/info/{roomCode}")
    public ResponseVO<GameRoomInfoVO> info(@PathVariable("roomCode") String roomCode) {
        GameRoomInfoVO entity = service.info(roomCode);
        return new ResponseVO<>(entity != null ? ResponseVO.SUCCESS : ResponseVO.FAILED, entity);
    }

    @ResponseBody
    @GetMapping("/start/{roomCode}")
    public ResponseVO<Boolean> start(@PathVariable("roomCode") String roomCode) {
        Boolean result = service.start(roomCode);
        return new ResponseVO<>(result ? ResponseVO.SUCCESS : ResponseVO.FAILED, result);
    }

    @ResponseBody
    @GetMapping("/list")
    public Paging<GameRoomVO> pagingList(PageQueryDTO dto) {
        Paging<GameRoomEntity> source = super.list(dto);
        Paging<GameRoomVO> target = GameRoomConverter.INSTANCE.pagingEntityToVO(source);
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

