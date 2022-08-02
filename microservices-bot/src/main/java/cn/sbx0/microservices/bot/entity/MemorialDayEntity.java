package cn.sbx0.microservices.bot.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/8/2
 */
@Data
public class MemorialDayEntity {
    private String day;
    private String sentence;
    private Boolean changeEveryYear;

    public MemorialDayEntity() {
    }

    public MemorialDayEntity(String day, String sentence, Boolean changeEveryYear) {
        this.day = day;
        this.sentence = sentence;
        this.changeEveryYear = changeEveryYear;
    }
}
