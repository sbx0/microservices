package cn.sbx0.microservices.uno.feign;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.entity.IDsDTO;
import cn.sbx0.microservices.uno.config.FeignConfiguration;
import lb.CustomLoadBalancerConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

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

    @GetMapping(value = "/findById", produces = "application/json")
    AccountVO findById(@RequestParam(value = "id") Long id);

    @PostMapping(value = "/mapNameByIds", produces = "application/json")
    Map<Long, String> mapNameByIds(@RequestBody IDsDTO dto);
}
