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

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now().toLocalDate().atStartOfDay();
        int year = now.getYear();
        System.out.println(year);
        LocalDateTime memorialDay = LocalDate.parse("2021-08-12", DATE_TIME_FORMATTER).atStartOfDay();
        int memorialYear = memorialDay.getYear();
        if (memorialYear != year) {

        }
    }

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
            if (cal < 0) {
                stringBuilder.append(day.getSentence()).append("已经 ").append(-cal).append(" 天！");
            } else if (cal > 0) {
                stringBuilder.append(day.getSentence()).append("还有 ").append(cal).append(" 天！");
            } else {
                stringBuilder.append(day.getSentence()).append("就是今天！");
            }
            stringBuilder.append("\n\n");
        }
        MessageEntity msg = new MessageEntity();
        msg.setTitle("时间都去哪了");
        msg.setText("#### " + stringBuilder);
        msg.setButtonText("查看详情");
        msg.setButtonUrl("https://m.rili.com.cn/wannianli/");
        log.info(JSONUtils.toJSONString(msg));
        messageService.sendMessage(msg);
    }
}
