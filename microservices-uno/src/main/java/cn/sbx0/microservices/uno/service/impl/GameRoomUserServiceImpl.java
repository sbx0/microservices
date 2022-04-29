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
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    @Override
    public boolean botQuitGameRoom(String roomCode, String botName) {
        AccountVO account = accountService.findByUserName(botName);
        boolean result = getBaseMapper().quitGameRoom(account.getId());
        if (result) {
            nonBlockingService.execute(() -> gameRoomService.message(roomCode, "quit", "*", account));
        }
        return result;
    }

    @Override
    public boolean botJoinGameRoom(String roomCode, String botName) {
        GameRoomEntity gameRoom = gameRoomService.getOneByRoomCode(roomCode);
        if (gameRoom == null || gameRoom.getRoomStatus() > 0) {
            return false;
        }
        GameRoomUserEntity gamer = new GameRoomUserEntity();
        AccountVO account = accountService.findByUserName(botName);
        gamer.setRoomId(gameRoom.getId());
        gamer.setUserId(account.getId());
        gamer.setUsername(account.getNickname());
        gamer.setCreateUserId(account.getId());
        gamer.setRemark("RandomBot");
        int result = getBaseMapper().insert(gamer);
        if (result > 0) {
            nonBlockingService.execute(() -> gameRoomService.message(roomCode, "join", "*", account));
            return true;
        }
        return false;
    }

    @Override
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
            String sizeKey = "cards:" + roomCode + ":" + one.getUserId();
            Long size = stringRedisTemplate.opsForList().size(sizeKey);
            if (size == null) {
                size = 0L;
            }
            account.setNumberOfCards(Math.toIntExact(size));
            return account;
        }).collect(Collectors.toList());
    }

    @Override
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
