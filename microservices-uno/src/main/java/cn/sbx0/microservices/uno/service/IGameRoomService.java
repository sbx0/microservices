package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-03-10
 */
public interface IGameRoomService extends IService<GameRoomEntity> {
    String create(GameRoomCreateDTO dto);

    GameRoomEntity getOneByRoomCode(String roomCode);

    GameRoomInfoVO info(String roomCode);

    Boolean start(String roomCode);

    String choose(String roomCode);
}
