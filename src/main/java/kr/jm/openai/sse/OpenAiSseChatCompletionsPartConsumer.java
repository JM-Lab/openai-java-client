package kr.jm.openai.sse;


import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.openai.dto.ChatChoice;
import kr.jm.openai.dto.Message;
import kr.jm.openai.dto.OpenAiChatCompletionsResponse;
import kr.jm.openai.dto.Role;
import kr.jm.openai.dto.sse.ChoicesItem;
import kr.jm.openai.dto.sse.OpenAiSseData;
import kr.jm.utils.JMOptional;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public void onBodyStart(String contentType, String charset, long contentLength) throws Exception {
        this.openAiSseDataConsumer.onBodyStart(contentType, charset, contentLength);
        this.openAiChatCompletionsResponse = new OpenAiChatCompletionsResponse();
        this.tempMessageBuilder = new StringBuilder();
    }

    @Override
    public void onReceivedContentPart(ByteBuffer buffer) throws Exception {
        this.openAiSseDataConsumer.onReceivedContentPart(buffer);
    }

    private void handleOpenAiSseData(OpenAiSseData openAiSseData) {
        ChoicesItem choicesItem = openAiSseData.getChoices().get(0);
        if (!"stop".equals(choicesItem.getFinishReason()))
            JMOptional.getOptional(choicesItem.getDelta(), "content").ifPresentOrElse(
                    this::appendPart, () -> JMOptional.getOptional(choicesItem.getDelta(), "role")
                            .ifPresent(role -> initRole(openAiSseData, Role.valueOf(role))));
    }

    private void initRole(OpenAiSseData openAiSseData, Role role) {
        if (Objects.isNull(this.openAiChatCompletionsResponse.getChoices()))
            initCompletionResopnse(openAiSseData);
        else {
            completeMessage(this.openAiChatCompletionsResponse.getChoices()
                    .get(this.openAiChatCompletionsResponse.getChoices().size() - 1));
            this.tempMessageBuilder = new StringBuilder();
        }
        this.openAiChatCompletionsResponse.getChoices()
                .add(new ChatChoice().setMessage(new Message(role, null)));
    }

    private void initCompletionResopnse(OpenAiSseData openAiSseData) {
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
    public void onCompletedBody() throws Exception {
        completeMessage(this.openAiChatCompletionsResponse.getChoices()
                .get(this.openAiChatCompletionsResponse.getChoices().size() - 1));
    }

    @Override
    public OpenAiChatCompletionsResponse getBody() {
        return this.openAiChatCompletionsResponse;
    }

    public List<String> getRawDataList() {
        return this.openAiSseDataConsumer.getRawDataList();
    }

}
