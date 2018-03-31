package com.jerry_mar.httputils.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;

public class Configuration {
    private long connectTimeout = 30000;
    private long readTimeout = 30000;
    private long writeTimeout = 30000;

    private Map<String, String> header = new HashMap<>();
    private List<Part> parameter = new ArrayList<>();

    private List<Interceptor> networkInterceptors = new ArrayList<>();
    private List<Interceptor> interceptors = new ArrayList<>();

    private CookieJar cookieJar = CookieJar.NO_COOKIES;//new CookieManager(new CookieStoreImpl());
    private Cache cache;
    private Authenticator authenticator;
    private List<InputStream> certificates = new ArrayList<>();
    private HostnameVerifier verifier;

    private boolean retryOnConnectionFailure = true;
    private boolean followRedirects = true;
    private boolean followSslRedirects = true;

    public long getConnectTimeout() {
        return connectTimeout;
    }
    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }
    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }
    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Map<String, String> getHeader() {
        return header;
    }
    public void addHeader(String name, String value) {
        header.put(name, value);
    }

    public List<Part> getParameter() {
        return parameter;
    }
    public void addParameter(Part part) {
        parameter.add(part);
    }

    public List<Interceptor> getNetworkInterceptors() {
        return networkInterceptors;
    }
    public void addNetworkInterceptor(Interceptor interceptor) {
        networkInterceptors.add(interceptor);
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public CookieJar getCookieJar() {
        return cookieJar;
    }
    public void setCookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
    }

    public Cache getCache() {
        return cache;
    }
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }
    public void setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isFollowSslRedirects() {
        return followSslRedirects;
    }
    public void setFollowSslRedirects(boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public List<InputStream> getCertificates() {
        return certificates;
    }
    public void addCertificate(InputStream certificate) {
        this.certificates.add(certificate);
    }

    public HostnameVerifier getHostnameVerifier() {
        return verifier;
    }
    public void setVHostnameVerifier(HostnameVerifier verifier) {
        this.verifier = verifier;
    }
}
