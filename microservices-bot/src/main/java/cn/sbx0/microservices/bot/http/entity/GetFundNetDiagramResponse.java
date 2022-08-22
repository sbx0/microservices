package cn.sbx0.microservices.bot.http.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sbx0
 * @since 2022/7/25
 */
@Data
public class GetFundNetDiagramResponse {
    @JsonProperty(value = "FSRQ")
    private String FSRQ;
    @JsonProperty(value = "DWJZ")
    private String DWJZ;
    @JsonProperty(value = "JZZZL")
    private String JZZZL;
    @JsonProperty(value = "LJJZ")
    private String LJJZ;
    @JsonProperty(value = "NAVTYPE")
    private String NAVTYPE;
    @JsonProperty(value = "RATE")
    private String RATE;
    @JsonProperty(value = "FHFCZ")
    private String FHFCZ;
    @JsonProperty(value = "FHFCBZ")
    private String FHFCBZ;
    @JsonProperty(value = "Remarks")
    private String Remarks;
}
