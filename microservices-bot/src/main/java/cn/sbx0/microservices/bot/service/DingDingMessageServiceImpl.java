package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.entity.MessageEntity;
import cn.sbx0.microservices.bot.http.entity.SendRobotMessageActionCardBody;
import cn.sbx0.microservices.bot.http.entity.SendRobotMessageBody;
import cn.sbx0.microservices.bot.utils.RetrofitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author sbx0
 * @since 2022/7/26
 */
@Slf4j
@Service
public class DingDingMessageServiceImpl implements IMessageService {
    @Resource
    private RetrofitConfig retrofitConfig;
    @Value("${dingtalk.bot.access-token}")
    private String accessToken;

    @Override
    public void sendMessage(MessageEntity data) {
        SendRobotMessageBody message = new SendRobotMessageBody();
        message.setMsgtype("actionCard");
        SendRobotMessageActionCardBody actionCard = new SendRobotMessageActionCardBody();
        actionCard.setTitle(data.getTitle());
        actionCard.setText(data.getText());
        actionCard.setHideAvatar("0");
        actionCard.setBtnOrientation("0");
        actionCard.setSingleTitle(data.getButtonText());
        actionCard.setSingleUrl(data.getButtonUrl());
        message.setActionCard(actionCard);
        Call<Void> sendCall = retrofitConfig.dingDingService.sendRobotMessage(accessToken, message);
        try {
            sendCall.execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
