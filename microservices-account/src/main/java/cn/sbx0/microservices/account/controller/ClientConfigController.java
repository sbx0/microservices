package cn.sbx0.microservices.account.controller;


import cn.sbx0.microservices.account.entity.ClientConfigEntity;
import cn.sbx0.microservices.account.mapper.ClientConfigMapper;
import cn.sbx0.microservices.account.service.impl.ClientConfigServiceImpl;
import cn.sbx0.microservices.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangh
 * @since 2022-03-08
 */
@RestController
@RequestMapping("/account/clientConfigEntity")
public class ClientConfigController extends BaseController<ClientConfigServiceImpl, ClientConfigMapper, ClientConfigEntity> {

}

