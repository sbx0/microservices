package cn.sbx0.microservices.uno.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.GameInfoVO;
import cn.sbx0.microservices.uno.service.IGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangh
 * @since 2022-05-27
 */
@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {
    @Resource
    private IGameService service;

    @GetMapping("/info/{code}")
    public ResponseVO<GameInfoVO> getInfoByCodeAndUserId(@PathVariable("code") String code) {
        return service.getInfoByCodeAndUserId(code, StpUtil.getLoginIdAsLong());
    }
}

