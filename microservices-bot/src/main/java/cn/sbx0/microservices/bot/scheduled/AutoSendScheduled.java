package cn.sbx0.microservices.bot.scheduled;

import cn.sbx0.microservices.bot.entity.GithubRepositoryEntity;
import cn.sbx0.microservices.bot.entity.MemorialDayEntity;
import cn.sbx0.microservices.bot.service.IGitHubBotService;
import cn.sbx0.microservices.bot.service.IGoldenService;
import cn.sbx0.microservices.bot.service.IMemorialDayService;
import cn.sbx0.microservices.bot.utils.JSONUtils;
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

    /**
     * Monday to Friday at 3pm
     */
    @Scheduled(cron = "0 0 15 * * 01,02,03,04,05")
    public void handleGoldenTask() {
        log.info("handleGoldenTask");
        goldenService.readData();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void handleGitHubTask() throws InterruptedException {
        List<GithubRepositoryEntity> list = new ArrayList<>();
        list.add(new GithubRepositoryEntity("xiaoye97", "DinkumChinese"));
        list.add(new GithubRepositoryEntity("spring-projects", "spring-boot"));
        list.add(new GithubRepositoryEntity("dotnetcore", "FastGithub"));
        list.add(new GithubRepositoryEntity("barry-ran", "QtScrcpy"));
        list.add(new GithubRepositoryEntity("zerotier", "ZeroTierOne"));
        list.add(new GithubRepositoryEntity("fatedier", "frp"));
        list.add(new GithubRepositoryEntity("moonlight-stream", "moonlight-qt"));
        list.add(new GithubRepositoryEntity("moonlight-stream", "Internet-Hosting-Tool"));
        for (GithubRepositoryEntity repository : list) {
            log.info("handleGitHubTask " + JSONUtils.toJSONString(repository));
            gitHubBotService.readData(repository.getAuthor(), repository.getName());
            Thread.sleep(5000);
        }
    }

    /**
     * every day at nine
     */
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
