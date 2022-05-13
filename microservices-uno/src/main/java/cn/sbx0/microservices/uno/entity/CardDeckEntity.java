package cn.sbx0.microservices.uno.entity;

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
    public static String[] COLORS = new String[]{CarColor.RED, CarColor.YELLOW, CarColor.GREEN, CarColor.BLUE};

    static {
        numbers.put(CarPoint.ZERO, 1);
        numbers.put(CarPoint.ONE, STANDARD);
        numbers.put(CarPoint.TWO, STANDARD);
        numbers.put(CarPoint.THREE, STANDARD);
        numbers.put(CarPoint.FOUR, STANDARD);
        numbers.put(CarPoint.FIVE, STANDARD);
        numbers.put(CarPoint.SIX, STANDARD);
        numbers.put(CarPoint.SEVEN, STANDARD);
        numbers.put(CarPoint.EIGHT, STANDARD);
        numbers.put(CarPoint.NINE, STANDARD);
        numbers.put(CarPoint.SKIP, FUNCTION_STANDARD);
        numbers.put(CarPoint.DRAW_TWO, FUNCTION_STANDARD);
        numbers.put(CarPoint.REVERSE, FUNCTION_STANDARD);

        for (Map.Entry<String, Integer> number : numbers.entrySet()) {
            for (int i = 0; i < 4 * number.getValue(); i++) {
                CARDS.add(new CardEntity(UUID.randomUUID().toString(), COLORS[i % 4], number.getKey(), 0L));
            }
        }
        for (int i = 0; i < BLACK_STANDARD; i++) {
            CARDS.add(new CardEntity(UUID.randomUUID().toString(), CarColor.BLACK, CarPoint.WILD, 0L));
            CARDS.add(new CardEntity(UUID.randomUUID().toString(), CarColor.BLACK, CarPoint.WILD_DRAW_FOUR, 0L));
        }
    }

    public static int randomChoose(int size) {
        return (int) ((Math.random() * size) % size);
    }

}
