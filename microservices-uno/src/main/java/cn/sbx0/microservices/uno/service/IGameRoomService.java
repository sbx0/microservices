package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.uno.entity.GameRoomCreateDTO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    void message(String roomCode, String type, Object message);

    SseEmitter subscribe(String roomCode);
}
