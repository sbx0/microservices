package cn.sbx0.microservices.uno.mapper;

import cn.sbx0.microservices.uno.entity.GameResultEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangh
 * @since 2022-05-19
 */
public interface GameResultMapper extends BaseMapper<GameResultEntity> {
    List<GameResultEntity> listByGameRoomId(Long id);

    List<GameResultEntity> listByGameRoomCode(String roomCode);
}
