package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    private final static ConcurrentHashMap<String, SseEmitter> caches = new ConcurrentHashMap<>();
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
            List<GameRoomUserEntity> gamers = userService.listByGameRoom(roomCode);
            for (GameRoomUserEntity gamer : gamers) {
                cardService.drawCard(roomCode, gamer.getUserId(), 7);
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

        caches.put(userId, sseEmitter);

        return sseEmitter;
    }

    @Override
    public void message(String roomCode, String type, String message) {
        List<GameRoomUserEntity> users = userService.listByGameRoom(roomCode);
        if (!CollectionUtils.isEmpty(users)) {
            nonBlockingService.execute(() -> {
                List<String> ids = users.stream().map((one) -> one.getUserId().toString()).collect(Collectors.toList());
                for (String id : ids) {
                    SseEmitter sse = caches.get(id);
                    if (sse == null) {
                        continue;
                    }
                    try {
                        sse.send(SseEmitter.event().name(type).data(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sse.completeWithError(e);
                        caches.remove(id);
                    }
                }
            });
        }
    }
}
