package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.IDsDTO;
import cn.sbx0.microservices.uno.entity.GameResultConverter;
import cn.sbx0.microservices.uno.entity.GameResultEntity;
import cn.sbx0.microservices.uno.entity.GameResultVO;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.mapper.GameResultMapper;
import cn.sbx0.microservices.uno.service.IGameResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Resource
    private AccountService accountService;

    @Override
    public List<GameResultEntity> listByGameRoomId(Long id) {
        return getBaseMapper().listByGameRoomId(id);
    }

    @Override
    public List<GameResultEntity> listByGameRoomCode(String roomCode) {
        return getBaseMapper().listByGameRoomCode(roomCode);
    }

    @Override
    public List<GameResultVO> listByGameRoom(String roomCode) {
        List<GameResultEntity> entities = listByGameRoomCode(roomCode);
        List<GameResultVO> vos = GameResultConverter.INSTANCE.entitiesToVOs(entities);
        Set<Long> ids = vos.stream().map(GameResultVO::getUserId).collect(Collectors.toSet());
        Map<Long, String> cache = accountService.mapNameByIds(new IDsDTO(ids));
        for (GameResultVO vo : vos) {
            vo.setUserName(cache.getOrDefault(vo.getUserId(), "not found"));
        }
        return vos;
    }
}
