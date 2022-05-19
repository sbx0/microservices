package cn.sbx0.microservices.uno.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.uno.entity.GameResultEntity;
import cn.sbx0.microservices.uno.mapper.GameResultMapper;
import cn.sbx0.microservices.uno.service.impl.GameResultServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangh
 * @since 2022-05-19
 */
@RestController
@RequestMapping("/uno/result")
public class GameResultController extends BaseController<GameResultServiceImpl, GameResultMapper, GameResultEntity> {

}
