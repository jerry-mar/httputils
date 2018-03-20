package com.jerry_mar.httputils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.jerry_mar.httputils.callback.AsyncCallback;
import com.jerry_mar.httputils.callback.Callback;
import com.jerry_mar.httputils.callback.ProgressCallback;
import com.jerry_mar.httputils.model.Packet;
import com.jerry_mar.httputils.model.Receipt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class RequestTask implements okhttp3.Callback, ProgressCallback {
    public final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            RequestTask task = (RequestTask) msg.obj;
            switch (msg.what) {
                case 0x00 :
                    if (extraData == null || TaskUtils.removeTask((String)
                            extraData.get(Packet.PACKET_NAME), task)) {
                        task.onError();
                    }
                    break;
                case 0x01 :
                    if (extraData == null || TaskUtils.removeTask((String)
                            extraData.get(Packet.PACKET_NAME), task)) {
                        task.onFinish();
                    }
                    break;
                case 0x02 :
                    if (extraData == null || TaskUtils.contains((String)
                            extraData.get(Packet.PACKET_NAME), task)) {
                        task.updateProgress(msg.arg1, msg.arg2);
                    }
                    break;
            }
        }
    };

    private File saveFile;
    private Map<String, Object> extraData;
    private Callback callback;
    private Receipt receipt;
    private Map<Integer, Long> speed = new HashMap<>();

    public RequestTask(Map<String, Object> extraData, Callback callback) {
        this.extraData = extraData;
        this.callback = callback;
    }

    public RequestTask(File saveFile, Map<String, Object> extraData, AsyncCallback callback) {
        this.saveFile = saveFile;
        this.extraData = extraData;
        this.callback = callback;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        receipt = new Receipt(new Receipt.Builder(e), saveFile, extraData);
        Message msg = handler.obtainMessage();
        msg.obj = this;
        msg.what = 0x00;
        handler.sendMessage(msg);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (saveFile != null) {
            FileUtils.saveFile(response, saveFile, this);
            receipt = new Receipt(new Receipt.Builder(saveFile), extraData);
        } else {
            receipt = new Receipt(new Receipt.Builder(response), saveFile, extraData);
        }
        Message msg = handler.obtainMessage();
        msg.obj = this;
        if (receipt.isSuccessful()) {
            msg.what = 0x01;
        } else {
            msg.what = 0x00;
        }
        handler.sendMessage(msg);
    }

    public void onError() {
        callback.onError(receipt.code(), receipt.message());
    }

    public void onFinish() {
        callback.onFinish(receipt);
    }

    public void updateProgress(int progress, int index) {
        if (callback instanceof ProgressCallback) {
            ((ProgressCallback) callback).updateProgress(progress, speed.get(index));
        }
    }

    @Override
    public void updateProgress(int progress, long speed) {
        Message msg = handler.obtainMessage();
        msg.obj = this;
        msg.arg1 = progress;
        msg.arg2 = this.speed.size();
        this.speed.put(msg.arg2, speed);
        msg.what = 0x02;
        handler.sendMessage(msg);
    }
}
