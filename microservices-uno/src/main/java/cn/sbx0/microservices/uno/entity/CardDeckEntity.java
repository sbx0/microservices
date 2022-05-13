package cn.sbx0.microservices.uno.entity;

import cn.sbx0.microservices.uno.constant.CardColor;
import cn.sbx0.microservices.uno.constant.CardPoint;
import lombok.Data;

import java.util.*;

/**
 * @author sbx0
 * @since 2022/3/9
 */
@Data
public class CardDeckEntity {
    public static List<CardEntity> CARDS = new ArrayList<>();
    private static final int STANDARD = 2;
    private static Map<String, Integer> numbers = new HashMap<>();
    private static final int FUNCTION_STANDARD = 2;
    private static final int BLACK_STANDARD = 4;
    public static String[] COLORS = new String[]{CardColor.RED, CardColor.YELLOW, CardColor.GREEN, CardColor.BLUE};

    static {
        numbers.put(CardPoint.ZERO, 1);
        numbers.put(CardPoint.ONE, STANDARD);
        numbers.put(CardPoint.TWO, STANDARD);
        numbers.put(CardPoint.THREE, STANDARD);
        numbers.put(CardPoint.FOUR, STANDARD);
        numbers.put(CardPoint.FIVE, STANDARD);
        numbers.put(CardPoint.SIX, STANDARD);
        numbers.put(CardPoint.SEVEN, STANDARD);
        numbers.put(CardPoint.EIGHT, STANDARD);
        numbers.put(CardPoint.NINE, STANDARD);
        numbers.put(CardPoint.SKIP, FUNCTION_STANDARD);
        numbers.put(CardPoint.DRAW_TWO, FUNCTION_STANDARD);
        numbers.put(CardPoint.REVERSE, FUNCTION_STANDARD);

        for (Map.Entry<String, Integer> number : numbers.entrySet()) {
            for (int i = 0; i < 4 * number.getValue(); i++) {
                CARDS.add(new CardEntity(UUID.randomUUID().toString(), COLORS[i % 4], number.getKey(), 0L));
            }
        }
        for (int i = 0; i < BLACK_STANDARD; i++) {
            CARDS.add(new CardEntity(UUID.randomUUID().toString(), CardColor.BLACK, CardPoint.WILD, 0L));
            CARDS.add(new CardEntity(UUID.randomUUID().toString(), CardColor.BLACK, CardPoint.WILD_DRAW_FOUR, 0L));
        }
    }

    public static int randomChoose(int size) {
        return (int) ((Math.random() * size) % size);
    }

}
