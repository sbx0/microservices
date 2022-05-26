package cn.sbx0.microservices.uno.scheduled;

import cn.sbx0.microservices.uno.constant.MessageChannel;
import cn.sbx0.microservices.uno.entity.MessageDTO;
import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    public static final Lock LOCK = new ReentrantLock();

    @Async
    @Scheduled(fixedRate = 5000)
    public void heartbeat() {
        messageService.send(new MessageDTO<>("*", MessageChannel.MESSAGE, "*", "heartbeat"));
    }

    @Async
    @Scheduled(fixedRate = 100)
    public void handleOtherMessage() {
        if (LOCK.tryLock()) {
            try {
                LOCK.lock();
                messageService.handleOwnerMessage();
            } finally {
                LOCK.unlock();
            }
        }
    }

    @Async
    @Scheduled(fixedRate = 5000)
    public void match() {
        matchService.match();
    }
}
