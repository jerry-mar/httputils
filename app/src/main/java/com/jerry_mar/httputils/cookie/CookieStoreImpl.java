package com.jerry_mar.httputils.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class CookieStoreImpl implements CookieStore {
    private final HashMap<String, ConcurrentHashMap<String, Cookie>> cookies;

    public CookieStoreImpl() {
        cookies = new HashMap<>();
    }

    @Override
    public List<Cookie> get(HttpUrl url) {
        ArrayList<Cookie> result = new ArrayList<>();
        if (cookies.containsKey(url.host()))
            result.addAll(cookies.get(url.host()).values());
        return result;
    }

    @Override
    public void add(HttpUrl url, Cookie cookie) {
        String name = getCookieToken(cookie);
        if (!cookie.persistent()) {
            if (!cookies.containsKey(url.host())) {
                cookies.put(url.host(), new ConcurrentHashMap<String, Cookie>());
            }
            cookies.get(url.host()).put(name, cookie);
        } else {
            if (cookies.containsKey(url.host())) {
                cookies.get(url.host()).remove(name);
            }
        }
        //持久化
    }

    protected String getCookieToken(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }
}
