package cn.sbx0.microservices.bot.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author sbx0
 * @since 2022/7/26
 */
public final class JsonpConverterFactory extends Converter.Factory {
    private final ObjectMapper mapper;

    private JsonpConverterFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static JsonpConverterFactory create() {
        return create(new ObjectMapper());
    }

    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static JsonpConverterFactory create(ObjectMapper mapper) {
        if (mapper == null) throw new NullPointerException("mapper == null");
        return new JsonpConverterFactory(mapper);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectReader reader = mapper.readerFor(javaType);
        return new JsonpResponseBodyConverter<>(reader);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mapper.writerFor(javaType);
        return new JsonpRequestBodyConverter<>(writer);
    }
}
