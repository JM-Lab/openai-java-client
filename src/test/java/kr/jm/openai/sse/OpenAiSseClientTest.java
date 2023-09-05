package kr.jm.openai.sse;

import com.king.platform.net.http.HttpResponse;
import kr.jm.openai.OpenAiApiConf;
import kr.jm.openai.dto.Message;
import kr.jm.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.openai.dto.OpenAiChatCompletionsResponse;
import kr.jm.openai.dto.sse.OpenAiSseData;
import kr.jm.utils.JMOptional;
import kr.jm.utils.helper.JMJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static kr.jm.openai.dto.Role.user;
import static org.junit.jupiter.api.Assertions.assertEquals;

// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
class OpenAiSseClientTest {

    private OpenAiApiConf openAiApiConf;


    @BeforeEach
    void setUp() {
        this.openAiApiConf = new OpenAiApiConf("https://api.openai.com/v1/chat/completions",
                JMOptional.getOptional(System.getenv("OPENAI_API_KEY"))
                        .orElseThrow(
                                () -> new RuntimeException("The OPENAI_API_KEY environment variable is not set.")));
    }

    @Disabled
    @Test
    void consumeServerSentEventPart() throws ExecutionException, InterruptedException {
        OpenAiSseClient openAiSseClient = OpenAiSseClient.getInstance();
        StringBuilder resultBuilder = new StringBuilder();
        OpenAiSseChatCompletionsPartConsumer openAiSsePartConsumer = new OpenAiSseChatCompletionsPartConsumer(part -> {
            System.out.print(part);
            resultBuilder.append(part);
        });
        CompletableFuture<HttpResponse<OpenAiChatCompletionsResponse>> httpResponseCompletableFuture =
                openAiSseClient.consumeServerSentEvent(openAiApiConf, JMJson.getInstance().toJsonString(
                                new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setStream(true)
                                        .setMessages(List.of(new Message(user, "인공지능 공부하는 법 간단히 알려줘")))),
                        openAiSsePartConsumer);
        OpenAiChatCompletionsResponse responseBody = httpResponseCompletableFuture.get().getBody();
        System.out.println(responseBody);
        assertEquals(responseBody.getChoices().get(0).getMessage().getContent(), resultBuilder.toString());
    }

    @Disabled
    @Test
    void consumeServerSentEventData() throws ExecutionException, InterruptedException {
        OpenAiSseClient openAiSseClient = OpenAiSseClient.getInstance();
        StringBuilder resultBuilder = new StringBuilder();
        OpenAiSseDataConsumer openAiSseDataConsumer = new OpenAiSseDataConsumer(part -> {
            System.out.print(part);
            resultBuilder.append(part);
        });
        CompletableFuture<HttpResponse<List<OpenAiSseData>>> httpResponseCompletableFuture =
                openAiSseClient.consumeServerSentEvent(openAiApiConf, JMJson.getInstance().toJsonString(
                                new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setStream(true)
                                        .setMessages(List.of(new Message(user, "인공지능 공부하는 법 간단히 알려줘")))),
                        openAiSseDataConsumer);
        List<OpenAiSseData> responseBody = httpResponseCompletableFuture.get().getBody();
        System.out.println(responseBody);
        assertEquals(responseBody.stream().map(Objects::toString).collect(Collectors.joining()).toString(),
                resultBuilder.toString());
    }

    @Disabled
    @Test
    void consumeServerSentEventRaw() throws ExecutionException, InterruptedException {
        OpenAiSseClient openAiSseClient = OpenAiSseClient.getInstance();
        StringBuilder resultBuilder = new StringBuilder();
        OpenAiSseRawConsumer openAiSseRawConsumer = new OpenAiSseRawConsumer(part -> {
            System.out.print(part);
            resultBuilder.append(part);
        });
        CompletableFuture<HttpResponse<List<String>>> httpResponseCompletableFuture =
                openAiSseClient.consumeServerSentEvent(openAiApiConf, JMJson.getInstance().toJsonString(
                                new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setStream(true)
                                        .setMessages(List.of(new Message(user, "인공지능 공부하는 법 간단히 알려줘")))),
                        openAiSseRawConsumer);
        List<String> responseBody = httpResponseCompletableFuture.get().getBody();
        System.out.println(responseBody);
        assertEquals(responseBody.stream().map(Objects::toString).collect(Collectors.joining()).toString(),
                resultBuilder.toString());
//        JMJson.getInstance().toJsonFile(openAiSseRawConsumer.getBody(),
//                Paths.get("src", "test", "resources", "streamResponses.json").toFile());
    }
}