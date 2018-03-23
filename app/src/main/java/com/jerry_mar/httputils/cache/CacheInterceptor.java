package com.jerry_mar.httputils.cache;

import android.content.Context;

import com.jerry_mar.httputils.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    private Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtils.isNetworkConnected(context) && !request.cacheControl().noCache()) {
            Request.Builder builder = request.newBuilder();
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
                builder.url(request.url().toString() + params.toString());
            }
            CacheControl.Builder cache = new CacheControl.Builder();
            if (request.cacheControl().noStore()) {
                cache.noStore();
            }
            builder.cacheControl(cache.build());
            builder.get();
            request = builder.build();
        }
        return chain.proceed(request);
    }
}
