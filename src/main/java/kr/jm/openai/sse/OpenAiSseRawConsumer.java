package kr.jm.openai.sse;


import com.king.platform.net.http.ResponseBodyConsumer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OpenAiSseRawConsumer implements ResponseBodyConsumer<List<String>> {

    private final Consumer<String> openAiSseConsumer;

    private final List<String> rawDataList;

    public OpenAiSseRawConsumer(Consumer<String> openAiSseConsumer) {
        this.openAiSseConsumer = openAiSseConsumer;
        this.rawDataList = new ArrayList<>();
    }

    @Override
    public void onBodyStart(String contentType, String charset, long contentLength) {

    }

    @Override
    public void onReceivedContentPart(ByteBuffer buffer) {
        onReceivedContentPart(Charset.defaultCharset().decode(buffer).toString());
    }

    void onReceivedContentPart(String originPart) {
        this.rawDataList.add(originPart);
        this.openAiSseConsumer.accept(originPart);
    }

    @Override
    public void onCompletedBody() {
    }

    @Override
    public List<String> getBody() {
        return this.rawDataList;
    }

}
