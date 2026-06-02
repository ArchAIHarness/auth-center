package top.cloudlab.auth.infrastructure;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson.JSON;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class TboxApiTest {

    @Test
    void test1() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        @SuppressWarnings("deprecation")
        RequestBody body = RequestBody.create(mediaType,
                "{\n  \"loginType\": \"YUNYAN\",\n  \"authCode\": \"6vc6vvac\",\n  \"agree\": \"Y\"\n}");
        Request request = new Request.Builder()
                .url("https://twebgwnet.tbox.cn/tdoraemonconsolepre/api/auth/login")
                .method("POST", body)
                .addHeader("x-webgw-appId", "180020010001263215")
                .addHeader("x-webgw-version", "2.0")
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "twebgwnet.tbox.cn")
                .addHeader("Connection", "keep-alive")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(JSON.parse(response.body().string()));
    }

}
