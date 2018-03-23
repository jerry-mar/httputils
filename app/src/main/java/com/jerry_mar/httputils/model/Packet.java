package com.jerry_mar.httputils.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Packet {
    public static final String PACKET_ID = "PACKET_ID";
    public static final String PACKET_NAME = "PACKET_NAME";
    private String url;
    private String json;
    private List<Part> parameter = new ArrayList<>();
    private Map<String, String> header;
    private Map<String, Object> extraData;
    private boolean noCache;
    private boolean cached = true;
    private File saveFile;

    public Packet(String url, String simpleName) {
        this.url = url;
        addExtraData(PACKET_NAME, simpleName);
    }

    public void setId(int id) {
        addExtraData(PACKET_ID, id);
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getJson() {
        return json;
    }
    public void setJson(String json) {
        this.json = json;
    }

    public List<Part> getParameter() {
        return parameter;
    }
    public void addParameter(String name, Object value) {
        String data = "";
        if (value != null) {
            data = value.toString();
        }
        parameter.add(new Part(name, data));
    }
    public void addParameter(String name, File data) {
        parameter.add(new Part(name, data));
    }
    public void addParameter(String name, File data, String fileName) {
        parameter.add(new Part(name, data, fileName));
    }
    public void addParameter(String name, byte[] bytes, String fileName) {
        parameter.add(new Part(name, bytes, fileName));
    }
    public void addParameter(String name, String msg, String fileName) {
        parameter.add(new Part(name, msg, fileName));
    }
    public void addParameter(String name, Object target, String fileName, String type, long size) {
        parameter.add(new Part(name, target, fileName, type, size));
    }
    public void removeParameter(String name) {
        parameter.remove(new Part(name, (String) null));
    }

    public Map<String, String> getHeader() {
        return header;
    }
    public void addHeader(String name, String value) {
        if(header == null) {
            header = new HashMap<>();
        }
        header.put(name, value);
    }
    public void removeHeader(String name) {
        if(header != null) {
            header.remove(name);
        }
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public <T> T findExtraData(String key) {
        if (extraData != null) {
            return (T) extraData.get(key);
        }
        return null;
    }
    public void addExtraData(String name, Object value) {
        if(extraData == null) {
            extraData = new HashMap<>();
        }
        extraData.put(name, value);
    }
    public void removeExtraData(Object name) {
        if(extraData != null) {
            extraData.remove(name);
        }
    }

    public void noCache(boolean cache) {
        noCache = cache;
    }

    public boolean noCache() {
        return noCache;
    }

    public void cache(boolean cache) {
        this.cached = cache;
    }

    public boolean cache() {
        return cached;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }
}
