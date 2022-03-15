package cn.sbx0.microservices.uno.mapper;

import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangh
 * @since 2022-03-10
 */
public interface GameRoomMapper extends BaseMapper<GameRoomEntity> {
    List<GameRoomEntity> alreadyCreatedButUnusedRoomsByCreateUserId(long userId);

    GameRoomEntity getOneByRoomCode(String roomCode);
}
