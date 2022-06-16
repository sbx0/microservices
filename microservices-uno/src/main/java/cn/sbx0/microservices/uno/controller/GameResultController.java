package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.GameResultEntity;
import cn.sbx0.microservices.uno.entity.GameResultVO;
import cn.sbx0.microservices.uno.mapper.GameResultMapper;
import cn.sbx0.microservices.uno.service.impl.GameResultServiceImpl;
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
 * @since 2022-05-19
 */
@RestController
@RequestMapping("/result")
public class GameResultController extends BaseController<GameResultServiceImpl, GameResultMapper, GameResultEntity> {
    @GetMapping("/list/{roomCode}")
    public ResponseVO<List<GameResultVO>> listByGameRoom(@PathVariable("roomCode") String roomCode) {
        List<GameResultVO> data = service.listByGameRoom(roomCode);
        return ResponseVO.judge(data != null, data);
    }
}
