package cn.sbx0.microservices.uno.logic;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
import cn.sbx0.microservices.uno.constant.CardPoint;
import cn.sbx0.microservices.uno.constant.GameRedisKey;
import cn.sbx0.microservices.uno.constant.MessageChannel;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.entity.GameResultEntity;
import cn.sbx0.microservices.uno.entity.GameRoomEntity;
import cn.sbx0.microservices.uno.service.IGameResultService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
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
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    private IGameRoomService roomService;
    @Lazy
    @Resource
    private IGameRoomUserService userService;
    @Resource
    private IGameResultService resultService;
    @Resource
    private IMessageService messageService;
    @Resource
    private RandomBot randomBot;

    public AccountVO getCurrentGamer(String roomCode) {
        String currentGamerKey = GameRedisKey.CURRENT_GAMER.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
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
            if (current.getPoint().contains(CardPoint.WILD)) {
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
            if ((CardPoint.WILD_DRAW_FOUR.equals(previous.getPoint()) && !CardPoint.WILD_DRAW_FOUR.equals(current.getPoint())) || (CardPoint.DRAW_TWO.equals(previous.getPoint()) && !CardPoint.DRAW_TWO.equals(current.getPoint()) && !CardPoint.WILD_DRAW_FOUR.equals(current.getPoint()))) {
                String penaltyCardsKey = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
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
        String key = GameRedisKey.ROOM_DISCARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        if (size > 5) {
            redisTemplate.opsForList().rightPop(key);
        }
        redisTemplate.opsForList().leftPush(key, card);
        nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.DISCARD_CARDS, "*", card));
    }

    public void functionCard(String roomCode, CardEntity card) {
        String key;
        String direction;
        int size = 2;
        switch (card.getPoint()) {
            case CardPoint.REVERSE:
                key = GameRedisKey.ROOM_DIRECTION.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
                direction = stringRedisTemplate.opsForValue().get(key);
                if (CardPoint.NORMAL.equals(direction)) {
                    direction = CardPoint.REVERSE;
                } else {
                    direction = CardPoint.NORMAL;
                }
                stringRedisTemplate.opsForValue().set(key, direction);
                String finalDirection = direction;
                nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.DIRECTION, "*", finalDirection));
                step(roomCode, 1);
                return;
            case CardPoint.WILD_DRAW_FOUR:
                size = 4;
            case CardPoint.DRAW_TWO:
                key = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
                String numberStr = stringRedisTemplate.opsForValue().get(key);
                if (numberStr == null) {
                    numberStr = "0";
                }
                int number = Integer.parseInt(numberStr);
                number += size;
                String value = String.valueOf(number);
                stringRedisTemplate.opsForValue().set(key, value);
                nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.PENALTY_CARDS, "*", value));
                step(roomCode, 1);
                return;
            case CardPoint.SKIP:
                step(roomCode, 2);
                return;
            default:
                step(roomCode, 1);
        }
    }

    public void step(String roomCode, int step) {
        String drawKey = GameRedisKey.ROOM_DRAW.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String key = GameRedisKey.CURRENT_GAMER.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String currentGamer = stringRedisTemplate.opsForValue().get(key);
        if (currentGamer == null) {
            currentGamer = "0";
        }
        List<AccountVO> gamers = userService.listByGameRoom(roomCode);
        List<GameResultEntity> results = resultService.listByGameRoomCode(roomCode);
        Set<Long> ids = results.stream().map(GameResultEntity::getUserId)
                .collect(Collectors.toSet());
        String directionKey = GameRedisKey.ROOM_DIRECTION.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String direction = stringRedisTemplate.opsForValue().get(directionKey);
        int newIndex = whoNext(Integer.parseInt(currentGamer), gamers, ids, direction, step);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(newIndex));
        nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.WHO_TURN, "*", String.valueOf(newIndex)));
        randomBot.notify(roomCode, gamers.get(newIndex).getId());
        stringRedisTemplate.expire(drawKey, Duration.ZERO);
    }

    public int whoNext(int currentGamer, List<AccountVO> gamers, Set<Long> ids, String direction, int step) {
        int stepCount = 0;
        for (int i = currentGamer; i < gamers.size() && stepCount <= step; i++) {
            if (ids.contains(gamers.get(i).getId())) {
                continue;
            }
            if (CardPoint.NORMAL.equals(direction)) {
                stepCount++;
                i = (i + 1) % gamers.size();
            } else {
                stepCount++;
                i = (i - 1 + gamers.size()) % gamers.size();
            }
            if (stepCount == step) {
                currentGamer = i;
            }
        }
        return currentGamer;
    }


    public void lastCard(String roomCode, Long userId) {
        // current room users
        List<AccountVO> gamers = userService.listByGameRoom(roomCode);
        // room round
        GameRoomEntity room = roomService.getOneByRoomCode(roomCode);
        // game result
        List<GameResultEntity> results = resultService.listByGameRoomId(room.getId());
        Set<Long> ids = results.stream().map(GameResultEntity::getUserId)
                .collect(Collectors.toSet());
        Set<Long> remainIds = gamers.stream().map(AccountVO::getId)
                .filter(id -> !ids.contains(id))
                .collect(Collectors.toSet());
        if (remainIds.contains(userId)) {
            GameResultEntity gameResult = new GameResultEntity();
            gameResult.setRoomId(room.getId());
            gameResult.setRound(room.getRound());
            gameResult.setUserId(userId);
            gameResult.setRanking(ids.size() + 1);
            resultService.save(gameResult);
        }
    }
}
