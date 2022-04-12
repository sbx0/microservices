package cn.sbx0.microservices.uno.controller;

import cn.sbx0.microservices.entity.ResponseVO;
import com.netflix.appinfo.ApplicationInfoManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/4/12
 */
@RestController
@RequestMapping("/service/instance")
public class ServiceInstanceController {
    @Resource
    private ApplicationInfoManager applicationInfoManager;

    @GetMapping("/choose")
    public ResponseVO<String> choose() {
        return new ResponseVO<>(ResponseVO.SUCCESS, applicationInfoManager.getInfo().getInstanceId());
    }
}