package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.entity.*;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-03-10
 */
@Slf4j
@Service
public class GameRoomServiceImpl extends ServiceImpl<GameRoomMapper, GameRoomEntity> implements IGameRoomService {
    @Lazy
    @Resource
    private IGameRoomUserService userService;
    @Resource
    private IGameCardService cardService;
    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, SseEmitter>> caches = new ConcurrentHashMap<>();
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();


    @Override
    public String create(GameRoomCreateDTO dto) {
        long userId = StpUtil.getLoginIdAsLong();

        List<GameRoomEntity> alreadyCreatedButUnusedRooms = getBaseMapper().alreadyCreatedButUnusedRoomsByCreateUserId(userId);

        if (alreadyCreatedButUnusedRooms != null && alreadyCreatedButUnusedRooms.size() > 0) {
            return null;
        }

        GameRoomEntity entity = new GameRoomEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setRoomCode(UUID.randomUUID().toString());
        entity.setCreateUserId(userId);
        int number = getBaseMapper().insert(entity);
        if (number > 0) {
            return entity.getRoomCode();
        } else {
            return null;
        }
    }

    @Override
    public GameRoomEntity getOneByRoomCode(String roomCode) {
        return getBaseMapper().getOneByRoomCode(roomCode);
    }

    @Override
    public GameRoomInfoVO info(String roomCode) {
        GameRoomEntity room = getOneByRoomCode(roomCode);
        long userId = StpUtil.getLoginIdAsLong();
        GameRoomInfoVO vo = new GameRoomInfoVO();
        BeanUtils.copyProperties(room, vo);
        vo.setIsIAmIn(userService.isIAmIn(room.getId(), userId));
        return vo;
    }

    @Override
    public Boolean start(String roomCode) {
        GameRoomEntity room = getOneByRoomCode(roomCode);
        room.setRoomStatus(GameRoomStatusEnum.BEGINNING.getValue());
        boolean result = updateById(room);
        if (result) {
            cardService.initCardDeck(roomCode);
            List<AccountVO> gamers = userService.listByGameRoom(roomCode);
            for (AccountVO gamer : gamers) {
                List<CardEntity> cardEntities = cardService.drawCard(roomCode, gamer.getId(), 7);
                nonBlockingService.execute(() -> message(roomCode, "draw_card", gamer.getId().toString(), cardEntities));
            }
        }
        return result;
    }

    @Override
    public SseEmitter subscribe(String roomCode) {
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

        ConcurrentHashMap<String, SseEmitter> cache = caches.get(roomCode);
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
        }
        cache.put(userId, sseEmitter);
        caches.put(roomCode, cache);

        return sseEmitter;
    }

    @Override
    public void message(String roomCode, String type, String userId, Object message) {
        nonBlockingService.execute(() -> {
            if ("*".equals(roomCode)) {
                for (Map.Entry<String, ConcurrentHashMap<String, SseEmitter>> cs : caches.entrySet()) {
                    ConcurrentHashMap<String, SseEmitter> cache = cs.getValue();
                    sendMessage(cs.getKey(), type, userId, message, cache);
                }
            } else {
                ConcurrentHashMap<String, SseEmitter> cache = caches.get(roomCode);
                sendMessage(roomCode, type, userId, message, cache);
            }
        });
    }

    private void sendMessage(String roomCode, String type, String userId, Object message, ConcurrentHashMap<String, SseEmitter> cache) {
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
                caches.put(roomCode, cache);
            }
        }
    }
}
