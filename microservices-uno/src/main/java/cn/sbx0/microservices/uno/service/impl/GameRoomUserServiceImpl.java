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
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "listByGameRoom", key = "#roomCode", condition = "#result"),
            @CacheEvict(cacheNames = "countByGameRoom", key = "#roomCode", condition = "#result")})
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
        boolean result = getBaseMapper().atomSave(gamer, gameRoom.getPlayersSize());
        if (result) {
            nonBlockingService.execute(() -> gameRoomService.message(roomCode, "join", "*", account));
        }
        return result;
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "listByGameRoom", key = "#roomCode", condition = "#result"),
            @CacheEvict(cacheNames = "countByGameRoom", key = "#roomCode", condition = "#result")})
    public boolean quitGameRoom(String roomCode) {
        String userId = StpUtil.getLoginIdAsString();
        boolean result = getBaseMapper().quitGameRoom(userId);
        if (result) {
            AccountVO account = accountService.loginInfo();
            nonBlockingService.execute(() -> gameRoomService.message(roomCode, "quit", "*", account));
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = "listByGameRoom", key = "#roomCode")
    public List<AccountVO> listByGameRoom(String roomCode) {
        GameRoomEntity gameRoom = gameRoomService.getOneByRoomCode(roomCode);
        if (gameRoom == null) {
            return Collections.emptyList();
        }
        List<GameRoomUserEntity> users = getBaseMapper().listByGameRoom(gameRoom.getId(), gameRoom.getPlayersSize());
        return users.stream().map((one) -> {
            AccountVO account = new AccountVO();
            account.setId(one.getUserId());
            account.setUsername(one.getUsername());
            account.setNickname(one.getUsername());
            return account;
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "countByGameRoom", key = "#roomCode")
    public Integer countByGameRoom(String roomCode) {
        GameRoomEntity gameRoom = gameRoomService.getOneByRoomCode(roomCode);
        if (gameRoom == null) {
            return 0;
        }
        return getBaseMapper().countByGameRoom(gameRoom.getId());
    }

    @Override
    public Boolean isIAmIn(long roomId, long userId) {
        GameRoomUserEntity alreadyJoin = getBaseMapper().alreadyJoinByCreateUserId(userId);
        if (alreadyJoin == null) return false;
        return alreadyJoin.getRoomId() == roomId;
    }
}
