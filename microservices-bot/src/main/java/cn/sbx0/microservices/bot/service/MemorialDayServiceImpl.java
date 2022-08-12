package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.entity.MemorialDayEntity;
import cn.sbx0.microservices.bot.entity.MessageEntity;
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
@Service
public class MemorialDayServiceImpl implements IMemorialDayService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Resource
    private IMessageService messageService;

    @Override
    public void handleData(List<MemorialDayEntity> days) {
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now().toLocalDate().atStartOfDay();
        for (MemorialDayEntity day : days) {
            LocalDateTime memorialDay = LocalDate.parse(day.getDay(), DATE_TIME_FORMATTER).atStartOfDay();
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
        messageService.sendMessage(msg);
    }
}
