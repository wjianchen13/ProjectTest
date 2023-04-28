package com.example.projecttest.base;

import android.os.Handler;
import android.os.Message;

public interface IHandlerHolder {
    void removeMessages(int what);

    void removeCallbacks(Runnable runnable, Object token);

    boolean sendEmptyMessageAtTime(int what, long time);

    BaseHandler getH();

    boolean post(Runnable runnable);

    boolean hasMessages(int what, Object object);

    void removeCallbacksAndMessages(Object token);

    boolean sendMessageAtTime(Message message, long time);

    boolean postAtTime(Runnable runnable, long time);

    boolean hasMessages(int what);

    boolean sendEmptyMessage(int what);

    boolean sendMessageDelayed(Message message, long time);

    boolean sendEmptyMessageDelayed(int what, long time);

    void removeMessages(int what, Object object);

    void run(IRun run);

    Message obtainMessage(int what);

    Message obtainMessage(int what, Object obj);

    boolean postDelayed(Runnable runnable, long time);

    void setCallback(Handler.Callback mCallback);

    Message obtainMessage();

    void removeCallbacks(Runnable runnable);

    void onDestroy();

    boolean postAtFrontOfQueue(Runnable r);

    boolean sendMessage(Message message);
}