package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.bot.RandomBot;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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
    @Lazy
    @Resource
    private RandomBot randomBot;
    public static final List<CardEntity> EMPTY = new ArrayList<>(0);

    @Override
    public boolean botPlayCard(String roomCode, String uuid, String color, Long id) {
        String userId = id.toString();
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
        List<CardEntity> cards = getCardListById(roomCode, id);

        boolean find = false;

        for (CardEntity card : cards) {
            if (uuid.equals(card.getUuid())) {
                find = true;
                CardEntity top = redisTemplate.opsForList().index(discardKey, 0);

                boolean canPlay = false;
                if (top != null) {
                    if (userId.equals(top.getUserId().toString())) {
                        canPlay = true;
                    }
                    if (card.getPoint().contains("wild")) {
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
                    if (top != null) {
                        if (("wild draw four".equals(top.getPoint()) && !"wild draw four".equals(card.getPoint())) || ("draw two".equals(top.getPoint()) && !"draw two".equals(card.getPoint()) && !"wild draw four".equals(card.getPoint()))) {
                            String penaltyCardsKey = "penaltyCards:" + roomCode;
                            String penaltyCards = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
                            int size = 0;
                            if (StringUtils.hasText(penaltyCards)) {
                                size = Integer.parseInt(penaltyCards);
                            }
                            if (size > 0) {
                                return false;
                            }
                        }
                    }
                    cards.remove(card);
                    card.setColor(color);
                    discardCard(roomCode, card);
                    functionCard(roomCode, card);
                    nonBlockingService.execute(() -> gameRoomService.message(roomCode, "number_of_cards", "*", userId + "=" + cards.size()));
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
        return find;
    }

    @Override
    public List<CardEntity> getCardListById(String roomCode, Long id) {
        String key = "cards:" + roomCode + ":" + id;
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        extensionOfTime(key);
        return redisTemplate.opsForList().range(key, 0, size);
    }

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
        String key = "penaltyCards:" + roomCode;
        String penaltyCards = stringRedisTemplate.opsForValue().get(key);
        int size = number;
        if (StringUtils.hasText(penaltyCards)) {
            size = Integer.parseInt(penaltyCards);
        }
        stringRedisTemplate.opsForValue().set(key, "0");
        nonBlockingService.execute(() -> gameRoomService.message(roomCode, "penalty_cards", "*", "0"));
        return drawCard(roomCode, StpUtil.getLoginIdAsLong(), size);
    }

    @Override
    public List<CardEntity> drawCard(String roomCode, Long userId, int number) {
        String drawKey = "cards:" + roomCode + ":draw";
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
            if (card == null) {
                return null;
            }
            card.setUserId(userId);
            pops.add(card);
        }
        if (!CollectionUtils.isEmpty(pops)) {
            cards.addAll(pops);
        }
        redisTemplate.opsForList().leftPushAll(keyPlusUserID, cards);
        stringRedisTemplate.opsForValue().set(drawKey, userId.toString());
        String sizeKey = "cards:" + roomCode + ":" + userId;
        Long size = redisTemplate.opsForList().size(sizeKey);
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

        boolean find = false;

        for (CardEntity card : cards) {
            if (uuid.equals(card.getUuid())) {
                find = true;
                CardEntity top = redisTemplate.opsForList().index(discardKey, 0);

                boolean canPlay = false;
                if (top != null) {
                    if (userId.equals(top.getUserId().toString())) {
                        canPlay = true;
                    }
                    if ("wild".equals(card.getPoint())) {
                        canPlay = true;
                    }
                    if ("wild draw four".equals(card.getPoint())) {
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
                    if (top != null) {
                        if (("wild draw four".equals(top.getPoint()) && !"wild draw four".equals(card.getPoint())) || ("draw two".equals(top.getPoint()) && !"draw two".equals(card.getPoint()) && !"wild draw four".equals(card.getPoint()))) {
                            String penaltyCardsKey = "penaltyCards:" + roomCode;
                            String penaltyCards = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
                            int size = 0;
                            if (StringUtils.hasText(penaltyCards)) {
                                size = Integer.parseInt(penaltyCards);
                            }
                            if (size > 0) {
                                return false;
                            }
                        }
                    }
                    cards.remove(card);
                    card.setColor(color);
                    discardCard(roomCode, card);
                    functionCard(roomCode, card);
                    nonBlockingService.execute(() -> gameRoomService.message(roomCode, "number_of_cards", "*", userId + "=" + cards.size()));
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
        return find;
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
                String finalDirection = direction;
                nonBlockingService.execute(() -> gameRoomService.message(roomCode, "direction", "*", finalDirection));
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
                String value = String.valueOf(number);
                stringRedisTemplate.opsForValue().set(key, value);
                nonBlockingService.execute(() -> gameRoomService.message(roomCode, "penalty_cards", "*", value));
                extensionOfTime(key);
                step(roomCode, 1);
                return;
            case "skip":
                step(roomCode, 2);
                return;
            default:
                step(roomCode, 1);
        }
    }

    @Override
    public List<CardEntity> botNextPlay(String roomCode, Long id) {
        List<CardEntity> cards = EMPTY;
        String penaltyCardsKey = "penaltyCards:" + roomCode;
        String numberStr = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
        if (numberStr == null) {
            numberStr = "0";
        }
        int number = Integer.parseInt(numberStr);
        if (number > 0) {
            cards = drawCard(roomCode, id, number);
            stringRedisTemplate.opsForValue().set(penaltyCardsKey, "0");
            nonBlockingService.execute(() -> gameRoomService.message(roomCode, "penalty_cards", "*", "0"));
        } else {
            String currentPlayer = id.toString();
            // check last draw user
            String drawKey = "cards:" + roomCode + ":draw";
            String lastDrawUser = stringRedisTemplate.opsForValue().get(drawKey);
            if (!currentPlayer.equals(lastDrawUser)) {
                cards = drawCard(roomCode, id, 1);
            }
        }
        step(roomCode, 1);
        return cards;
    }

    @Override
    public List<CardEntity> nextPlay(String roomCode) {
        List<CardEntity> cards = EMPTY;
        String penaltyCardsKey = "penaltyCards:" + roomCode;
        String numberStr = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
        if (numberStr == null) {
            numberStr = "0";
        }
        int number = Integer.parseInt(numberStr);
        if (number > 0) {
            cards = drawCard(roomCode, StpUtil.getLoginIdAsLong(), number);
            stringRedisTemplate.opsForValue().set(penaltyCardsKey, "0");
            nonBlockingService.execute(() -> gameRoomService.message(roomCode, "penalty_cards", "*", "0"));
        } else {
            String currentPlayer = StpUtil.getLoginIdAsString();
            // check last draw user
            String drawKey = "cards:" + roomCode + ":draw";
            String lastDrawUser = stringRedisTemplate.opsForValue().get(drawKey);
            if (!currentPlayer.equals(lastDrawUser)) {
                cards = drawCard(roomCode, StpUtil.getLoginIdAsLong(), 1);
            }
        }
        step(roomCode, 1);
        return cards;
    }

    private void step(String roomCode, int step) {
        String drawKey = "cards:" + roomCode + ":draw";
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
        randomBot.notify(roomCode, gamers.get(newIndex).getId());
        stringRedisTemplate.expire(drawKey, 1, TimeUnit.MICROSECONDS);
        extensionOfTime(key);
    }

    @Override
    public void discardCard(String roomCode, CardEntity card) {
        String key = "cards:" + roomCode + ":discard";
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        if (size > 5) {
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
