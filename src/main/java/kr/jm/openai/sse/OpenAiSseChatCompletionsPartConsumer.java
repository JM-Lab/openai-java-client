package kr.jm.openai.sse;


import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.openai.dto.ChatChoice;
import kr.jm.openai.dto.Message;
import kr.jm.openai.dto.OpenAiChatCompletionsResponse;
import kr.jm.openai.dto.Role;
import kr.jm.openai.dto.sse.ChoicesItem;
import kr.jm.openai.dto.sse.OpenAiSseData;
import kr.jm.utils.JMOptional;
import kr.jm.utils.JMString;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

public class OpenAiSseChatCompletionsPartConsumer implements ResponseBodyConsumer<OpenAiChatCompletionsResponse> {
    private StringBuilder tempMessageBuilder;

    private final Consumer<String> partConsumer;

    private OpenAiChatCompletionsResponse openAiChatCompletionsResponse;

    private final OpenAiSseDataConsumer openAiSseDataConsumer;

    public OpenAiSseChatCompletionsPartConsumer(Consumer<String> partConsumer) {
        this.partConsumer = partConsumer;
        this.openAiSseDataConsumer = new OpenAiSseDataConsumer(this::handleOpenAiSseData);
    }

    @Override
    public void onBodyStart(String contentType, String charset, long contentLength) {
        this.openAiSseDataConsumer.onBodyStart(contentType, charset, contentLength);
        this.openAiChatCompletionsResponse = new OpenAiChatCompletionsResponse();
        this.tempMessageBuilder = new StringBuilder();
    }

    @Override
    public void onReceivedContentPart(ByteBuffer buffer) {
        this.openAiSseDataConsumer.onReceivedContentPart(buffer);
    }

    private void handleOpenAiSseData(OpenAiSseData openAiSseData) {
        Optional.ofNullable(openAiSseData).map(OpenAiSseData::getChoices).map(list -> list.get(0))
                .filter(choicesItem -> !"stop".equals(choicesItem.getFinishReason()))
                .map(ChoicesItem::getDelta).ifPresent(delta -> handleOpenAiSseData(openAiSseData, delta));
    }

    private void handleOpenAiSseData(OpenAiSseData openAiSseData, Map<String, String> delta) {
        JMOptional.getOptional(delta, "role").ifPresentOrElse(role -> initRole(openAiSseData, Role.valueOf(role)),
                () -> JMOptional.getOptional(delta, "content").filter(JMString::isNotNullOrBlank)
                        .ifPresent(this::appendPart));
    }

    private void initRole(OpenAiSseData openAiSseData, Role role) {
        if (Objects.isNull(this.openAiChatCompletionsResponse.getChoices()))
            initCompletionResponse(openAiSseData);
        else {
            completeMessage(this.openAiChatCompletionsResponse.getChoices()
                    .get(this.openAiChatCompletionsResponse.getChoices().size() - 1));
            this.tempMessageBuilder = new StringBuilder();
        }
        this.openAiChatCompletionsResponse.getChoices()
                .add(new ChatChoice().setMessage(new Message(role, null)));
    }

    private void initCompletionResponse(OpenAiSseData openAiSseData) {
        this.openAiChatCompletionsResponse.setChoices(new ArrayList<>()).setId(openAiSseData.getId())
                .setObject(openAiSseData.getObject()).setCreated(openAiSseData.getCreated())
                .setModel(openAiSseData.getModel());
    }

    private void appendPart(String content) {
        tempMessageBuilder.append(content);
        partConsumer.accept(content);
    }

    private void completeMessage(ChatChoice chatChoice) {
        chatChoice.setMessage(new Message(chatChoice.getMessage().getRole(), this.tempMessageBuilder.toString()));
    }

    @Override
    public void onCompletedBody() {
        JMOptional.getOptional(this.openAiChatCompletionsResponse.getChoices())
                .ifPresent(choices -> completeMessage(choices.get(choices.size() - 1)));
    }

    @Override
    public OpenAiChatCompletionsResponse getBody() {
        return this.openAiChatCompletionsResponse;
    }

    public List<String> getRawDataList() {
        return this.openAiSseDataConsumer.getRawDataList();
    }

}
