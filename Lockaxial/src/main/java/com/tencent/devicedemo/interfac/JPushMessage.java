package com.tencent.devicedemo.interfac;

public interface JPushMessage {
    public void onMessage(String from, String mime, String content);
}
