package cn.sbx0.microservices.uno.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author sbx0
 * @since 2022/3/9
 */
@Setter
@Getter
public class CardDeckEntity {
    public static List<CardEntity> CARDS = new ArrayList<>();
    private static final int STANDARD = 2;
    private static Map<String, Integer> numbers = new HashMap<>();
    private static final int FUNCTION_STANDARD = 2;
    private static final int BLACK_STANDARD = 4;
    public static String[] COLORS = new String[]{"red", "yellow", "green", "blue"};

    static {
        numbers.put("0", 1);
        numbers.put("1", STANDARD);
        numbers.put("2", STANDARD);
        numbers.put("3", STANDARD);
        numbers.put("4", STANDARD);
        numbers.put("5", STANDARD);
        numbers.put("6", STANDARD);
        numbers.put("7", STANDARD);
        numbers.put("8", STANDARD);
        numbers.put("9", STANDARD);
        numbers.put("skip", FUNCTION_STANDARD);
        numbers.put("draw two", FUNCTION_STANDARD);
        numbers.put("reverse", FUNCTION_STANDARD);

        for (Map.Entry<String, Integer> number : numbers.entrySet()) {
            for (int i = 0; i < 4 * number.getValue(); i++) {
                CARDS.add(new CardEntity(UUID.randomUUID().toString(), COLORS[i % 4], number.getKey(), 0L));
            }
        }
        for (int i = 0; i < BLACK_STANDARD; i++) {
            CARDS.add(new CardEntity(UUID.randomUUID().toString(), "black", "wild", 0L));
            CARDS.add(new CardEntity(UUID.randomUUID().toString(), "black", "wild draw four", 0L));
        }
    }

    public static int randomChoose(int size) {
        return (int) ((Math.random() * size) % size);
    }

    public static void main(String[] args) {
        CardDeckEntity cardDesk = new CardDeckEntity();
        List<CardEntity> cards = cardDesk.CARDS.subList(0, cardDesk.CARDS.size());
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
