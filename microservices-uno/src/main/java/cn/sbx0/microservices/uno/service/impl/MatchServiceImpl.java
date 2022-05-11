package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author sbx0
 * @since 2022/5/10
 */
@Slf4j
@Service
public class MatchServiceImpl implements IMatchService {
    public static final String CODE = "match";
    public static final String CHANNEL_INFO = "match_info";
    public static final String CHANNEL_FOUND = "match_found";
    private final Deque<MatchExpectDTO> QUEUE = new ConcurrentLinkedDeque<>();
    private final static ConcurrentHashMap<String, ConcurrentLinkedQueue<MatchExpectDTO>> caches = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Resource
    private IMessageService messageService;
    @Resource
    private IGameRoomUserService gameRoomUserService;

    @Override
    public ResponseVO<Boolean> join(MatchExpectDTO dto) {
        QUEUE.addLast(dto);
        executorService.execute(() -> messageService.send(CODE, CHANNEL_INFO, "*", QUEUE.size()));
        return new ResponseVO<>(ResponseVO.SUCCESS, true);
    }

    @Override
    public ResponseVO<Boolean> quit(Long userId) {
        AtomicBoolean find = new AtomicBoolean(false);
        QUEUE.forEach(q -> {
            if (userId.equals(q.getUserId())) {
                QUEUE.remove(q);
                find.set(true);
            }
        });
        if (find.get()) {
            executorService.execute(() -> messageService.send(CODE, CHANNEL_INFO, "*", QUEUE.size()));
        }
        return new ResponseVO<>(find.get() ? ResponseVO.SUCCESS : ResponseVO.FAILED, find.get());
    }

    @Override
    public ResponseVO<Integer> getQueueInfo() {
        return new ResponseVO<>(ResponseVO.SUCCESS, QUEUE.size());
    }

    @Override
    public void match() {
        QUEUE.forEach(q -> {
            int expect = q.getGamerSize();
            String key = q.getAllowBot().toString() + q.getGamerSize().toString();
            ConcurrentLinkedQueue<MatchExpectDTO> cache = caches.get(key);
            if (cache == null) {
                cache = new ConcurrentLinkedQueue<>();
                cache.add(q);
                caches.put(key, cache);
            } else {
                if (cache.size() == (expect - 1)) {
                    List<Long> ids = new ArrayList<>(cache.stream().map(MatchExpectDTO::getUserId).toList());
                    ids.add(q.getUserId());
                    String roomCode = gameRoomUserService.createGameRoomByUserIds(ids);
                    for (Long id : ids) {
                        executorService.execute(() -> messageService.send(CODE, CHANNEL_FOUND, String.valueOf(id), roomCode));
                    }
                    caches.put(key, new ConcurrentLinkedQueue<>());
                } else {
                    cache.add(q);
                    caches.put(key, cache);
                }
            }
        });
    }
}
