package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.constant.CardPoint;
import cn.sbx0.microservices.uno.constant.GameRedisKey;
import cn.sbx0.microservices.uno.constant.GameRoomStatus;
import cn.sbx0.microservices.uno.constant.MessageChannel;
import cn.sbx0.microservices.uno.entity.*;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.netflix.appinfo.ApplicationInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
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
    @Resource
    private ApplicationInfoManager applicationInfoManager;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Lazy
    @Resource
    private RandomBot randomBot;
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();
    @Resource
    private IMessageService messageService;


    @Override
    public String choose(String roomCode) {
        GameRoomEntity gameRoom = getOneByRoomCode(roomCode);
        if (gameRoom == null) {
            return null;
        }
        return gameRoom.getInstanceId();
    }

    @Override
    public String create(GameRoomCreateDTO dto, long userId) {
        if (userId != 0) {
            List<GameRoomEntity> alreadyCreatedButUnusedRooms = getBaseMapper().alreadyCreatedButUnusedRoomsByCreateUserId(userId);

            if (alreadyCreatedButUnusedRooms != null && alreadyCreatedButUnusedRooms.size() > 0) {
                return null;
            }
        }

        GameRoomEntity entity = GameRoomConverter.INSTANCE.dtoToEntity(dto);

        entity.setRoomCode(UUID.randomUUID().toString());
        entity.setCreateUserId(userId);
        entity.setInstanceId(applicationInfoManager.getInfo().getInstanceId());
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
    public GameRoomInfoVO getInfoByUserId(String roomCode, Long userId) {
        GameRoomEntity room = getOneByRoomCode(roomCode);
        if (room == null) {
            return null;
        }
        GameRoomInfoVO vo = GameRoomConverter.INSTANCE.entityToInfoVO(room);
        vo.setIsIAmIn(userService.isIAmIn(room.getId(), userId));
        String currentGamerKey = GameRedisKey.CURRENT_GAMER.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String currentGamerStr = stringRedisTemplate.opsForValue().get(currentGamerKey);
        if (currentGamerStr == null) {
            currentGamerStr = "0";
        }
        vo.setCurrentGamer(Integer.parseInt(currentGamerStr));
        String penaltyCardsKey = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String penaltyCards = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
        if (!StringUtils.hasText(penaltyCards)) {
            penaltyCards = "0";
        }
        vo.setPenaltyCards(Integer.parseInt(penaltyCards));
        String directionKey = GameRedisKey.ROOM_DIRECTION.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String direction = stringRedisTemplate.opsForValue().get(directionKey);
        if (!StringUtils.hasText(direction)) {
            direction = CardPoint.NORMAL;
        }
        vo.setDirection(direction);
        return vo;
    }

    @Override
    public GameRoomInfoVO info(String roomCode) {
        GameRoomEntity room = getOneByRoomCode(roomCode);
        if (room == null) {
            return null;
        }
        long userId = StpUtil.getLoginIdAsLong();
        GameRoomInfoVO vo = GameRoomConverter.INSTANCE.entityToInfoVO(room);
        vo.setIsIAmIn(userService.isIAmIn(room.getId(), userId));
        String currentGamerKey = GameRedisKey.CURRENT_GAMER.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String currentGamerStr = stringRedisTemplate.opsForValue().get(currentGamerKey);
        if (currentGamerStr == null) {
            currentGamerStr = "0";
        }
        vo.setCurrentGamer(Integer.parseInt(currentGamerStr));
        String penaltyCardsKey = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String penaltyCards = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
        if (!StringUtils.hasText(penaltyCards)) {
            penaltyCards = "0";
        }
        vo.setPenaltyCards(Integer.parseInt(penaltyCards));
        String directionKey = GameRedisKey.ROOM_DIRECTION.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String direction = stringRedisTemplate.opsForValue().get(directionKey);
        if (!StringUtils.hasText(direction)) {
            direction = CardPoint.NORMAL;
        }
        vo.setDirection(direction);
        return vo;
    }

    @Override
    public Boolean start(String roomCode) {
        GameRoomEntity room = getOneByRoomCode(roomCode);
        if (room == null) {
            return false;
        }
        room.setRoomStatus(GameRoomStatus.BEGINNING);
        boolean result = updateById(room);
        if (result) {
            cardService.initCardDeck(roomCode);
            List<AccountVO> gamers = userService.getGamerByCode(roomCode);
            if (CollectionUtils.isEmpty(gamers)) {
                return false;
            }
            cardService.initGame(roomCode);
            for (AccountVO gamer : gamers) {
                List<CardEntity> cardEntities = cardService.drawCard(roomCode, gamer.getId(), 7);
                nonBlockingService.execute(() -> messageService.send(new MessageDTO<>(roomCode, MessageChannel.DRAW_CARD, gamer.getId().toString(), cardEntities)));
            }
            randomBot.notify(roomCode, gamers.get(0).getId());
        }
        return result;
    }

    @Override
    public List<GameRoomEntity> pagingList(String keyword) {
        return getBaseMapper().pagingList(keyword);
    }
}
