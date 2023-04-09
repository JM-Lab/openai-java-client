package kr.jm.openai.sse;


import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.openai.dto.sse.OpenAiSseData;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static kr.jm.openai.OpenAiCompletionsInterface.JmJson;

public class OpenAiSseDataConsumer implements ResponseBodyConsumer<List<OpenAiSseData>> {


    private static final int DATA_BEGIN_INDEX = "data: ".length();

    private final Consumer<OpenAiSseData> openAiSseDataConsumer;

    private List<OpenAiSseData> openAiSseDataList;
    private final OpenAiSseRawConsumer openAiSseRawConsumer;

    public OpenAiSseDataConsumer(Consumer<OpenAiSseData> openAiSseDataConsumer) {
        this.openAiSseDataConsumer = openAiSseDataConsumer;
        this.openAiSseRawConsumer = new OpenAiSseRawConsumer(this::handleOpenAiSse);
    }

    private void handleOpenAiSse(String originPart) {
        for (String data : originPart.split("\n\n"))
            if (!data.isBlank() && !data.endsWith("[DONE]")) {
                OpenAiSseData openAiSseData =
                        JmJson.withJsonString(data.substring(DATA_BEGIN_INDEX), OpenAiSseData.class);
                this.openAiSseDataConsumer.accept(openAiSseData);
                this.openAiSseDataList.add(openAiSseData);
            }
    }

    @Override
    public void onBodyStart(String contentType, String charset, long contentLength) {
        this.openAiSseRawConsumer.onBodyStart(contentType, charset, contentLength);
        this.openAiSseDataList = new ArrayList<>();
    }

    @Override
    public void onReceivedContentPart(ByteBuffer byteBuffer) {
        this.openAiSseRawConsumer.onReceivedContentPart(byteBuffer);
    }

    @Override
    public void onCompletedBody() {
        this.openAiSseRawConsumer.onCompletedBody();
    }

    @Override
    public List<OpenAiSseData> getBody() {
        return this.openAiSseDataList;
    }

    public List<String> getRawDataList() {
        return this.openAiSseRawConsumer.getBody();
    }
}
