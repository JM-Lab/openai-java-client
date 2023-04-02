package kr.jm.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.king.platform.net.http.HttpResponse;
import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.openai.dto.DefaultOpenAiCompletionsRequestInterface;
import kr.jm.openai.sse.OpenAiSseClient;
import kr.jm.utils.helper.JMJson;
import kr.jm.utils.helper.JMLog;
import kr.jm.utils.http.JMHttpRequester;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

public interface OpenAiCompletionsInterface<RQ extends DefaultOpenAiCompletionsRequestInterface, RS> {

    JMJson JmJson =
            new JMJson(new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));

    default CompletableFuture<RS> requestWithSse(RQ body, ResponseBodyConsumer<RS> openAiSsePartConsumer) {
        body.setStream(true);
        String response = JmJson.toJsonString(body);
        JMLog.debug(getLog(), "requestWithSse", body, response);
        return OpenAiSseClient.getInstance().consumeServerSentEvent(getOpenAiApiConf(),
                response, openAiSsePartConsumer).thenApply(HttpResponse::getBody);
    }

    default RS request(RQ body) {
        body.setStream(false);
        String response = request(JmJson.toJsonString(body));
        JMLog.debug(getLog(), "request", body, response);
        return JmJson.withJsonString(response, getResponseClass());
    }

    private TypeReference<RS> getTypeReference() {
        return new TypeReference<>() {};
    }

    private String request(String body) {
        return JMHttpRequester.getInstance()
                .postResponseAsString(getOpenAiApiConf().getHeaders(), getOpenAiApiConf().getOpenAIUrl(), body);
    }

    Class<RS> getResponseClass();

    OpenAiApiConf getOpenAiApiConf();

    Logger getLog();

}
