package com.jerry_mar.httputils;

import com.jerry_mar.httputils.callback.AsyncCallback;
import com.jerry_mar.httputils.callback.Callback;
import com.jerry_mar.httputils.callback.ProgressCallback;
import com.jerry_mar.httputils.model.Configuration;
import com.jerry_mar.httputils.model.Packet;
import com.jerry_mar.httputils.model.Part;
import com.jerry_mar.httputils.model.Receipt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    private static HttpUtils instance;

    public static HttpUtils getInstance() {
        return instance;
    }

    public synchronized static void init(Configuration configuration) {
        if (instance == null) {
            instance = new HttpUtils(configuration);
        }
    }

    public static Receipt get(Packet packet) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        instance.compilePacket(packet);
        Request.Builder builder = instance.builder(packet, false);
        return instance.executeOnMainThread(builder.get(), (String) packet.findExtraData(Packet.PACKET_NAME));
    }

    public static void get(Packet packet, Callback callback) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        instance.compilePacket(packet);
        Request.Builder builder = instance.builder(packet, false);
        instance.executeOnNewThread(builder.get(), packet.getExtraData(), callback);
    }

    public static Receipt post(Packet packet) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        Request.Builder builder = instance.builder(packet, false);
        builder.post(instance.createBody(packet));
        return instance.executeOnMainThread(builder, (String) packet.findExtraData(Packet.PACKET_NAME));
    }

    public static void post(Packet packet, Callback callback) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        Request.Builder builder = instance.builder(packet, false);
        builder.post(instance.createBody(packet));
        instance.executeOnNewThread(builder, packet.getExtraData(), callback);
    }

    public static Receipt upload(Packet packet, ProgressCallback callback) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        Request.Builder builder = instance.builder(packet, true);
        builder.post(instance.createMultipartBody(packet, callback));
        return instance.executeOnMainThread(builder, (String) packet.findExtraData(Packet.PACKET_NAME));
    }

    public static void upload(Packet packet, Callback callback) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        Request.Builder builder = instance.builder(packet, true);
        builder.post(instance.createMultipartBody(packet, callback instanceof
                ProgressCallback ? (ProgressCallback) callback : null));
        instance.executeOnNewThread(builder, packet.getExtraData(), callback);
    }

    public static Receipt download(Packet packet, ProgressCallback callback) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        Request.Builder builder = instance.builder(packet, true);
        if (packet.getParameter() != null) {
            builder.post(instance.createMultipartBody(packet, null));
        }
        return instance.executeOnMainThread(builder, packet.getSaveFile(), callback, (String) packet.findExtraData(Packet.PACKET_NAME));
    }

    public static void download(Packet packet, AsyncCallback callback) {
        HttpUtils instance = getInstance();
        instance.pastePacket(packet);
        Request.Builder builder = instance.builder(packet, true);
        if (packet.getParameter() != null) {
            builder.post(instance.createMultipartBody(packet, null));
        }
        instance.executeOnNewThread(builder, packet.getSaveFile(), packet.getExtraData(), callback);
    }

    public static void removeTask(String simpleName) {
        TaskUtils.removeTask(simpleName);
    }

    private OkHttpClient core;
    private List<Part> parameter;
    private Headers header;

    private HttpUtils(Configuration config) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.MILLISECONDS);

        Headers.Builder headerBuilder = new Headers.Builder();
        for(Map.Entry<String, String> header : config.getHeader().entrySet()){
            headerBuilder.add(header.getKey(), header.getValue());
        }
        header = headerBuilder.build();
        parameter = config.getParameter();

        CookieJar cookieJar = config.getCookieJar();
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }

        Cache cache = config.getCache();
        if (cache != null) {
            builder.cache(cache);
        }

        List<Interceptor> interceptors = config.getInterceptors();
        if(interceptors != null) {
            builder.interceptors().addAll(interceptors);
        }

        List<Interceptor> networkInterceptors = config.getNetworkInterceptors();
        if(networkInterceptors != null) {
            builder.networkInterceptors().addAll(networkInterceptors);
        }

        builder.followRedirects(config.isFollowRedirects());
        builder.retryOnConnectionFailure(config.isRetryOnConnectionFailure());

        core = builder.build();
    }


    private void pastePacket(Packet packet) {
        HttpUtils instance = getInstance();
        packet.getParameter().addAll(parameter);
    }

    private void compilePacket(Packet packet) {
        List<Part> parameter = packet.getParameter();
        if(parameter != null) {
            StringBuffer url = new StringBuffer(packet.getUrl());
            if(url.indexOf("?") < 0) {
                url.append("?");
            } else if(url.indexOf("?") < url.length() - 1) {
                url.append("&");
            }

            for(Part part : parameter) {
                url.append(part.getName())
                        .append("=")
                        .append(part.getValue())
                        .append("&");
            }

            int length = url.length();
            url.delete(length - 1, length);
            packet.setUrl(url.toString());
        }
    }

    private Request.Builder builder(Packet packet, boolean noCache) {
        Request.Builder builder = new Request.Builder();
        builder.url(packet.getUrl());
        pasteHeader(builder, packet);
        pasteCache(builder, packet, noCache);
        return builder;
    }

    private RequestBody createBody(Packet packet) {
        RequestBody body;
        String json = packet.getJson();
        if(json != null) {
            body = RequestBody.create(MediaType.parse("application/json"), json);
        } else {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            List<Part> parameter = packet.getParameter();
            if(parameter != null) {
                for (Part part : parameter) {
                    bodyBuilder.addEncoded(part.getName(), part.getValue());
                }
            }
            body = bodyBuilder.build();
        }
        return body;
    }

    private RequestBody createMultipartBody(Packet packet, ProgressCallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        List<Part> parameter = packet.getParameter();

        for(Part part : parameter) {
            if(part.isMultipart()) {
                Object file = part.getTarget();
                MediaType type = part.getMediaType();
                RequestBody partBody;

                if(file instanceof File)
                    partBody = RequestBody.create(type, (File) file);
                else if(file instanceof byte[])
                    partBody = RequestBody.create(type, (byte[]) file);
                else if (file instanceof String)
                    partBody = RequestBody.create(type, ((String) file));
                else throw new RuntimeException("上传附件数据格式不识别!!!");
                if (callback != null) {
                    builder.addFormDataPart(part.getName(), part.getFileName(), new UploadRequestBody(partBody, callback));
                } else {
                    builder.addFormDataPart(part.getName(), part.getFileName(), partBody);
                }
            } else {
                builder.addFormDataPart(part.getName(), part.getValue());
            }
        }

        return builder.build();
    }

    private Receipt executeOnMainThread(Request.Builder builder, String simpale) {
        Receipt result;
        TaskUtils.addTask(simpale, null);
        Call call = instance.core.newCall(builder.build());
        try {
            Response response = call.execute();
            result = new Receipt.Builder(response).build();
        } catch (IOException e) {
            result = new Receipt.Builder(e).build();
        }
        if (TaskUtils.removeTask(simpale, null)) {
            result.code(Receipt.ERROR_RELEASE);
        }
        return result;
    }

    private void executeOnNewThread(Request.Builder builder, Map<String, Object> extraData, Callback callback) {
        Call call = instance.core.newCall(builder.build());
        RequestTask task = new RequestTask(extraData, callback);
        TaskUtils.addTask((String) extraData.get(Packet.PACKET_NAME), task);
        callback.onPreExecute();
        call.enqueue(task);
    }

    private Receipt executeOnMainThread(Request.Builder builder, File saveFile, ProgressCallback callback, String simpale) {
        Receipt result;
        TaskUtils.addTask(simpale, null);
        Call call = instance.core.newCall(builder.build());
        try {
            Response response = call.execute();
            long total = response.body().contentLength();
            FileUtils.saveFile(response, saveFile, callback);
            if (total != saveFile.length()) {
                throw new RuntimeException();
            }
            result = new Receipt.Builder(saveFile).build();
        } catch (IOException e) {
            result = new Receipt.Builder(e).build();
        }
        if (TaskUtils.removeTask(simpale, null)) {
            result.code(Receipt.ERROR_RELEASE);
        }
        return result;
    }

    private void executeOnNewThread(Request.Builder builder, File saveFile,
                        Map<String, Object> extraData, AsyncCallback callback) {
        Call call = instance.core.newCall(builder.build());
        RequestTask task = new RequestTask(saveFile, extraData, callback);
        TaskUtils.addTask((String) extraData.get(Packet.PACKET_NAME), task);
        callback.onPreExecute();
        call.enqueue(task);
    }

    private void pasteCache(Request.Builder builder, Packet packet, boolean noCache) {
        CacheControl cache;
        if (noCache) {
            cache = new CacheControl.Builder().noCache().noStore().build();
        } else {
            if (packet.noCache() && packet.cache()) {
                new RuntimeException("请正确配置缓存策略");
            }
            if (packet.noCache()) {
                cache = CacheControl.FORCE_NETWORK;
            } else {
                cache = new CacheControl.Builder().build();
            }
            if (packet.cache()) {
                cache.onlyIfCached();
            }
        }
        builder.cacheControl(cache);
    }

    private void pasteHeader(Request.Builder builder, Packet packet) {
        Map<String, String> header = packet.getHeader();
        Headers.Builder headerBuilder = this.header.newBuilder();
        if(header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headerBuilder.removeAll(entry.getKey());
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        builder.headers(headerBuilder.build());
    }
}
