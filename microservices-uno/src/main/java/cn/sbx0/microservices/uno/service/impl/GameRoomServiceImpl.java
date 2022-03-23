package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.*;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-03-10
 */
@Service
public class GameRoomServiceImpl extends ServiceImpl<GameRoomMapper, GameRoomEntity> implements IGameRoomService {
    @Lazy
    @Resource
    private IGameRoomUserService userService;
    @Resource
    private IGameCardService cardService;

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

}
