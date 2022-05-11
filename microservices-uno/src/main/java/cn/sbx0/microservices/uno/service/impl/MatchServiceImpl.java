package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.entity.QueueInfoVO;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
    private final static ConcurrentHashMap<String, ConcurrentLinkedQueue<MatchExpectDTO>> CACHES = new ConcurrentHashMap<>();
    private final static Set<Long> IDS_CACHE = new HashSet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Resource
    private IMessageService messageService;
    @Resource
    private IGameRoomUserService gameRoomUserService;

    @Override
    public ResponseVO<Boolean> join(MatchExpectDTO dto) {
        // check exist
        String roomCode = gameRoomUserService.whereAmI(dto.getUserId());
        if (StringUtils.hasText(roomCode)) {
            executorService.execute(() -> messageService.send(CODE, CHANNEL_FOUND, String.valueOf(dto.getUserId()), roomCode));
        } else {
            if (IDS_CACHE.contains(dto.getUserId())) {
                return new ResponseVO<>(ResponseVO.FAILED, false);
            }
            QUEUE.addLast(dto);
            IDS_CACHE.add(dto.getUserId());
            executorService.execute(() -> messageService.send(CODE, CHANNEL_INFO, "*", IDS_CACHE.size()));
        }
        return new ResponseVO<>(ResponseVO.SUCCESS, true);
    }

    @Override
    public ResponseVO<Boolean> quit(Long userId) {
        QUEUE.forEach(q -> {
            if (userId.equals(q.getUserId())) {
                QUEUE.remove(q);
            }
        });
        IDS_CACHE.remove(userId);
        executorService.execute(() -> messageService.send(CODE, CHANNEL_INFO, "*", IDS_CACHE.size()));
        return new ResponseVO<>(ResponseVO.SUCCESS, true);
    }

    @Override
    public ResponseVO<QueueInfoVO> getQueueInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        QueueInfoVO info = new QueueInfoVO();
        info.setSize(IDS_CACHE.size());
        info.setJoin(IDS_CACHE.contains(userId));
        return new ResponseVO<>(ResponseVO.SUCCESS, info);
    }

    @Override
    public void match() {
        if (QUEUE.size() == 0) return;
        MatchExpectDTO q = QUEUE.getFirst();
        if (q == null) return;
        int expect = q.getGamerSize();
        String key = q.getAllowBot().toString() + q.getGamerSize().toString();
        ConcurrentLinkedQueue<MatchExpectDTO> cache = CACHES.get(key);
        if (cache == null) {
            cache = new ConcurrentLinkedQueue<>();
            cache.add(q);
            CACHES.put(key, cache);
        } else {
            if (cache.size() == (expect - 1)) {
                Set<Long> ids = cache.stream().map(MatchExpectDTO::getUserId).collect(Collectors.toSet());
                ids.add(q.getUserId());
                if (ids.size() == expect) {
                    String roomCode = gameRoomUserService.createGameRoomByUserIds(ids);
                    for (Long id : ids) {
                        executorService.execute(() -> messageService.send(CODE, CHANNEL_FOUND, String.valueOf(id), roomCode));
                    }
                    ids.forEach(IDS_CACHE::remove);
                    executorService.execute(() -> messageService.send(CODE, CHANNEL_INFO, "*", IDS_CACHE.size()));
                    CACHES.put(key, new ConcurrentLinkedQueue<>());
                }
            } else {
                cache.add(q);
                CACHES.put(key, cache);
            }
        }
        QUEUE.remove(q);
    }
}
