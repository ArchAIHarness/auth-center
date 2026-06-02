package top.cloudlab.auth.interfaces.config.wrapper;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.common.dto.R;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseBodyWrapper implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    private static final String[] EXCLUDE_URLS = {
            "/v3/api-docs",
            "/swagger-resources",
            "/swagger-ui",
            "/webjars",
            "/doc.html"
    };

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        String requestPath = request.getURI().getPath();
        for (String excludeUrl : EXCLUDE_URLS) {
            if (requestPath.contains(excludeUrl)) {
                return body;
            }
        }

        if (body instanceof R) {
            return body;
        }

        if (body instanceof String) {
            R<?> r = R.builder()
                    .code(0)
                    .data(body)
                    .build();
            try {
                return objectMapper.writeValueAsString(r);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("响应序列化失败", e);
                return r;
            }
        }

        return R.builder()
                .code(0)
                .data(body)
                .build();
    }
}
