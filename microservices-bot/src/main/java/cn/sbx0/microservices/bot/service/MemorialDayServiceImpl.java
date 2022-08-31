package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.entity.MemorialDayEntity;
import cn.sbx0.microservices.bot.entity.MessageEntity;
import cn.sbx0.microservices.bot.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author sbx0
 * @since 2022/8/2
 */
@Slf4j
@Service
public class MemorialDayServiceImpl implements IMemorialDayService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Resource
    private IMessageService messageService;

    @Override
    public void handleData(List<MemorialDayEntity> days) {
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now().toLocalDate().atStartOfDay();
        int year = now.getYear();
        for (MemorialDayEntity day : days) {
            String dayStr;
            Boolean changeEveryYear = day.getChangeEveryYear();
            if (changeEveryYear) {
                dayStr = year + day.getDay();
                LocalDateTime tempDay = LocalDate.parse(dayStr, DATE_TIME_FORMATTER).atStartOfDay();
                long cal = Duration.between(now, tempDay).toDays();
                if (cal < 0) {
                    dayStr = (year + 1) + day.getDay();
                }
            } else {
                dayStr = day.getDay();
            }
            LocalDateTime memorialDay = LocalDate.parse(dayStr, DATE_TIME_FORMATTER).atStartOfDay();
            long cal = Duration.between(now, memorialDay).toDays();
            day.setRemainDay(cal);
        }
        days = days.stream().sorted((o1, o2) -> {
            long a = Math.abs(o1.getRemainDay());
            long b = Math.abs(o2.getRemainDay());
            if (Objects.equals(a, b)) {
                return 0;
            } else {
                return a > b ? 1 : -1;
            }
        }).collect(Collectors.toList());
        for (MemorialDayEntity day : days) {
            if (day.getRemainDay() < 0) {
                stringBuilder.append(day.getSentence()).append(" 已经 ").append(-day.getRemainDay()).append(" 天！");
            } else if (day.getRemainDay() > 0) {
                stringBuilder.append(day.getSentence()).append(" 还有 ").append(day.getRemainDay()).append(" 天！");
            } else {
                stringBuilder.append(day.getSentence()).append(" 就是今天！");
            }
            stringBuilder.append("\n\n");
        }
        MessageEntity msg = new MessageEntity();
        msg.setTitle("时间都去哪了");
        msg.setText(stringBuilder.toString());
        msg.setButtonText("查看详情");
        msg.setButtonUrl("https://m.rili.com.cn/wannianli/");
        log.info(JSONUtils.toJSONString(msg));
        messageService.sendMessage(msg);
    }
}
