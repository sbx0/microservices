package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-03-15
 */
@Service
public class GameRoomUserServiceImpl extends ServiceImpl<GameRoomUserMapper, GameRoomUserEntity> implements IGameRoomUserService {
    @Resource
    private IGameRoomService gameRoomService;
    @Resource
    private AccountService accountService;

    @Override
    @CacheEvict(cacheNames = "listByGameRoom", key = "#roomCode", condition = "#result")
    public boolean joinGameRoom(String roomCode) {
        GameRoomEntity gameRoom = gameRoomService.getOneByRoomCode(roomCode);
        if (gameRoom == null || gameRoom.getRoomStatus() > 0) {
            return false;
        }
        GameRoomUserEntity gamer = new GameRoomUserEntity();
        AccountVO account = accountService.loginInfo();
        gamer.setRoomId(gameRoom.getId());
        gamer.setUserId(account.getId());
        gamer.setUsername(account.getNickname());
        gamer.setCreateUserId(account.getId());
        return getBaseMapper().atomSave(gamer, gameRoom.getPlayersSize());
    }

    @Override
    @CacheEvict(cacheNames = "listByGameRoom", key = "#roomCode", condition = "#result")
    public boolean quitGameRoom(String roomCode) {
        return getBaseMapper().quitGameRoom(StpUtil.getLoginIdAsLong());
    }

    @Override
    @Cacheable(cacheNames = "listByGameRoom", key = "#roomCode")
    public List<GameRoomUserEntity> listByGameRoom(String roomCode) {
        GameRoomEntity gameRoom = gameRoomService.getOneByRoomCode(roomCode);
        if (gameRoom == null || gameRoom.getRoomStatus() > 0) {
            return Collections.emptyList();
        }
        return getBaseMapper().listByGameRoom(gameRoom.getId(), gameRoom.getPlayersSize());
    }

    @Override
    public Boolean isIAmIn(long roomId, long userId) {
        GameRoomUserEntity alreadyJoin = getBaseMapper().alreadyJoinByCreateUserId(userId);
        if (alreadyJoin == null) return false;
        return alreadyJoin.getRoomId() == roomId;
    }
}
