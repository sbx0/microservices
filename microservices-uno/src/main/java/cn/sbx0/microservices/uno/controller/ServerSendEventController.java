package cn.sbx0.microservices.uno.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sbx0
 * @since 2022/3/28
 */
@Slf4j
@Controller
public class ServerSendEventController {
    private final ConcurrentHashMap<String, SseEmitter> caches = new ConcurrentHashMap<>();

    @GetMapping(value = "/sse/subscribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe() {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        String id = StpUtil.getLoginIdAsString();

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sseEmitter.onCompletion(() -> {
            log.info(id + " completion");
            caches.remove(id);
        });

        sseEmitter.onTimeout(() -> {
            log.info(id + " timeout");
            caches.remove(id);
        });

        sseEmitter.onError((e) -> {
            log.info(id + " error");
            caches.remove(id);
        });

        caches.put(id, sseEmitter);

        return sseEmitter;
    }

    @GetMapping("/sse/send")
    @ResponseBody
    public void dispatchDataRequest() {
        if (CollectionUtils.isEmpty(caches)) {
            return;
        }
        String id = StpUtil.getLoginIdAsString();
        for (Map.Entry<String, SseEmitter> one : caches.entrySet()) {
            try {
                one.getValue().send(SseEmitter.event().name("message").data(id + " is join"));
            } catch (IOException e) {
                caches.remove(one.getKey());
            }
        }
    }
}
