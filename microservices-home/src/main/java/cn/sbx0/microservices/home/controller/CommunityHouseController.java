package cn.sbx0.microservices.home.controller;


import cn.sbx0.microservices.controller.BaseController;
import cn.sbx0.microservices.home.entity.CommunityHouseEntity;
import cn.sbx0.microservices.home.mapper.CommunityHouseMapper;
import cn.sbx0.microservices.home.service.impl.CommunityHouseServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 小区房子 前端控制器
 * </p>
 *
 * @author wangh
 * @since 2022-06-13
 */
@RestController
@RequestMapping("/home/communityHouseEntity")
public class CommunityHouseController extends BaseController<CommunityHouseServiceImpl, CommunityHouseMapper, CommunityHouseEntity> {

}

