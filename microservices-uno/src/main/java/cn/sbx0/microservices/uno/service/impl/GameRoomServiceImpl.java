package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.mapper.GameRoomMapper;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}
