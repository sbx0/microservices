package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.impl.GameRoomServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangh
 * @since 2022-03-09
 */
@RestController
@RequestMapping("/uno/room")
public class GameRoomController extends BaseController<GameRoomServiceImpl, GameRoomMapper, GameRoomEntity> {

}

