package kr.jm.openai.sse;


import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.openai.dto.sse.OpenAiSseData;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static kr.jm.openai.OpenAiCompletionsInterface.JmJson;

@Slf4j
public class OpenAiSseDataConsumer implements ResponseBodyConsumer<List<OpenAiSseData>> {
    private static final String DATA_BEGIN = "data: ";

    private static final int DATA_BEGIN_INDEX = DATA_BEGIN.length();

    private final Consumer<OpenAiSseData> openAiSseDataConsumer;

    private final List<OpenAiSseData> openAiSseDataList;
    private String tempPart;
    private final OpenAiSseRawConsumer openAiSseRawConsumer;

    public OpenAiSseDataConsumer(Consumer<OpenAiSseData> openAiSseDataConsumer) {
        this.openAiSseDataConsumer = openAiSseDataConsumer;
        this.openAiSseRawConsumer = new OpenAiSseRawConsumer(this::handleOpenAiSse);
        this.openAiSseDataList = new ArrayList<>();
    }


    private void handleOpenAiSse(String originPart) {
        for (String part : originPart.split("\n\n")) {
            if (isCompleteSsePart(part))
                handleOpenAiSsePart(part);
            else
                Optional.ofNullable(this.tempPart).map(tempPart -> this.tempPart += part)
                        .ifPresentOrElse(this::handleOpenAiSseTempPart, () -> this.tempPart = part);
        }

    }

    private void handleOpenAiSseTempPart(String tempPart) {
        if (isCompleteSsePart(tempPart)) {
            this.tempPart = null;
            handleOpenAiSsePart(tempPart);
        }
    }

    private void handleOpenAiSsePart(String part) {
        OpenAiSseData openAiSseData =
                JmJson.withJsonString(part.substring(DATA_BEGIN_INDEX), OpenAiSseData.class);
        this.openAiSseDataConsumer.accept(openAiSseData);
        this.openAiSseDataList.add(openAiSseData);
        Optional.ofNullable(this.tempPart).ifPresent(tempPart -> {
            log.warn("tempPart isn't processed !!! - " + tempPart);
            this.tempPart = null;
        });
    }

    private boolean isCompleteSsePart(String part) {
        return !part.isBlank() && part.startsWith(DATA_BEGIN) && part.endsWith("}");
    }

    @Override
    public void onBodyStart(String contentType, String charset, long contentLength) {
        this.openAiSseRawConsumer.onBodyStart(contentType, charset, contentLength);
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
