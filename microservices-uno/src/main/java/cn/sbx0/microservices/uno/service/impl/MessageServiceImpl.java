package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.constant.GameRedisKey;
import cn.sbx0.microservices.uno.entity.MessageDTO;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {
    private final static String ID = UUID.randomUUID().toString();
    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, SseEmitter>> CACHES = new ConcurrentHashMap<>();
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();
    @Resource
    private RedisTemplate<String, MessageDTO> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public SseEmitter subscribe(String code) {
        String userId = StpUtil.getLoginIdAsString();
        String key = GameRedisKey.ROUTING.replaceAll(GameRedisKey.CODE, code).replaceAll(GameRedisKey.USER_ID, userId);

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sseEmitter.onCompletion(() -> {
            log.info(userId + " completion");
            deleteCache(code, userId, key);
        });

        sseEmitter.onTimeout(() -> {
            log.info(userId + " timeout");
            deleteCache(code, userId, key);
        });

        sseEmitter.onError((e) -> {
            log.info(userId + " error");
            deleteCache(code, userId, key);
        });

        // add to routing table
        stringRedisTemplate.opsForValue().set(key, ID);

        ConcurrentHashMap<String, SseEmitter> cache = CACHES.get(code);
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
        }
        cache.put(userId, sseEmitter);
        CACHES.put(code, cache);

        return sseEmitter;
    }

    private void deleteCache(String code, String userId, String key) {
        // delete from routing table
        stringRedisTemplate.expire(key, Duration.ZERO);

        ConcurrentHashMap<String, SseEmitter> cache = CACHES.get(code);
        if (cache != null) {
            cache.remove(userId);
            CACHES.put(code, cache);
        }
    }

    /**
     * other services assign messages to this service
     */
    @Override
    public void handleOwnerMessage() {
        MessageDTO message = redisTemplate.opsForList().leftPop(GameRedisKey.MESSAGE.replaceAll(GameRedisKey.ID, ID));
        if (message == null) {
            return;
        }
        nonBlockingService.execute(() -> {
            if ("*".equals(message.getCode())) {
                for (Map.Entry<String, ConcurrentHashMap<String, SseEmitter>> cs : CACHES.entrySet()) {
                    ConcurrentHashMap<String, SseEmitter> cache = cs.getValue();
                    message.setCode(cs.getKey());
                    sendMessage(message, cache);
                }
            } else {
                ConcurrentHashMap<String, SseEmitter> cache = CACHES.get(message.getCode());
                sendMessage(message, cache);
            }
        });
    }

    @Override
    public void send(MessageDTO message) {
        nonBlockingService.execute(() -> {
            if ("*".equals(message.getCode())) {
                for (Map.Entry<String, ConcurrentHashMap<String, SseEmitter>> cs : CACHES.entrySet()) {
                    ConcurrentHashMap<String, SseEmitter> cache = cs.getValue();
                    message.setCode(cs.getKey());
                    sendMessage(message, cache);
                }
            } else {
                String key = GameRedisKey.ROUTING.replaceAll(GameRedisKey.CODE, message.getCode()).replaceAll(GameRedisKey.USER_ID, message.getUserId());
                Set<String> keys = new HashSet<>();
                if ("*".equals(message.getUserId())) {
                    keys = stringRedisTemplate.keys(key);
                } else {
                    keys.add(key);
                }

                if (!CollectionUtils.isEmpty(keys)) {
                    for (String k : keys) {
                        String id = stringRedisTemplate.opsForValue().get(k);
                        if (!ID.equals(id) && StringUtils.hasText(id)) {
                            redisTemplate.opsForList().rightPush(GameRedisKey.MESSAGE.replaceAll(GameRedisKey.ID, id), message);
                        }
                    }
                }

                ConcurrentHashMap<String, SseEmitter> cache = CACHES.get(message.getCode());
                sendMessage(message, cache);
            }
        });
    }

    private void sendMessage(MessageDTO message, ConcurrentHashMap<String, SseEmitter> cache) {
        if (cache == null) {
            return;
        }
        for (Map.Entry<String, SseEmitter> c : cache.entrySet()) {
            SseEmitter sse = c.getValue();
            if (sse == null) {
                continue;
            }
            if (!"*".equals(message.getUserId())) {
                if (!c.getKey().equals(message.getUserId())) {
                    continue;
                }
            }
            try {
                sse.send(SseEmitter.event().name(message.getChannel()).data(message.getMessage()));
            } catch (IOException e) {
                sse.completeWithError(e);
                cache.remove(c.getKey());
                CACHES.put(message.getCode(), cache);
            }
        }
    }
}
