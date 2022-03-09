package cn.sbx0.microservices.uno.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sbx0
 * @since 2022/3/9
 */
@Slf4j
public class PlayEntity {
    private String name;
    private List<CardEntity> cards = new ArrayList<>();

    public PlayEntity() {
    }

    public PlayEntity(String name) {
        this.name = name;
    }

    public void drawCard(CardEntity card) {
        log.info("{} drawCard {} left {}", name, card.toString(), cards.size());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cards.add(card);
    }

    public CardEntity playCard(CardEntity topCard) {
        List<CardEntity> canPlay = new ArrayList<>();
        if (topCard == null) {
            canPlay = cards;
        } else {
            for (CardEntity card : cards) {
                if (topCard.getPoint().equals(card.getPoint())) {
                    canPlay.add(card);
                    continue;
                }
                if (topCard.getColor().equals(card.getColor())) {
                    canPlay.add(card);
                }
            }
        }
        if (canPlay.size() == 0) {
            return null;
        }
        int index = CardDeckEntity.randomChoose(canPlay.size());
        CardEntity card = canPlay.get(index);
        cards.remove(card);
        log.info("{} play {} left {}", name, card.toString(), cards.size());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return card;
    }
}
