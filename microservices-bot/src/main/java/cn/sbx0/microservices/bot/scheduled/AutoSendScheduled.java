package cn.sbx0.microservices.bot.scheduled;

import cn.sbx0.microservices.bot.entity.MemorialDayEntity;
import cn.sbx0.microservices.bot.service.IGitHubBotService;
import cn.sbx0.microservices.bot.service.IGoldenService;
import cn.sbx0.microservices.bot.service.IMemorialDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/7/26
 */
@Slf4j
@Component
public class AutoSendScheduled {
    @Resource
    private IGoldenService goldenService;
    @Resource
    private IGitHubBotService gitHubBotService;

    @Resource
    private IMemorialDayService memorialDayService;

    @Scheduled(cron = "0 0 10,11,14,15 * * ?")
    public void handleGoldenTask() {
        log.info("handleGoldenTask");
        goldenService.readData();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void handleGitHubTask() {
        log.info("handleGitHubTask");
        gitHubBotService.readData("xiaoye97", "DinkumChinese");
        gitHubBotService.readData("spring-projects", "spring-boot");
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void handleMemorialDayTask() {
        log.info("handleMemorialDayTask");
        List<MemorialDayEntity> days = new ArrayList<>();
        days.add(new MemorialDayEntity("2020-05-23", "我们在一起", false));
        days.add(new MemorialDayEntity("-08-13", "宝贝生日", true));
        days.add(new MemorialDayEntity("-11-19", "哼哼生日", true));
        days.add(new MemorialDayEntity("2020-06-15", "毕业", false));
        memorialDayService.handleData(days);
    }
}
