package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.entity.PageQueryDTO;
import cn.sbx0.microservices.entity.Paging;
import cn.sbx0.microservices.uno.entity.GameRoomVO;
import cn.sbx0.microservices.uno.service.IGameAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangh
 * @since 2022-05-27
 */
@Slf4j
@RestController
@RequestMapping("/game/admin")
public class GameAdminController {
    @Resource
    private IGameAdminService service;

    @GetMapping("/rooms")
    public Paging<GameRoomVO> roomPagingList(PageQueryDTO dto) {
        return service.roomPagingList(dto);
    }
}

