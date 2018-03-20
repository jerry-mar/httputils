package com.jerry_mar.httputils.model;

import java.io.File;

import okhttp3.MediaType;

public class Part {
    private String name;
    private String value;
    private Wrapper wrapper;

    public Part(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Part(String name, File file) {
        this.name = name;
        wrapper = new Wrapper(file);
    }

    public Part(String name, File file, String fileName) {
        this.name = name;
        wrapper = new Wrapper(file, fileName);
    }

    public Part(String name, byte[] bytes, String fileName) {
        this.name = name;
        wrapper = new Wrapper(bytes, fileName);
    }

    public Part(String name, String msg, String fileName) {
        this.name = name;
        wrapper = new Wrapper(msg, fileName);
    }

    public Part(String name, Object target, String fileName, String type, long size) {
        this.name = name;
        wrapper = new Wrapper(target, fileName, MediaType.parse(type), size);
    }

    public boolean isMultipart() {
        return wrapper != null;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Object getTarget() {
        return wrapper.getTarget();
    }

    public String getFileName() {
        return wrapper.getName();
    }

    public MediaType getMediaType() {
        return wrapper.getType();
    }

    public long getFileSize() {
        return wrapper.getSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Part) {
            Part part = (Part) obj;
            return part.getName().equals(getName());
        }
        return super.equals(obj);
    }
}
