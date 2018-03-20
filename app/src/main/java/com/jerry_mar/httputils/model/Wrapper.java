package com.jerry_mar.httputils.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.Charset;

import okhttp3.MediaType;

public class Wrapper {
    private Object target;
    private String name;
    private MediaType type;
    private long size;

    public Wrapper(File file) {
        this(file, file.getName());
    }

    public Wrapper(File file, String name) {
        this(file, name, getMimeType(name), file.length());
    }

    public Wrapper(byte[] target, String name) {
        this(target, name, getMimeType(name), target.length);
    }

    public Wrapper(String target, String name) {
        this(target, name, getMimeType(name), target.getBytes(Charset.defaultCharset()).length);
    }

    public Wrapper(Object target, String name, MediaType type, long size) {
        this.target = target;
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public Object getTarget() {
        return target;
    }

    public String getName() {
        return name;
    }

    public MediaType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public static MediaType getMimeType(String name){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(name);
        if(mimeType == null)
            mimeType = "application/octet-stream";
        return MediaType.parse(mimeType);
    }
}
