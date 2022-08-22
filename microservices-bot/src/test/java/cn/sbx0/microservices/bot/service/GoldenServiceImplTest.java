package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.BotApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/7/26
 */
@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BotApplication.class)
class GoldenServiceImplTest {
    @Resource
    private IGoldenService goldenService;

    @Test
    void readData() {
        goldenService.readData();
    }
}
