package cn.sbx0.microservices.uno.bot;

import cn.sbx0.microservices.entity.AccountVO;
import cn.sbx0.microservices.uno.entity.CardDeckEntity;
import cn.sbx0.microservices.uno.entity.CardEntity;
import cn.sbx0.microservices.uno.feign.AccountService;
import cn.sbx0.microservices.uno.service.IGameCardService;
import cn.sbx0.microservices.uno.service.IGameRoomUserService;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Random Bot
 * <p>
 * this bot random choose card and color which can play.
 *
 * @author sbx0
 * @since 2022/4/27
 */
@Data
@Component
public class RandomBot {
    private Long id;
    private String name = "RandomBot";
    @Resource
    private AccountService accountService;
    @Resource
    private IGameRoomUserService gameRoomUserService;
    @Resource
    private IGameCardService gameCardService;
    @Resource
    private RedisTemplate<String, CardEntity> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void playCard(String roomCode) {
        initId();
        String discardKey = "cards:" + roomCode + ":discard";
        CardEntity top = redisTemplate.opsForList().index(discardKey, 0);
        List<CardEntity> cards = gameCardService.botCardList(roomCode, id);

        // todo bot have unlimited cards, will remove after debug
        if (CollectionUtils.isEmpty(cards)) {
            drawCard(roomCode, 7);
            cards = gameCardService.botCardList(roomCode, id);
        }

        List<CardEntity> canPlayCards;

        // first round or top card belong bot.
        if (top == null || id.equals(top.getUserId())) {
            // free play
            canPlayCards = cards;
        } else {
            // match play
            canPlayCards = new ArrayList<>();
            // see penalty cards
            String penaltyCardsKey = "penaltyCards:" + roomCode;
            String penaltyCards = stringRedisTemplate.opsForValue().get(penaltyCardsKey);
            int size = 0;
            if (StringUtils.hasText(penaltyCards)) {
                size = Integer.parseInt(penaltyCards);
            }
            if (size > 0) {
                // see can plus ?
                for (CardEntity card : cards) {
                    if ("wild draw four".equals(card.getPoint())) {
                        canPlayCards.add(card);
                    }
                    if ("draw two".equals(card.getPoint()) && "draw two".equals(top.getPoint())) {
                        canPlayCards.add(card);
                    }
                }
            } else {
                // filter which can play
                for (CardEntity card : cards) {
                    boolean canPlay = card.getPoint().contains("wild");
                    if (card.getColor().equals(top.getColor())) {
                        canPlay = true;
                    }
                    if (card.getPoint().equals(top.getPoint())) {
                        canPlay = true;
                    }
                    if (canPlay) {
                        canPlayCards.add(card);
                    }
                }
            }
        }

        if (canPlayCards == null || canPlayCards.size() < 1) {
            // no card can play
            gameCardService.botNextPlay(roomCode, id);
            return;
        }

        int index = CardDeckEntity.randomChoose(canPlayCards.size());
        CardEntity card = canPlayCards.get(index);

        // random choose color
        String color;
        if (card.getPoint().contains("wild")) {
            int colorIndex = CardDeckEntity.randomChoose(CardDeckEntity.COLORS.length);
            color = CardDeckEntity.COLORS[colorIndex];
        } else {
            color = card.getColor();
        }

        // new thread to play card make sure not stuck main thread
        new Thread(() -> {
            // add delay to make user feel better, this is why new thread to run
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean result = gameCardService.botPlayCard(roomCode, card.getUuid(), color, id);
            if (!result) {
                throw new RuntimeException("bot choose card " + card.getUuid() + " which can't play");
            }
        }).start();
    }

    public void initId() {
        if (this.id == null) {
            AccountVO bot = accountService.findByUserName(name);
            if (bot == null) {
                throw new RuntimeException("RandomBot account not create");
            }
            this.id = bot.getId();
        }
    }

    public void notify(String roomCode, Long id) {
        initId();
        if (this.id.equals(id)) {
            playCard(roomCode);
        }
    }

    public void drawCard(String roomCode, int number) {
        initId();
        gameCardService.drawCard(roomCode, id, number);
    }

    public boolean joinRoom(String roomCode) {
        return gameRoomUserService.botJoinGameRoom(roomCode, name);
    }

    public boolean quitRoom(String roomCode) {
        return gameRoomUserService.botQuitGameRoom(roomCode, name);
    }

}
