package com.jerry_mar.httputils;

import com.jerry_mar.httputils.callback.ProgressCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class UploadRequestBody extends RequestBody {
    private RequestBody body;
    private ProgressCallback listener;
    private long sum;
    private long total;
    private long time;

    public UploadRequestBody(RequestBody body, ProgressCallback listener) {
        this.body = body;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return body.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return body.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink = Okio.buffer(newSink(sink));
        body.writeTo(sink);
        sink.flush();
    }

    private Sink newSink(BufferedSink sink) throws IOException {
        time = System.currentTimeMillis();
        total = contentLength();
        return new ForwardingSink(sink){
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                sum += byteCount;
                listener.updateProgress((int) (sum * 100 / total), (System.currentTimeMillis() - time) / 1000);
            }
        };
    }
}
