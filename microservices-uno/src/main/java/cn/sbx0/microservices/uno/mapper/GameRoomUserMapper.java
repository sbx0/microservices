package cn.sbx0.microservices.uno.mapper;

import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangh
 * @since 2022-03-15
 */
public interface GameRoomUserMapper extends BaseMapper<GameRoomUserEntity> {
    GameRoomUserEntity alreadyJoinByCreateUserId(Long id);

    List<GameRoomUserEntity> listByGameRoom(@Param("id") Long id, @Param("size") Integer size);

    boolean atomSave(@Param("entity") GameRoomUserEntity gamer, @Param("size") Integer size);

    boolean quitGameRoom(long userId);
}
