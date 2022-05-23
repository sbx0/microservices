package cn.sbx0.microservices.uno.service;

import cn.sbx0.microservices.uno.entity.GameResultEntity;
import cn.sbx0.microservices.uno.entity.GameResultVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangh
 * @since 2022-05-19
 */
public interface IGameResultService extends IService<GameResultEntity> {

    List<GameResultEntity> listByGameRoomId(Long id);

    List<GameResultEntity> listByGameRoomCode(String roomCode);

    List<GameResultVO> listByGameRoom(String roomCode);
}
