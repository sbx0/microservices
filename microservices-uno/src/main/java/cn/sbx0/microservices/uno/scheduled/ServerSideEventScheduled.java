package cn.sbx0.microservices.uno.scheduled;

import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author sbx0
 * @since 2022/4/1
 */
@Slf4j
@Component
public class ServerSideEventScheduled {
    @Resource
    private IMessageService messageService;
    @Resource
    private IMatchService matchService;

    @Scheduled(fixedDelay = 5000)
    public void heartbeat() {
        messageService.send("*", "message", "*", "heartbeat");
    }

    @Scheduled(fixedDelay = 1000)
    public void match() {
        matchService.match();
    }
}
