package cn.sbx0.microservices.bot.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author wangh
 * @since 2022/6/23
 */
@Slf4j
public class RetrofitUtils {
    public static String generateSignature(Map<String, String> data) {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder stringBuilder = new StringBuilder();
        for (String k : keyArray) {
            if ("signature".equals(k)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) {
                stringBuilder.append(k).append("=").append(data.get(k).trim()).append("&");
            }
        }
        return stringBuilder.toString();
    }

    public static String generateBusinessCenterSignature(Map<String, String> data) {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder stringBuilder = new StringBuilder("{");
        for (int i = 0; i < keyArray.length; i++) {
            String k = keyArray[i];
            if (data.get(k).trim().length() > 0) {
                stringBuilder.append("\"").append(k).append("\"")
                        .append(":")
                        .append("\"").append(data.get(k).trim()).append("\"");
            }
            if (i != keyArray.length - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
