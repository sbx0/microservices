package cn.sbx0.microservices.uno.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sbx0
 * @since 2022/3/9
 */
public class CardDeckEntity {
    private List<CardEntity> cards = new ArrayList<>();
    private String[] colors = new String[]{"red", "yellow", "green", "blue"};
    private Map<String, Integer> numbers = new HashMap<>();

    public CardDeckEntity() {
        numbers.put("0", 1);
        numbers.put("1", 2);
        numbers.put("2", 2);
        numbers.put("3", 2);
        numbers.put("4", 2);
        numbers.put("5", 2);
        numbers.put("6", 2);
        numbers.put("7", 2);
        numbers.put("8", 2);
        numbers.put("9", 2);
        numbers.put("skip", 2);
        numbers.put("draw two", 2);
        numbers.put("reverse", 2);

        for (Map.Entry<String, Integer> number : numbers.entrySet()) {
            for (int i = 0; i < 4 * number.getValue(); i++) {
                cards.add(new CardEntity(colors[i % 4], number.getKey()));
            }
        }
        for (int i = 0; i < 4; i++) {
            cards.add(new CardEntity("", "wild"));
            cards.add(new CardEntity("", "wild draw four"));
        }
    }

    public static int randomChoose(int size) {
        return (int) ((Math.random() * size) % size);
    }

    public static void main(String[] args) {
        CardDeckEntity cardDesk = new CardDeckEntity();
        List<CardEntity> cards = cardDesk.cards.subList(0, cardDesk.cards.size());
        PlayEntity a = new PlayEntity("a");
        PlayEntity b = new PlayEntity("b");
        PlayEntity c = new PlayEntity("c");
        PlayEntity d = new PlayEntity("d");
        for (int i = 0; i < 7; i++) {

            int index = randomChoose(cards.size());
            a.drawCard(cards.get(index));
            cards.remove(index);

            index = randomChoose(cards.size());
            b.drawCard(cards.get(index));
            cards.remove(index);

            index = randomChoose(cards.size());
            c.drawCard(cards.get(index));
            cards.remove(index);

            index = randomChoose(cards.size());
            d.drawCard(cards.get(index));
            cards.remove(index);
        }

        CardEntity topCar = a.playCard(null);

        CardEntity choose = b.playCard(topCar);
        if (choose == null) {
            b.drawCard(cards.get(randomChoose(cards.size())));
            choose = b.playCard(topCar);
            if (choose != null) {
                topCar = choose;
            }
        } else {
            topCar = choose;
        }

        for (int i = 0; i < 100; i++) {
            topCar = play(cards, c, topCar);
            topCar = play(cards, d, topCar);
            topCar = play(cards, a, topCar);
            topCar = play(cards, b, topCar);
        }

    }

    private static CardEntity play(List<CardEntity> cards, PlayEntity c, CardEntity topCar) {
        CardEntity choose;
        choose = c.playCard(topCar);
        if (choose == null) {
            c.drawCard(cards.get(randomChoose(cards.size())));
            choose = c.playCard(topCar);
            if (choose != null) {
                topCar = choose;
            }
        } else {
            topCar = choose;
        }
        return topCar;
    }


}