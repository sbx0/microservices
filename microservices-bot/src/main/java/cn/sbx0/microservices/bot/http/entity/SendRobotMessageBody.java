package cn.sbx0.microservices.bot.http.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/7/25
 */
@Data
public class SendRobotMessageBody {
    private String msgtype;
    private SendRobotMessageActionCardBody actionCard;
}
