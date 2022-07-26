package cn.sbx0.microservices.bot.scheduled;

import cn.sbx0.microservices.bot.service.IGitHubBotService;
import cn.sbx0.microservices.bot.service.IGoldenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Scheduled(cron = "0 0 10,11,14,15 * * ?")
    public void handleGoldenTask() {
        log.info("handleGoldenTask");
        goldenService.readData();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void handleGitHubTask() {
        log.info("handleGitHubTask");
        gitHubBotService.readData();
    }
}
