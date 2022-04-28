package cn.sbx0.microservices.uno.feign;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.config.FeignConfiguration;
import lb.CustomLoadBalancerConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author sbx0
 * @since 2022/3/15
 */
@LoadBalancerClient(value = "ACCOUNT", configuration = CustomLoadBalancerConfiguration.class)
@FeignClient(name = "ACCOUNT", configuration = FeignConfiguration.class)
public interface AccountService {
    @GetMapping(value = "/user/loginInfo", produces = "application/json")
    AccountVO loginInfo();

    @GetMapping(value = "/findByUserName", produces = "application/json")
    AccountVO findByUserName(@RequestParam(value = "name") String name);
}
