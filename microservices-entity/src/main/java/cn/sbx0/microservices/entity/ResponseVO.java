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

    public static <T> ResponseVO<T> success() {
        return success(null);
    }

    public static <T> ResponseVO<T> success(T data) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(SUCCESS);
        response.setData(data);
        response.setMessage("success");
        return response;
    }

    public static <T> ResponseVO<T> failed() {
        return failed(null);
    }

    public static <T> ResponseVO<T> failed(T data) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(FAILED);
        response.setData(data);
        response.setMessage("failed");
        return response;
    }

    public static <T> ResponseVO<T> failed(T data, String message) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(FAILED);
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    public static <T> ResponseVO<T> judge(boolean result, T data) {
        return result ? success(data) : failed(data);
    }

    public ResponseVO() {
    }
}
