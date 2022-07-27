package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.config.RetrofitConfig;
import cn.sbx0.microservices.bot.entity.MessageEntity;
import cn.sbx0.microservices.bot.http.entity.GitHubReleasesResponse;
import cn.sbx0.microservices.bot.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author sbx0
 * @since 2022/7/26
 */
@Slf4j
@Service
public class GitHubBotServiceImpl implements IGitHubBotService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RetrofitConfig retrofitConfig;
    @Resource
    private IMessageService messageService;

    @Override
    public void readData(String user, String repositoryName) {
        Call<GitHubReleasesResponse> call = retrofitConfig.gitHubService.getDinkumChineseLatest(user, repositoryName);
        try {
            Response<GitHubReleasesResponse> execute = call.execute();
            if (execute.isSuccessful()) {
                GitHubReleasesResponse body = execute.body();
                log.info(JSONUtils.toJSONString(body));
                handleData(repositoryName, body);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String handleData(String repositoryName, GitHubReleasesResponse data) {
        if (data == null) {
            return null;
        }
        String nodeId = data.getNode_id();
        String last = stringRedisTemplate.opsForValue().get(repositoryName + "Latest");
        if (last == null || !last.equals(nodeId)) {
            stringRedisTemplate.opsForValue().set(repositoryName + "Latest", nodeId);
            log.info(repositoryName + " update");
            MessageEntity msg = new MessageEntity();
            msg.setTitle("程序更新");
            msg.setText("#### " + repositoryName + " " + data.getTag_name() + "\n\n" + data.getBody());
            msg.setButtonText("查看详情");
            msg.setButtonUrl(data.getHtml_url());
            messageService.sendMessage(msg);
        }
        return null;
    }
}
