package cn.sbx0.microservices.uno.logic;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.constant.GameRedisKeyConstant;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import cn.sbx0.microservices.uno.service.IMessageService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sbx0
 * @since 2022/5/9
 */
@Component
public class BasicGameRule {
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();
    @Resource
    private RedisTemplate<String, CardEntity> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Lazy
    @Resource
    private IGameRoomUserService userService;
    @Resource
    private IMessageService messageService;

    public AccountVO getCurrentGamer(String roomCode) {
        String currentGamerKey = GameRedisKeyConstant.CURRENT_GAMER.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
        String currentGamerStr = stringRedisTemplate.opsForValue().get(currentGamerKey);
        if (!StringUtils.hasText(currentGamerStr)) {
            currentGamerStr = "0";
        }
        int index = Integer.parseInt(currentGamerStr);
        List<AccountVO> gamers = userService.listByGameRoom(roomCode);
        if (CollectionUtils.isEmpty(gamers)) {
            return null;
        }
        return gamers.get(index);
    }

    public boolean canIPlayNow(String roomCode, Long userId) {
        AccountVO currentGamer = getCurrentGamer(roomCode);
        if (currentGamer == null) {
            return false;
        }
        return Objects.equals(currentGamer.getId(), userId);
    }

    public boolean judgeIsCanPlay(CardEntity previous, CardEntity current, Long currentUserId) {
        boolean canPlay = false;
        if (previous != null) {
            if (currentUserId.equals(previous.getUserId())) {
                canPlay = true;
            }
            if (current.getPoint().contains("wild")) {
                canPlay = true;
            }
            if (current.getColor().equals(previous.getColor())) {
                canPlay = true;
            }
            if (current.getPoint().equals(previous.getPoint())) {
                canPlay = true;
            }
        } else {
            canPlay = true;
        }
        return canPlay;
    }

    public boolean judgePenaltyCards(CardEntity previous, CardEntity current, String roomCode) {
        if (previous != null) {
            if (("wild draw four".equals(previous.getPoint()) && !"wild draw four".equals(current.getPoint())) || ("draw two".equals(previous.getPoint()) && !"draw two".equals(current.getPoint()) && !"wild draw four".equals(current.getPoint()))) {
                String penaltyCardsKey = GameRedisKeyConstant.ROOM_PENALTY.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
                String penaltyCards = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
                int size = 0;
                if (StringUtils.hasText(penaltyCards)) {
                    size = Integer.parseInt(penaltyCards);
                }
                return size <= 0;
            }
        }
        return true;
    }

    public void discardCard(String roomCode, CardEntity card) {
        String key = GameRedisKeyConstant.ROOM_DISCARDS.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        if (size > 5) {
            redisTemplate.opsForList().rightPop(key);
        }
        redisTemplate.opsForList().leftPush(key, card);
        nonBlockingService.execute(() -> messageService.send(roomCode, "discard_cards", "*", card));
    }
}
