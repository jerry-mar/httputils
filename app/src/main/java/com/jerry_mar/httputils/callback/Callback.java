package com.jerry_mar.httputils.callback;

import com.jerry_mar.httputils.model.Receipt;

public interface Callback {
    void onPreExecute();
    void onFinish(Receipt receipt);
    void onError(int code, String message);
}
