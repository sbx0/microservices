package cn.sbx0.microservices.uno.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.sbx0.microservices.uno.entity.CardDeckEntity;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.service.IGameCardService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangh
 * @since 2022-03-22
 */
@Service
public class GameCardServiceImpl implements IGameCardService {
    @Resource
    private RedisTemplate<String, CardEntity> redisTemplate;

    @Override
    public void initCardDeck(String roomCode) {
        List<CardEntity> cards = CardDeckEntity.CARDS;
        Collections.shuffle(cards);
        for (CardEntity card : cards) {
            redisTemplate.opsForList().rightPush("cards_" + roomCode, card);
        }
    }

    @Override
    public List<CardEntity> drawCardOnBeginning(String roomCode) {
        List<CardEntity> cards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            cards.add(redisTemplate.opsForList().leftPop("cards_" + roomCode));
        }
        redisTemplate.opsForList().rightPushAll("cards_" + roomCode + "_" + StpUtil.getLoginIdAsString(), cards);
        return cards;
    }

    @Override
    public List<CardEntity> myCard(String roomCode) {
        String key = "cards_" + roomCode + "_" + StpUtil.getLoginIdAsString();
        Long size = redisTemplate.opsForList().size(key);
        if (size == null) {
            size = 0L;
        }
        return redisTemplate.opsForList().range(key, 0, size);
    }
}
