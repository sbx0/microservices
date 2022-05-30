package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

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

    boolean botJoinGameRoom(String roomCode, String botName);

    boolean botQuitGameRoom(String roomCode, String botName);

    List<AccountVO> getGamerByCode(String roomCode);

    Boolean isIAmIn(long roomId, long userId);

    boolean quitGameRoom(String roomCode);

    Integer countByGameRoom(String roomCode);

    String createGameRoomByUserIds(Set<Long> ids);

    String whereAmI(Long userId);

    boolean removeByRoomId(Long id);
}
