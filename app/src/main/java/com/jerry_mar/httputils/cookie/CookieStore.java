package com.jerry_mar.httputils.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieStore {
    List<Cookie> get(HttpUrl url);
    void add(HttpUrl url, Cookie cookie);
}
