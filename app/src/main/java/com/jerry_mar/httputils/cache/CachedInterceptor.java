package com.jerry_mar.httputils.cache;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CachedInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!request.cacheControl().noStore()) {
            String method = request.method();
            RequestBody body = request.body();
            if(!method.toLowerCase().equals("GET") && body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                int size = formBody.size();
                StringBuffer params = new StringBuffer("?");
                for(int i = 0; i < size; i++) {
                    params.append(formBody.name(i))
                            .append("=")
                            .append(formBody.value(i))
                            .append("&");
                }
                params.delete(params.length() - 1, params.length());
                String url = request.url().toString() + params.toString();
                try {
                    Field field = request.getClass().getDeclaredField("method");
                    field.setAccessible(true);
                    field.set(request, "GET");
                    field.setAccessible(false);
                    field = request.getClass().getDeclaredField("url");
                    field.setAccessible(true);
                    if (url.regionMatches(true, 0, "ws:", 0, 3)) {
                        url = "http:" + url.substring(3);
                    } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
                        url = "https:" + url.substring(4);
                    }
                    field.set(request, HttpUrl.parse(url));
                    field.setAccessible(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(request.method().toUpperCase().equals("GET")) {
                try {
                    Field field = request.getClass().getDeclaredField("cacheControl");
                    field.setAccessible(true);
                    field.set(request, new CacheControl.Builder().build());
                    field.setAccessible(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Headers header = response.headers();
            Headers.Builder headerBuilder = header.newBuilder();
            headerBuilder.removeAll("Pragma");
            headerBuilder.removeAll("Cache-Control");
            headerBuilder.add("Cache-Control", "max-age=" + Integer.MAX_VALUE + ",max-stale=" + Integer.MAX_VALUE +
                    ",min-fresh=" + Integer.MAX_VALUE);
            response = response.newBuilder()
                    .headers(headerBuilder.build())
                    .build();
        }
        return response;
    }
}
