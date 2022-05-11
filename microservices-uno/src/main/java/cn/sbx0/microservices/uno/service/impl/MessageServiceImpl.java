package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {
    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, SseEmitter>> caches = new ConcurrentHashMap<>();
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    @Override
    public SseEmitter subscribe(String code) {
        String userId = StpUtil.getLoginIdAsString();
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sseEmitter.onCompletion(() -> {
            log.info(userId + " completion");
            caches.remove(userId);
        });

        sseEmitter.onTimeout(() -> {
            log.info(userId + " timeout");
            caches.remove(userId);
        });

        sseEmitter.onError((e) -> {
            log.info(userId + " error");
            caches.remove(userId);

        });

        ConcurrentHashMap<String, SseEmitter> cache = caches.get(code);
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
        }
        cache.put(userId, sseEmitter);
        caches.put(code, cache);

        return sseEmitter;
    }

    @Override
    public void send(String code, String channel, String id, Object message) {
        nonBlockingService.execute(() -> {
            if ("*".equals(code)) {
                for (Map.Entry<String, ConcurrentHashMap<String, SseEmitter>> cs : caches.entrySet()) {
                    ConcurrentHashMap<String, SseEmitter> cache = cs.getValue();
                    sendMessage(cs.getKey(), channel, id, message, cache);
                }
            } else {
                ConcurrentHashMap<String, SseEmitter> cache = caches.get(code);
                sendMessage(code, channel, id, message, cache);
            }
        });
    }

    private void sendMessage(String code, String type, String userId, Object message, ConcurrentHashMap<String, SseEmitter> cache) {
        if (cache == null) {
            return;
        }
        for (Map.Entry<String, SseEmitter> c : cache.entrySet()) {
            SseEmitter sse = c.getValue();
            if (sse == null) {
                continue;
            }
            if (!"*".equals(userId)) {
                if (!c.getKey().equals(userId)) {
                    continue;
                }
            }
            try {
                sse.send(SseEmitter.event().name(type).data(message));
            } catch (IOException e) {
                sse.completeWithError(e);
                cache.remove(c.getKey());
                caches.put(code, cache);
            }
        }
    }
}
