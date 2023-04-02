package kr.jm.openai.sse;


import com.king.platform.net.http.HttpClient;
import com.king.platform.net.http.HttpResponse;
import com.king.platform.net.http.ResponseBodyConsumer;
import com.king.platform.net.http.netty.NettyHttpClientBuilder;
import kr.jm.openai.OpenAiApiConf;

import java.util.concurrent.CompletableFuture;

import static com.king.platform.net.http.ConfKeys.IDLE_TIMEOUT_MILLIS;
import static kr.jm.openai.OpenAiApiConf.AUTHORIZATION;
import static kr.jm.openai.OpenAiApiConf.CONTENT_TYPE;

public class OpenAiSseClient {
    private final HttpClient httpClient;

    public static OpenAiSseClient getInstance() {
        return OpenAiSseClient.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final OpenAiSseClient INSTANCE = new OpenAiSseClient();
    }

    private OpenAiSseClient() {
        this.httpClient = new NettyHttpClientBuilder().setOption(IDLE_TIMEOUT_MILLIS, 60_000).createHttpClient();
        this.httpClient.start();
    }

    public <T> CompletableFuture<HttpResponse<T>> consumeServerSentEvent(OpenAiApiConf openAiApiConf, String body,
            ResponseBodyConsumer<T> responseBodyConsumer) {
        return httpClient.createPost(openAiApiConf.getOpenAIUrl())
                .contentType(openAiApiConf.getHeaders().get(CONTENT_TYPE))
                .content(body.getBytes()).addHeader(AUTHORIZATION, openAiApiConf.getHeaders().get(AUTHORIZATION))
                .build(() -> responseBodyConsumer).execute();
    }
}
