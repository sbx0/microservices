package cn.sbx0.microservices.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableCaching
@EnableDiscoveryClient
@MapperScan({"cn.sbx0.microservices.*.mapper"})
@SpringBootApplication(scanBasePackages = {"cn.sbx0.microservices"})
public class UnoApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnoApplication.class, args);
    }
}
