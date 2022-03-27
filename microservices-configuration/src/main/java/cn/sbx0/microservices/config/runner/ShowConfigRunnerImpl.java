package cn.sbx0.microservices.config.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author sbx0
 * @since 2022/3/27
 */
@Component
public class ShowConfigRunnerImpl implements ApplicationRunner {
    @Value("${spring.cloud.config.server.native.search-locations}")
    private String path;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(path);
        System.out.println();
        System.out.println();
        System.out.println();
    }
}
