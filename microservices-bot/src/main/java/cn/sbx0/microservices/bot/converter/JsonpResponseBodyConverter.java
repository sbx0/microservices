package cn.sbx0.microservices.bot.converter;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;

@Slf4j
final class JsonpResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final ObjectReader adapter;

    JsonpResponseBodyConverter(ObjectReader adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String string = value.string();
        string = string.replace("jsonpgz(", "");
        string = string.replace(");", "");
        try {
            return adapter.readValue(string);
        } finally {
            value.close();
        }
    }
}
