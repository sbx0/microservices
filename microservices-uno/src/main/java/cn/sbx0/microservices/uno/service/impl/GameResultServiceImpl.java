package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.uno.entity.GameResultEntity;
import cn.sbx0.microservices.uno.mapper.GameResultMapper;
import cn.sbx0.microservices.uno.service.IGameResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-05-19
 */
@Service
public class GameResultServiceImpl extends ServiceImpl<GameResultMapper, GameResultEntity> implements IGameResultService {

    @Override
    public List<GameResultEntity> listByGameRoomId(Long id) {
        return getBaseMapper().listByGameRoomId(id);
    }

    @Override
    public List<GameResultEntity> listByGameRoomCode(String roomCode) {
        return getBaseMapper().listByGameRoomCode(roomCode);
    }
}
