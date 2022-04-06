package cn.sbx0.microservices.uno.scheduled;

import cn.sbx0.microservices.uno.service.IGameRoomService;
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
    private IGameRoomService roomService;

    @Scheduled(fixedDelay = 5000)
    public void heartbeat() {
        log.info("SSE heartbeat...");
        roomService.message("*", "message", "heartbeat");
    }
}
