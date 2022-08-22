package cn.sbx0.microservices.bot.http.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sbx0
 * @since 2022/7/25
 */
@Data
public class RealTimeEastMoneyResponse {
    @JsonProperty(value = "fundcode")
    private String fundcode;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "jzrq")
    private String jzrq;
    @JsonProperty(value = "dwjz")
    private String dwjz;
    @JsonProperty(value = "gsz")
    private String gsz;
    @JsonProperty(value = "gszzl")
    private String gszzl;
    @JsonProperty(value = "gztime")
    private String gztime;
}
