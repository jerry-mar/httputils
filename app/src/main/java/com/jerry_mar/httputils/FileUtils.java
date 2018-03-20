package com.jerry_mar.httputils;

import com.jerry_mar.httputils.callback.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public class FileUtils {
    public static void saveFile(Response response, File saveFile, ProgressCallback callback) throws IOException {
        InputStream in = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream writer = null;
        long previousTime = System.currentTimeMillis();
        try {
            in = response.body().byteStream();
            long total = response.body().contentLength();
            long sum = 0;

            writer = new FileOutputStream(saveFile);
            while ((len = in.read(buf)) != -1) {
                sum += len;
                writer.write(buf, 0, len);

                if (callback != null) {
                    long time = (System.currentTimeMillis() - previousTime) / 1000;
                    time = time == 0 ? 1 : time;
                    callback.updateProgress((int) (sum * 100 / total), sum / time);
                }
            }
            writer.flush();
        } finally {
            try {
                if (in != null) { in.close(); }
            } catch (IOException e) {}
            try {
                if (writer != null) { writer.close(); }
            } catch (IOException e) {}
        }
    }
}
