package cn.sbx0.microservices.uno.logic;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
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
import java.time.Duration;
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
    @Resource
    private RandomBot randomBot;

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

    public void functionCard(String roomCode, CardEntity card) {
        String key;
        String direction;
        int size = 2;
        switch (card.getPoint()) {
            case "reverse":
                key = GameRedisKeyConstant.ROOM_DIRECTION.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
                direction = stringRedisTemplate.opsForValue().get(key);
                if ("normal".equals(direction)) {
                    direction = "reverse";
                } else {
                    direction = "normal";
                }
                stringRedisTemplate.opsForValue().set(key, direction);
                String finalDirection = direction;
                nonBlockingService.execute(() -> messageService.send(roomCode, "direction", "*", finalDirection));
                step(roomCode, 1);
                return;
            case "wild draw four":
                size = 4;
            case "draw two":
                key = GameRedisKeyConstant.ROOM_PENALTY.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
                String numberStr = stringRedisTemplate.opsForValue().get(key);
                if (numberStr == null) {
                    numberStr = "0";
                }
                int number = Integer.parseInt(numberStr);
                number += size;
                String value = String.valueOf(number);
                stringRedisTemplate.opsForValue().set(key, value);
                nonBlockingService.execute(() -> messageService.send(roomCode, "penalty_cards", "*", value));
                step(roomCode, 1);
                return;
            case "skip":
                step(roomCode, 2);
                return;
            default:
                step(roomCode, 1);
        }
    }

    public void step(String roomCode, int step) {
        String drawKey = GameRedisKeyConstant.ROOM_DRAW.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
        String key = GameRedisKeyConstant.CURRENT_GAMER.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
        String currentGamer = stringRedisTemplate.opsForValue().get(key);
        if (currentGamer == null) {
            currentGamer = "0";
        }
        List<AccountVO> gamers = userService.listByGameRoom(roomCode);
        String directionKey = GameRedisKeyConstant.ROOM_DIRECTION.replaceAll(GameRedisKeyConstant.ROOM_CODE, roomCode);
        String direction = stringRedisTemplate.opsForValue().get(directionKey);
        int newIndex = Integer.parseInt(currentGamer);
        if ("normal".equals(direction)) {
            newIndex = (newIndex + step) % gamers.size();
        } else {
            newIndex = (newIndex - step + gamers.size()) % gamers.size();
        }
        stringRedisTemplate.opsForValue().set(key, String.valueOf(newIndex));
        int finalNewIndex = newIndex;
        nonBlockingService.execute(() -> messageService.send(roomCode, "who_turn", "*", String.valueOf(finalNewIndex)));
        randomBot.notify(roomCode, gamers.get(newIndex).getId());
        stringRedisTemplate.expire(drawKey, Duration.ZERO);
    }


}
