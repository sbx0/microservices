package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.entity.CardDeckEntity;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author wangh
 * @since 2022-03-22
 */
@Service
public class GameCardServiceImpl implements IGameCardService {
    @Resource
    private RedisTemplate<String, CardEntity> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();
    @Lazy
    @Resource
    private IGameRoomService gameRoomService;
    @Lazy
    @Resource
    private IGameRoomUserService userService;

    @Override
    public void initCardDeck(String roomCode) {
        String key = "cards:" + roomCode;
        List<CardEntity> cards = CardDeckEntity.CARDS;
        Collections.shuffle(cards);
        redisTemplate.opsForList().leftPushAll(key, cards);
        extensionOfTime(key);
    }

    @Override
    public List<CardEntity> drawCard(String roomCode) {
        String key = "cards:" + roomCode;
        String keyPlusUserID = key + ":" + StpUtil.getLoginIdAsString();
        List<CardEntity> cards = new ArrayList<>();
        List<CardEntity> pops = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            pops.add(redisTemplate.opsForList().rightPop(key));
        }
        if (!CollectionUtils.isEmpty(pops)) {
            cards.addAll(pops);
        }
        redisTemplate.opsForList().leftPushAll(keyPlusUserID, cards);
        extensionOfTime(key);
        extensionOfTime(keyPlusUserID);
        return cards;
    }

    @Override
    public List<CardEntity> drawCard(String roomCode, int number) {
        return drawCard(roomCode, StpUtil.getLoginIdAsLong(), number);
    }

    @Override
    public List<CardEntity> drawCard(String roomCode, Serializable userId, int number) {
        String sizeKey = "cards:" + roomCode + ":" + StpUtil.getLoginIdAsString();
        Long size = redisTemplate.opsForList().size(sizeKey);
        String key = "cards:" + roomCode;
        String keyPlusUserID = key + ":" + userId;
        if (number < 1) {
            number = 1;
        }
        List<CardEntity> cards = new ArrayList<>();
        List<CardEntity> pops = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            CardEntity card = redisTemplate.opsForList().rightPop(key);
            if (card == null) {
                initCardDeck(roomCode);
                card = redisTemplate.opsForList().rightPop(key);
            }
            pops.add(card);
        }
        if (!CollectionUtils.isEmpty(pops)) {
            cards.addAll(pops);
        }
        redisTemplate.opsForList().leftPushAll(keyPlusUserID, cards);
        nonBlockingService.execute(() -> gameRoomService.message(roomCode, "number_of_cards", "*", userId + "=" + size));
        extensionOfTime(key);
        extensionOfTime(keyPlusUserID);
        return cards;
    }

    @Override
    public List<CardEntity> myCardList(String roomCode) {
        String key = "cards:" + roomCode + ":" + StpUtil.getLoginIdAsString();
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        extensionOfTime(key);
        return redisTemplate.opsForList().range(key, 0, size);
    }

    @Override
    public Boolean playCard(String roomCode, String uuid, String color) {
        String userId = StpUtil.getLoginIdAsString();
        String currentGamerKey = "currentGamer:" + roomCode;
        String currentGamerStr = stringRedisTemplate.opsForValue().get(currentGamerKey);
        if (currentGamerStr == null) {
            currentGamerStr = "0";
        }
        int index = Integer.parseInt(currentGamerStr);
        List<AccountVO> gamers = userService.listByGameRoom(roomCode);
        AccountVO currentGamer = gamers.get(index);
        if (currentGamer == null) {
            return false;
        }
        if (currentGamer.getId() != Long.parseLong(userId)) {
            return false;
        }
        String key = "cards:" + roomCode + ":" + userId;
        String discardKey = "cards:" + roomCode + ":discard";
        List<CardEntity> cards = myCardList(roomCode);

        for (CardEntity card : cards) {
            if (uuid.equals(card.getUuid())) {
                CardEntity top = redisTemplate.opsForList().index(discardKey, 0);

                boolean canPlay = false;
                if (top != null) {
                    if (top.getColor().equals("black")) {
                        canPlay = true;
                    }
                    if (card.getColor().equals("black")) {
                        canPlay = true;
                    }
                    if (card.getColor().equals(top.getColor())) {
                        canPlay = true;
                    }
                    if (card.getPoint().equals(top.getPoint())) {
                        canPlay = true;
                    }
                } else {
                    canPlay = true;
                }

                if (canPlay) {
                    functionCard(roomCode, card);
                    cards.remove(card);
                    card.setColor(color);
                    nonBlockingService.execute(() -> gameRoomService.message(roomCode, "number_of_cards", "*", userId + "=" + cards.size()));
                    discardCard(roomCode, card);
                    break;
                } else {
                    return false;
                }
            }
        }
        redisTemplate.expire(key, Duration.ZERO);
        if (!CollectionUtils.isEmpty(cards)) {
            redisTemplate.opsForList().leftPushAll(key, cards);
        }
        return true;
    }

    private void functionCard(String roomCode, CardEntity card) {
        String key;
        String direction;
        int size = 2;
        switch (card.getPoint()) {
            case "reverse":
                key = "direction:" + roomCode;
                direction = stringRedisTemplate.opsForValue().get(key);
                if ("normal".equals(direction)) {
                    direction = "reverse";
                } else {
                    direction = "normal";
                }
                stringRedisTemplate.opsForValue().set(key, direction);
                extensionOfTime(key);
                step(roomCode, 1);
                return;
            case "wild draw four":
                size = 4;
            case "draw two":
                key = "penaltyCards:" + roomCode;
                String numberStr = stringRedisTemplate.opsForValue().get(key);
                if (numberStr == null) {
                    numberStr = "0";
                }
                int number = Integer.parseInt(numberStr);
                number += size;
                stringRedisTemplate.opsForValue().set(key, String.valueOf(number));
                extensionOfTime(key);
                step(roomCode, 1);
                return;
            case "skip":
                step(roomCode, 2);
            default:
                step(roomCode, 1);
        }
    }

    private void step(String roomCode, int step) {
        String key = "currentGamer:" + roomCode;
        String currentGamer = stringRedisTemplate.opsForValue().get(key);
        if (currentGamer == null) {
            currentGamer = "0";
        }
        List<AccountVO> gamers = userService.listByGameRoom(roomCode);
        String directionKey = "direction:" + roomCode;
        String direction = stringRedisTemplate.opsForValue().get(directionKey);
        int newIndex = Integer.parseInt(currentGamer);
        if ("normal".equals(direction)) {
            newIndex = (newIndex + step) % gamers.size();
        } else {
            newIndex = (newIndex - step + gamers.size()) % gamers.size();
        }
        stringRedisTemplate.opsForValue().set(key, String.valueOf(newIndex));
        int finalNewIndex = newIndex;
        nonBlockingService.execute(() -> gameRoomService.message(roomCode, "who_turn", "*", String.valueOf(finalNewIndex)));
        extensionOfTime(key);
    }

    @Override
    public void discardCard(String roomCode, CardEntity card) {
        String key = "cards:" + roomCode + ":discard";
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        if (size > 10) {
            redisTemplate.opsForList().rightPop(key);
        }
        redisTemplate.opsForList().leftPush(key, card);
        nonBlockingService.execute(() -> gameRoomService.message(roomCode, "discard_cards", "*", card));
        extensionOfTime(key);
    }

    @Override
    public List<CardEntity> discardCardList(String roomCode) {
        String key = "cards:" + roomCode + ":discard";
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        return redisTemplate.opsForList().range(key, 0, size);
    }

    @Override
    public void initGame(String roomCode) {
        // normal reverse
        stringRedisTemplate.opsForValue().set("direction:" + roomCode, "normal");
        extensionOfTime("direction:" + roomCode);
        stringRedisTemplate.opsForValue().set("currentGamer:" + roomCode, "0");
        extensionOfTime("currentGamer:" + roomCode);
        stringRedisTemplate.opsForValue().set("penaltyCards:" + roomCode, "0");
        extensionOfTime("penaltyCards:" + roomCode);
    }

    private void extensionOfTime(String key) {
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }
}
