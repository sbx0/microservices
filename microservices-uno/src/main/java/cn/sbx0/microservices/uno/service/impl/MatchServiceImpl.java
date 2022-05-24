package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.entity.QueueInfoVO;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final static ConcurrentHashMap<String, ConcurrentLinkedQueue<MatchExpectDTO>> CACHES = new ConcurrentHashMap<>();
    private final static Set<Long> IDS_CACHE = new HashSet<>();
    private final static Set<Long> DELETE_IDS = new HashSet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Resource
    private IMessageService messageService;
    @Resource
    private IGameRoomUserService gameRoomUserService;
    @Resource
    private RandomBot randomBot;
    private List<MatchExpectDTO> BOTS;

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
            IDS_CACHE.add(dto.getUserId());
            DELETE_IDS.remove(dto.getUserId());
            String key = dto.getAllowBot().toString() + "," + dto.getGamerSize().toString();
            ConcurrentLinkedQueue<MatchExpectDTO> queue = CACHES.get(key);
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }
            queue.add(dto);
            CACHES.put(key, queue);
            executorService.execute(() -> messageService.send(CODE, CHANNEL_INFO, "*", IDS_CACHE.size()));
        }
        return new ResponseVO<>(ResponseVO.SUCCESS, true);
    }

    @Override
    public ResponseVO<Boolean> quit(Long userId) {
        IDS_CACHE.remove(userId);
        DELETE_IDS.add(userId);
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
        if (BOTS == null) {
            BOTS = new ArrayList<>();
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 2, true));
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 3, true));
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 4, true));
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 5, true));
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 6, true));
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 7, true));
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 8, true));
            BOTS.add(new MatchExpectDTO(randomBot.getId(), 9, true));
            for (MatchExpectDTO bot : BOTS) {
                String key = bot.getAllowBot().toString() + "," + bot.getGamerSize().toString();
                ConcurrentLinkedQueue<MatchExpectDTO> queue = CACHES.get(key);
                if (queue == null) {
                    queue = new ConcurrentLinkedQueue<>();
                }
                queue.add(bot);
            }
        }
        for (Map.Entry<String, ConcurrentLinkedQueue<MatchExpectDTO>> cache : CACHES.entrySet()) {
            String keys = cache.getKey();
            String[] split = keys.split(",");
            String allowBotKey = split[0];
            String sizeKey = split[1];
            boolean allowBot = Boolean.parseBoolean(allowBotKey);
            int size = Integer.parseInt(sizeKey);
            List<MatchExpectDTO> matched = new ArrayList<>();
            ConcurrentLinkedQueue<MatchExpectDTO> queue = cache.getValue();
            if (CollectionUtils.isEmpty(queue)) {
                continue;
            }
            if (queue.size() < size) {
                continue;
            }
            Set<Long> ids = new HashSet<>();
            for (int i = 0; i < size; ) {
                MatchExpectDTO poll = queue.poll();
                if (poll == null) {
                    continue;
                }
                if (DELETE_IDS.contains(poll.getUserId())) {
                    continue;
                }
                if (ids.contains(poll.getUserId())) {
                    continue;
                }
                matched.add(poll);
                ids.add(poll.getUserId());
                i++;
            }
            if (matched.size() == size) {
                CACHES.put(cache.getKey(), queue);
                String roomCode = gameRoomUserService.createGameRoomByUserIds(ids);
                for (Long id : ids) {
                    executorService.execute(() -> messageService.send(CODE, CHANNEL_FOUND, String.valueOf(id), roomCode));
                }
                ids.forEach(IDS_CACHE::remove);
            }
            executorService.execute(() -> messageService.send(CODE, CHANNEL_INFO, "*", IDS_CACHE.size()));
        }
    }
}
