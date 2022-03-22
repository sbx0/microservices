package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-03-15
 */
public interface IGameRoomUserService extends IService<GameRoomUserEntity> {
    boolean joinGameRoom(String roomCode);

    List<GameRoomUserEntity> listByGameRoom(String roomCode);

    Boolean isIAmIn(long roomId, long userId);

    boolean quitGameRoom(String roomCode);

    Integer countByGameRoom(String roomCode);

}
