package kr.jm.openai;

import kr.jm.openai.dto.Message;
import kr.jm.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.openai.dto.OpenAiChatCompletionsResponse;
import kr.jm.openai.sse.OpenAiSseChatCompletionsPartConsumer;
import kr.jm.utils.JMOptional;
import kr.jm.utils.enums.OS;
import kr.jm.utils.http.JMHttpRequester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static kr.jm.openai.dto.Role.system;
import static kr.jm.openai.dto.Role.user;
import static org.junit.jupiter.api.Assertions.assertEquals;


// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.

class OpenAiChatCompletionsTest {

    OpenAiChatCompletions openAiChatCompletions;
    String openAiKey;

    @BeforeEach
    void setUp() {
        this.openAiKey = JMOptional.getOptional(System.getenv("OPENAI_API_KEY"))
                .orElseThrow(() -> new RuntimeException("The OPENAI_API_KEY environment variable is not set."));
    }

    @Disabled
    @Test
    void completions() {
        String responseAsString = JMHttpRequester.getInstance()
                .postResponseAsString(
                        Map.of("Content-Type", "application/json", "Authorization", "Bearer " + this.openAiKey),
                        "https://api.openai.com/v1/chat/completions", "{\n" +
                                "  \"model\": \"gpt-3.5-turbo\",\n" +
                                "  \"messages\": [{\"role\": \"user\", \"content\": \"Hello!\"}]\n" +
                                "}");
        System.out.println(responseAsString);
    }

    @Disabled
    @Test
    void testCompletions() {
        this.openAiChatCompletions =
                new OpenAiChatCompletions(new OpenAiApiConf("https://api.openai.com/v1/chat/completions",
                        this.openAiKey));
        String systemPrompt =
                "Platform: " + OS.getOsName() + OS.getLineSeparator() + "Version: " + OS.getOsVersion() +
                        OS.getLineSeparator() + "Do Not: explanations" + OS.getLineSeparator();
        System.out.println(systemPrompt);
        String prompt = "Generate a shell command to " + "10번 반복하면서 파일 안의 글 찾기";
        System.out.println(prompt);
        OpenAiChatCompletionsResponse completionsResult = openAiChatCompletions.request(
                new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000)
                        .setMessages(List.of(new Message(user, systemPrompt + prompt))));
        System.out.println(completionsResult);
        String content1 = completionsResult.getChoices().get(0).getMessage().getContent();

        completionsResult = openAiChatCompletions.request(
                new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000).setMessages(
                        List.of(new Message(system, systemPrompt), new Message(user, prompt))));
        System.out.println(completionsResult);
        String content2 = completionsResult.getChoices().get(0).getMessage().getContent();
        Assertions.assertNotEquals(content1, content2);

    }

    @Disabled
    @Test
    void testCompletionsSse() throws ExecutionException, InterruptedException {
        this.openAiChatCompletions =
                new OpenAiChatCompletions(new OpenAiApiConf("https://api.openai.com/v1/chat/completions",
                        this.openAiKey));
        String systemPrompt =
                "Platform: " + OS.getOsName() + OS.getLineSeparator() + "Version: " + OS.getOsVersion() +
                        OS.getLineSeparator() + "Do Not: explanations" + OS.getLineSeparator();
        System.out.println(systemPrompt);
        String prompt = "Generate a shell command to " + "10번 반복하면서 파일 안의 글 찾기";
        System.out.println(prompt);

        StringBuilder resultBuilder = new StringBuilder();
        OpenAiSseChatCompletionsPartConsumer openAiSsePartConsumer = new OpenAiSseChatCompletionsPartConsumer(part -> {
            System.out.print(part);
            resultBuilder.append(part);
        });
        CompletableFuture<OpenAiChatCompletionsResponse> openAiChatCompletionsResponseCompletableFuture =
                openAiChatCompletions.requestWithSse(
                        new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000).setMessages(
                                List.of(new Message(system, systemPrompt), new Message(user, prompt))),
                        openAiSsePartConsumer);
        System.out.println(resultBuilder);
        OpenAiChatCompletionsResponse responseBody = openAiChatCompletionsResponseCompletableFuture.get();
        System.out.println(responseBody);
        assertEquals(responseBody.getChoices().get(0).getMessage().getContent(), resultBuilder.toString());


    }
}