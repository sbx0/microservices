package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.CardDeckEntity;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameCardService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wangh
 * @since 2022-03-22
 */
@Service
public class GameCardServiceImpl implements IGameCardService {
    @Resource
    private RedisTemplate<String, CardEntity> redisTemplate;

    @Override
    public void initCardDeck(String roomCode) {
        String key = "cards_" + roomCode;
        List<CardEntity> cards = CardDeckEntity.CARDS;
        Collections.shuffle(cards);
        redisTemplate.opsForList().rightPushAll(key, cards);
        extensionOfTime(key);
    }

    @Override
    public List<CardEntity> drawCard(String roomCode) {
        String key = "cards_" + roomCode;
        String keyPlusUserID = key + "_" + StpUtil.getLoginIdAsString();
        List<CardEntity> cards = new ArrayList<>();
        List<CardEntity> pops = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            pops.add(redisTemplate.opsForList().leftPop(key));
        }
        if (!CollectionUtils.isEmpty(pops)) {
            cards.addAll(pops);
        }
        redisTemplate.opsForList().rightPushAll(keyPlusUserID, cards);
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
        String key = "cards_" + roomCode;
        String keyPlusUserID = key + "_" + userId;
        if (number < 1) {
            number = 1;
        }
        List<CardEntity> cards = new ArrayList<>();
        List<CardEntity> pops = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            pops.add(redisTemplate.opsForList().leftPop(key));
        }
        if (!CollectionUtils.isEmpty(pops)) {
            cards.addAll(pops);
        }
        redisTemplate.opsForList().rightPushAll(keyPlusUserID, cards);
        extensionOfTime(key);
        extensionOfTime(keyPlusUserID);
        return cards;
    }

    @Override
    public List<CardEntity> myCard(String roomCode) {
        String key = "cards_" + roomCode + "_" + StpUtil.getLoginIdAsString();
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        extensionOfTime(key);
        return redisTemplate.opsForList().range(key, 0, size);
    }

    @Override
    public Boolean playCard(String roomCode, String uuid) {
        String key = "cards_" + roomCode + "_" + StpUtil.getLoginIdAsString();
        List<CardEntity> cards = myCard(roomCode);
        for (CardEntity card : cards) {
            if (uuid.equals(card.getUuid())) {
                cards.remove(card);
                break;
            }
        }
        redisTemplate.expire(key, Duration.ZERO);
        redisTemplate.opsForList().rightPushAll(key, cards);
        return true;
    }

    private void extensionOfTime(String key) {
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }
}
