package com.jerry_mar.httputils.model;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static okhttp3.internal.Util.UTF_8;

public class Receipt {
    public static int ERROR_NONE = 0;
    public static int ERROR_RELEASE = -1;
    public static int ERROR_UNKNOW = 1003;
    public static int ERROR_TIMEOUT = 408;

    private int code = ERROR_NONE;
    private String message;
    private Charset charset;
    private byte[] source;
    private File saveFile;
    private Integer id;
    private String simpleName;
    private Map<String, Object> extraData;

    public Receipt(Builder builder) {
        code = builder.code;
        message = builder.message;
        charset = builder.charset;
        source = builder.source;
        saveFile = builder.saveFile;
    }

    public Receipt(Builder builder, Map<String, Object> extraData) {
        this(builder);
        if (extraData != null) {
            id = (Integer) extraData.remove(Packet.PACKET_ID);
            simpleName = (String) extraData.remove(Packet.PACKET_NAME);
            this.extraData = extraData;
        }
    }

    public Receipt(Builder builder, File saveFile, Map<String, Object> extraData) {
        this(builder);
        this.saveFile = saveFile;
        if (extraData != null) {
            id = (Integer) extraData.remove(Packet.PACKET_ID);
            simpleName = (String) extraData.remove(Packet.PACKET_NAME);
            this.extraData = extraData;
        }
    }

    public boolean isSuccessful() {
        return code == ERROR_NONE;
    }

    public int code() {
        return code;
    }

    public void code(int code) {
        this.code = code;
    }

    public String message() {
        return message;
    }

    public byte[] bytes() {
        return source;
    }

    public String string() {
        return new String(source, charset);
    }

    public File file() {
        return saveFile;
    }

    public Integer getId() {
        return id;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public <T> T getExtraData(String key) {
        return (T) extraData.get(key);
    }

    public static class Builder {
        int code;
        String message;
        Charset charset;
        byte[] source;
        File saveFile;

        public Builder(IOException e) {
            if (e instanceof SocketTimeoutException || (e instanceof InterruptedIOException &&
                    TextUtils.equals(e.getMessage(), "timeout"))) {
                message = "服务器超时,请重新访问!";
                code = ERROR_TIMEOUT;
            } else {
                message = "服务器异常,请稍后重试!";
                code = ERROR_UNKNOW;
            }
        }

        public Builder(Response response) {
            try {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    MediaType contentType = body.contentType();
                    charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
                    source = body.bytes();
                } else {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                message = "服务器异常,请稍后重试!";
                code = response.code();
            }
        }

        public Builder(File saveFile) {
            this.saveFile = saveFile;
        }

        public Receipt build() {
            return new Receipt(this);
        }
    }
}
