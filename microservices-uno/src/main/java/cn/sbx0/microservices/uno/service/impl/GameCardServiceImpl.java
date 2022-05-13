package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.constant.CardPoint;
import cn.sbx0.microservices.uno.constant.GameRedisKey;
import cn.sbx0.microservices.uno.constant.MessageChannel;
import cn.sbx0.microservices.uno.entity.CardDeckEntity;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.logic.BasicGameRule;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IMessageService;
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
    @Resource
    private BasicGameRule gameRule;
    @Resource
    private IMessageService messageService;
    public static final List<CardEntity> EMPTY = new ArrayList<>(0);

    @Override
    public boolean playCard(String roomCode, String uuid, String color, Long userId) {
        if (!gameRule.canIPlayNow(roomCode, userId)) {
            return false;
        }

        List<CardEntity> cards = getCardsByUserId(roomCode, userId);
        CardEntity currentCard = cards.stream()
                .filter(c -> uuid.equals(c.getUuid()))
                .findAny()
                .orElse(null);

        if (currentCard == null) {
            return false;
        }

        String discardKey = GameRedisKey.ROOM_DISCARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        CardEntity previousCard = redisTemplate.opsForList().index(discardKey, 0);
        boolean canPlay = gameRule.judgeIsCanPlay(previousCard, currentCard, userId);
        if (canPlay) {
            canPlay = gameRule.judgePenaltyCards(previousCard, currentCard, roomCode);
        }
        if (canPlay) {
            cards.remove(currentCard);
            currentCard.setColor(color);
            gameRule.discardCard(roomCode, currentCard);
            gameRule.functionCard(roomCode, currentCard);
            nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.NUMBER_OF_CARDS, "*", userId + "=" + cards.size()));
        } else {
            return false;
        }

        String userCardsKey = GameRedisKey.USER_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode)
                .replaceAll(GameRedisKey.USER_ID, userId.toString());
        redisTemplate.expire(userCardsKey, Duration.ZERO);
        if (!CollectionUtils.isEmpty(cards)) {
            redisTemplate.opsForList().leftPushAll(userCardsKey, cards);
        }
        return true;
    }

    @Override
    public List<CardEntity> getCardsByUserId(String roomCode, Long id) {
        String userCardsKey = GameRedisKey.USER_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode)
                .replaceAll(GameRedisKey.USER_ID, id.toString());
        Long size = redisTemplate.opsForList().size(userCardsKey);
        if (size == null) {
            size = 0L;
        }
        return redisTemplate.opsForList().range(userCardsKey, 0, size);
    }

    @Override
    public void initCardDeck(String roomCode) {
        String roomCards = GameRedisKey.ROOM_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        List<CardEntity> cards = CardDeckEntity.CARDS;
        Collections.shuffle(cards);
        redisTemplate.opsForList().leftPushAll(roomCards, cards);
    }

    @Override
    public List<CardEntity> drawCard(String roomCode) {
        String roomCardsKey = GameRedisKey.ROOM_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String userCardsKey = GameRedisKey.USER_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode)
                .replaceAll(GameRedisKey.USER_ID, StpUtil.getLoginIdAsString());
        List<CardEntity> cards = new ArrayList<>();
        List<CardEntity> pops = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            // get card from room cards
            pops.add(redisTemplate.opsForList().rightPop(roomCardsKey));
        }
        if (!CollectionUtils.isEmpty(pops)) {
            cards.addAll(pops);
        }
        // give cards to user
        redisTemplate.opsForList().leftPushAll(userCardsKey, cards);
        return cards;
    }

    @Override
    public List<CardEntity> drawCard(String roomCode, int number) {
        String penaltyKey = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String penaltyCards = stringRedisTemplate.opsForValue().get(penaltyKey);
        int size = number;
        if (StringUtils.hasText(penaltyCards)) {
            int penalty = Integer.parseInt(penaltyCards);
            if (penalty > size) {
                size = penalty;
            } else {
                size += penalty;
            }
        }
        stringRedisTemplate.opsForValue().set(penaltyKey, "0");
        nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.PENALTY_CARDS, "*", "0"));
        return drawCard(roomCode, StpUtil.getLoginIdAsLong(), size);
    }

    @Override
    public List<CardEntity> drawCard(String roomCode, Long userId, int number) {
        String drawKey = GameRedisKey.ROOM_DRAW.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String key = GameRedisKey.ROOM_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String userCardsKey = GameRedisKey.USER_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode)
                .replaceAll(GameRedisKey.USER_ID, userId.toString());
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
        redisTemplate.opsForList().leftPushAll(userCardsKey, cards);
        stringRedisTemplate.opsForValue().set(drawKey, userId.toString());
        Long size = redisTemplate.opsForList().size(userCardsKey);
        nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.NUMBER_OF_CARDS, "*", userId + "=" + size));
        return cards;
    }

    @Override
    public List<CardEntity> myCardList(String roomCode) {
        String userCardsKey = GameRedisKey.USER_CARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode)
                .replaceAll(GameRedisKey.USER_ID, StpUtil.getLoginIdAsString());
        Long size = redisTemplate.opsForList().size(userCardsKey);
        if (size == null) {
            size = 0L;
        }
        return redisTemplate.opsForList().range(userCardsKey, 0, size);
    }

    private int getPenaltyCardsNumber(String roomCode) {
        String penaltyCardsKey = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        String numberStr = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
        if (numberStr == null) {
            numberStr = "0";
        }
        return Integer.parseInt(numberStr);
    }

    @Override
    public List<CardEntity> nextPlay(String roomCode, Long id) {
        String penaltyCardsKey = GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        List<CardEntity> cards = EMPTY;
        int number = getPenaltyCardsNumber(roomCode);
        if (number > 0) {
            cards = drawCard(roomCode, id, number);
            stringRedisTemplate.opsForValue().set(penaltyCardsKey, "0");
            nonBlockingService.execute(() -> messageService.send(roomCode, MessageChannel.PENALTY_CARDS, "*", "0"));
        } else {
            String currentPlayer = id.toString();
            // check last draw user
            String drawKey = GameRedisKey.ROOM_DRAW.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
            String lastDrawUser = stringRedisTemplate.opsForValue().get(drawKey);
            if (!currentPlayer.equals(lastDrawUser)) {
                cards = drawCard(roomCode, id, 1);
            }
        }
        gameRule.step(roomCode, 1);
        return cards;
    }

    @Override
    public List<CardEntity> discardCardList(String roomCode) {
        String key = GameRedisKey.ROOM_DISCARDS.replaceAll(GameRedisKey.ROOM_CODE, roomCode);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        return redisTemplate.opsForList().range(key, 0, size);
    }

    @Override
    public void initGame(String roomCode) {
        // normal reverse
        stringRedisTemplate.opsForValue().set(GameRedisKey.ROOM_DIRECTION.replaceAll(GameRedisKey.ROOM_CODE, roomCode), CardPoint.NORMAL);
        stringRedisTemplate.opsForValue().set(GameRedisKey.CURRENT_GAMER.replaceAll(GameRedisKey.ROOM_CODE, roomCode), "0");
        stringRedisTemplate.opsForValue().set(GameRedisKey.ROOM_PENALTY.replaceAll(GameRedisKey.ROOM_CODE, roomCode), "0");
    }
}
