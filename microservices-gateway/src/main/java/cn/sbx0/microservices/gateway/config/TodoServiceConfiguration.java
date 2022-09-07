package cn.sbx0.microservices.gateway.config;

import lb.CustomLoadBalancerConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;

/**
 * @author sbx0
 * @since 2022/3/10
 */
@Configuration
@LoadBalancerClient(value = "TODO", configuration = CustomLoadBalancerConfiguration.class)
public class TodoServiceConfiguration {

}
