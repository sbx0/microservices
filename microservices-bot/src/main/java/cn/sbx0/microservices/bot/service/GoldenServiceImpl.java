package cn.sbx0.microservices.bot.service;

import cn.sbx0.microservices.bot.config.RetrofitConfig;
import cn.sbx0.microservices.bot.entity.MessageEntity;
import cn.sbx0.microservices.bot.http.entity.EastMoneyServiceResponse;
import cn.sbx0.microservices.bot.http.entity.GetFundNetDiagramResponse;
import cn.sbx0.microservices.bot.http.entity.RealTimeEastMoneyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sbx0
 * @since 2022/7/25
 */
@Slf4j
@Service
public class GoldenServiceImpl implements IGoldenService {
    @Resource
    private RetrofitConfig retrofitConfig;

    @Resource
    private IMessageService messageService;

    @Override
    public String handleData(List<GetFundNetDiagramResponse> data) {
        if (data == null) {
            return "";
        }
        int upDay = 0;
        int downDay = 0;
        double minPrice = Double.MAX_VALUE;
        for (GetFundNetDiagramResponse one : data) {
            double price = (int) ((Double.parseDouble(one.getDWJZ()) * 285.0) * 100) / 100.0;
            if (price < minPrice) {
                minPrice = price;
            }
            String jzzzl = one.getJZZZL();
            double zdf = Double.parseDouble(jzzzl);
            if (zdf > 0) {
                upDay++;
                downDay = 0;
            } else {
                downDay--;
                upDay = 0;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("##### 近期最低 ").append(minPrice).append("\n\n");
        if (upDay != 0) {
            stringBuilder.append("##### 已连续涨 ").append(upDay).append(" 天");
            return stringBuilder.toString();
        }
        if (downDay != 0) {
            stringBuilder.append("##### 已连续跌 ").append(-downDay).append(" 天");
            return stringBuilder.toString();
        }
        return "";
    }

    @Override
    public String handleData(RealTimeEastMoneyResponse data) {
        StringBuilder stringBuffer = new StringBuilder();
        String dwjz = data.getDwjz();
        String gsz = data.getGsz();
        double oldPrice = (int) ((Double.parseDouble(dwjz) * 285.0) * 100) / 100.0;
        double price = (int) ((Double.parseDouble(gsz) * 285.0) * 100) / 100.0;
        stringBuffer.append("#### ").append(data.getName()).append(" ").append("[").append(data.getFundcode()).append("]").append("\n\n")
                .append("##### 实时估算 ").append(price).append(" ").append("[").append(data.getGszzl()).append("]").append("\n\n")
                .append("##### 上交金价 ").append(oldPrice).append(" ").append("\n\n")
        ;
        return stringBuffer.toString();
    }

    @Override
    public void readData() {
        String code = "002963";
        Call<EastMoneyServiceResponse<List<GetFundNetDiagramResponse>>> call = retrofitConfig.eastMoneyService.getFundNetDiagram(code, "y", "Wap", "Wap", "EFund", "2.0.0", ThreadLocalRandom.current().nextInt(900000000, 999999999) + "");
        Call<RealTimeEastMoneyResponse> detailCall = retrofitConfig.realTimeEastMoneyService.getDetail(ThreadLocalRandom.current().nextInt(900000000, 999999999) + "");
        String recent = "";
        try {
            Response<EastMoneyServiceResponse<List<GetFundNetDiagramResponse>>> execute = call.execute();
            if (execute.isSuccessful()) {
                EastMoneyServiceResponse<List<GetFundNetDiagramResponse>> body = execute.body();
                if (body != null) {
                    recent = handleData(body.getDatas());
                }
            }
            Response<RealTimeEastMoneyResponse> realTimeResponse = detailCall.execute();
            if (realTimeResponse.isSuccessful()) {
                RealTimeEastMoneyResponse body = realTimeResponse.body();
                if (body != null) {
                    String data = handleData(body);
                    MessageEntity msg = new MessageEntity();
                    msg.setTitle("把握机遇");
                    msg.setText(data + recent);
                    msg.setButtonText("查看详情");
                    msg.setButtonUrl("http://fund.eastmoney.com/002963.html");
                    messageService.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
