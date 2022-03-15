package cn.sbx0.microservices.uno.service.impl;

import cn.sbx0.microservices.entity.AccountEntity;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.entity.GameRoomUserEntity;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.mapper.GameRoomUserMapper;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @Override
    public boolean joinGameRoom(String roomCode) {
        GameRoomEntity gameRoom = gameRoomService.getOneByRoomCode(roomCode);
        if (gameRoom == null || gameRoom.getRoomStatus() > 0) {
            return false;
        }
        GameRoomUserEntity gamer = new GameRoomUserEntity();
        AccountEntity account = accountService.loginInfo();
        gamer.setRoomId(gameRoom.getId());
        gamer.setUserId(account.getId());

        GameRoomUserEntity alreadyJoin = getBaseMapper().alreadyJoinByCreateUserId(account.getId());
        if (alreadyJoin != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime baseTime = now.minusMinutes(3);
            LocalDateTime lastTime = alreadyJoin.getUpdateTime();
            if (lastTime == null) {
                lastTime = alreadyJoin.getCrateTime();
            }
            if (baseTime.isBefore(lastTime)) {
                return false;
            }
        }

        gamer.setUsername(account.getUsername());
        gamer.setCreateUserId(account.getId());
        return save(gamer);
    }

    @Override
    public List<GameRoomUserEntity> listByGameRoom(String roomCode) {
        GameRoomEntity gameRoom = gameRoomService.getOneByRoomCode(roomCode);
        if (gameRoom == null || gameRoom.getRoomStatus() > 0) {
            return Collections.emptyList();
        }
        List<GameRoomUserEntity> gamers = getBaseMapper().listByGameRoom(gameRoom.getId(), gameRoom.getPlayersSize());
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(3);
        return gamers.stream().filter(one -> {
            if (one.getUpdateTime() != null) {
                return baseTime.isBefore(one.getUpdateTime());
            }
            return baseTime.isBefore(one.getCrateTime());
        }).collect(Collectors.toList());
    }
}
