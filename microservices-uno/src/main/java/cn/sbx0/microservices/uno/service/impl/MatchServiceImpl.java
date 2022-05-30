package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.ResponseVO;
import cn.sbx0.microservices.uno.entity.MatchExpectDTO;
import cn.sbx0.microservices.uno.entity.MessageDTO;
import cn.sbx0.microservices.uno.entity.QueueInfoVO;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMatchService;
import cn.sbx0.microservices.uno.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Resource
    private IMessageService messageService;
    @Resource
    private IGameRoomUserService gameRoomUserService;
    public static final String IDS_CACHE = "game:match:ids_cache";
    public static final String CHOOSE_CACHE = "game:match:choose_cache";
    public static final String DELETE_IDS = "game:match:delete_ids";
    private final String[] ALLOW_BOT = {"false,", "true,"};
    private final String[] SIZES = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    @Resource
    private RedisTemplate<String, MatchExpectDTO> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResponseVO<Boolean> join(MatchExpectDTO dto) {
        // check exist
        String roomCode = gameRoomUserService.whereAmI(dto.getUserId());
        if (StringUtils.hasText(roomCode)) {
            executorService.execute(() -> messageService.send(new MessageDTO<>(CODE, CHANNEL_FOUND, String.valueOf(dto.getUserId()), roomCode)));
        } else {
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(IDS_CACHE, dto.getUserId().toString()))) {
                return new ResponseVO<>(ResponseVO.FAILED, false);
            }
            stringRedisTemplate.opsForSet().add(IDS_CACHE, dto.getUserId().toString());
            stringRedisTemplate.opsForSet().remove(DELETE_IDS, dto.getUserId().toString());
            String key = "game:match:queue:" + dto.getAllowBot().toString() + "," + dto.getGamerSize().toString();
            stringRedisTemplate.opsForHash().put(CHOOSE_CACHE, dto.getUserId().toString(), dto.getGamerSize().toString());
            Long index = redisTemplate.opsForList().indexOf(key, dto);
            if (index == null) {
                redisTemplate.opsForList().rightPush(key, dto);
            }
            executorService.execute(() -> messageService.send(new MessageDTO<>(CODE, CHANNEL_INFO, "*", stringRedisTemplate.opsForSet().size(IDS_CACHE))));
        }
        return new ResponseVO<>(ResponseVO.SUCCESS, true);
    }

    @Override
    public ResponseVO<Boolean> quit(Long userId) {
        stringRedisTemplate.opsForSet().remove(IDS_CACHE, userId.toString());
        stringRedisTemplate.opsForSet().add(DELETE_IDS, userId.toString());
        executorService.execute(() -> messageService.send(new MessageDTO<>(CODE, CHANNEL_INFO, "*", stringRedisTemplate.opsForSet().size(IDS_CACHE))));
        return new ResponseVO<>(ResponseVO.SUCCESS, true);
    }

    @Override
    public ResponseVO<QueueInfoVO> getQueueInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        QueueInfoVO info = new QueueInfoVO();
        Long size = stringRedisTemplate.opsForSet().size(IDS_CACHE);
        if (size == null) {
            size = 0L;
        }
        String choose = (String) stringRedisTemplate.opsForHash().get(CHOOSE_CACHE, Long.toString(userId));
        info.setChoose(choose);
        info.setSize(size.intValue());
        info.setJoin(Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(IDS_CACHE, Long.toString(userId))));
        return new ResponseVO<>(ResponseVO.SUCCESS, info);
    }

    @Override
    public void match() {
        for (String sizeKey : SIZES) {
            String key = "game:match:queue:" + ALLOW_BOT[0] + sizeKey;
            int size = Integer.parseInt(sizeKey);
            List<MatchExpectDTO> matched = new ArrayList<>();
            Long queueSize = redisTemplate.opsForList().size(key);
            if (queueSize == null) {
                queueSize = 0L;
            }
            if (queueSize < size) {
                continue;
            }
            Set<Long> ids = new HashSet<>();
            for (int i = 0; i < size; ) {
                MatchExpectDTO poll = redisTemplate.opsForList().leftPop(key);
                if (poll == null) {
                    continue;
                }
                if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(DELETE_IDS, poll.getUserId().toString()))) {
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
                String roomCode = gameRoomUserService.createGameRoomByUserIds(ids);
                for (Long id : ids) {
                    executorService.execute(() -> messageService.send(new MessageDTO<>(CODE, CHANNEL_FOUND, String.valueOf(id), roomCode)));
                }
                for (Long id : ids) {
                    stringRedisTemplate.opsForSet().remove(IDS_CACHE, id);
                }
            } else {
                for (MatchExpectDTO dto : matched) {
                    redisTemplate.opsForList().rightPush(key, dto);
                }
            }
            executorService.execute(() -> messageService.send(new MessageDTO<>(CODE, CHANNEL_INFO, "*", stringRedisTemplate.opsForSet().size(IDS_CACHE))));
        }
    }
}
