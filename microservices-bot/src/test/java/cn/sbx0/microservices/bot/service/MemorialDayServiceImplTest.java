package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.BotApplication;
import cn.sbx0.microservices.bot.entity.MemorialDayEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/8/2
 */
@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BotApplication.class)
class MemorialDayServiceImplTest {
    @Resource
    private IMemorialDayService memorialDayService;

    @Test
    void test() {
        List<MemorialDayEntity> days = new ArrayList<>();
        days.add(new MemorialDayEntity("2020-05-23", "我们在一起", false));
        days.add(new MemorialDayEntity("2022-08-13", "宝贝生日", true));
        days.add(new MemorialDayEntity("2022-08-12", "今天", true));
        days.add(new MemorialDayEntity("2022-11-19", "哼哼生日", true));
        days.add(new MemorialDayEntity("2020-06-15", "毕业", false));
        memorialDayService.handleData(days);
    }

}
