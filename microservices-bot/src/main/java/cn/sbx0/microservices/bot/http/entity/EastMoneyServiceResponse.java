package cn.sbx0.microservices.bot.http.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sbx0
 * @since 2022/7/25
 */
@Data
public class EastMoneyServiceResponse<T> {
    @JsonProperty(value = "Datas")
    private T Datas;
    @JsonProperty(value = "ErrCode")
    private String ErrCode;
    @JsonProperty(value = "ErrMsg")
    private String ErrMsg;
    @JsonProperty(value = "TotalCount")
    private String TotalCount;
    @JsonProperty(value = "Expansion")
    private String Expansion;
}
