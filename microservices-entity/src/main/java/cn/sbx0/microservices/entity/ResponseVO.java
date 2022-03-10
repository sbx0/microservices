package cn.sbx0.microservices.entity;

import lombok.Data;

/**
 * @author sbx0
 * @since 2022/3/10
 */
@Data
public class ResponseVO<T> {
    public static String SUCCESS = "0";
    public static String FAILED = "-1";
    private String code;
    private String message;
    private T data;

    public ResponseVO() {
    }

    public ResponseVO(String code, T data) {
        this.code = code;
        if (SUCCESS.equals(code)) {
            this.message = "success";
        } else {
            this.message = "failed";
        }
        this.data = data;
    }

}
