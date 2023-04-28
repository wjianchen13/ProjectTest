package com.example.projecttest.base;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2022/11/10.
 */
public class HandlerHolder implements IHandlerHolder, Handler.Callback, IHandlerHolderStatus {

    private final int threadPoolSize;
    private final boolean isMainLoop;
    private CustomHandler h;
    @NonNull
    private AtomicBoolean isStop = new AtomicBoolean(false);
    private HandlerThread mHandlerThread;
    private Handler.Callback mCallback;

    public HandlerHolder(boolean isMainLoop) {
        this(1, isMainLoop);
    }

    public HandlerHolder(int threadPoolSize, boolean isMainLoop) {
        this.threadPoolSize = threadPoolSize;
        this.isMainLoop = isMainLoop;
    }

    public HandlerHolder(int threadPoolSize) {
        this(threadPoolSize, true);
    }

    public HandlerHolder() {
        this(true);
    }

    @Override
    public boolean postDelayed(Runnable runnable, long time) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.postDelayed(runnable, time);
        }
        return false;
    }

    @Override
    public boolean sendEmptyMessage(int what) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.sendEmptyMessage(what);
        }
        return false;
    }

    @Override
    public Message obtainMessage(int what) {
        Message msg = null;
        if (isRunning()) {
            CustomHandler h = getH();
            msg = h != null ? h.obtainMessage(what) : null;
        }
        if (msg == null) {
            msg = new Message();
            msg.what = what;
        }
        return msg;
    }

    @Override
    public boolean sendMessageDelayed(Message message, long time) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.sendMessageDelayed(message, time);
        }
        return false;
    }

    @Override
    public boolean sendMessage(Message message) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.sendMessage(message);
        }
        return false;
    }

    @Override
    public Message obtainMessage() {
        if (isRunning()) {
            CustomHandler h = getH();
            return h != null ? h.obtainMessage() : new Message();
        }
        return new Message();
    }

    @Override
    public boolean postAtTime(Runnable runnable, long time) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.postAtTime(runnable, time);
        }
        return false;
    }

    @Override
    public boolean post(Runnable runnable) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.post(runnable);
        }
        return false;
    }

    @Override
    public boolean sendMessageAtTime(Message message, long time) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.sendMessageAtTime(message, time);
        }
        return false;
    }

    @Override
    public boolean hasMessages(int what) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.hasMessages(what);
        }
        return false;
    }

    @Override
    public boolean sendEmptyMessageDelayed(int what, long time) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.sendEmptyMessageDelayed(what, time);
        }
        return false;
    }

    @Override
    public boolean sendEmptyMessageAtTime(int what, long time) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.sendEmptyMessageAtTime(what, time);
        }
        return false;
    }

    @Override
    public void removeCallbacks(Runnable runnable) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) h.removeCallbacks(runnable);
        }
    }

    @Override
    public boolean isRunning() {
        return !isStop.get();
    }

    @Override
    public boolean handleMessage(Message msg) {
        return mCallback != null && mCallback.handleMessage(msg);
    }

    public void onDestroy() {
        isStop.set(true);
        if (h != null) {
            h.removeCallbacksAndMessages(null);
            h = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    @Override
    public boolean isStop() {
        return isStop.get();
    }

    public void setCallback(Handler.Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public Message obtainMessage(int what, Object obj) {
        Message msg = null;
        if (isRunning()) {
            CustomHandler h = getH();
            msg = h != null ? h.obtainMessage(what, obj) : null;
        }
        if (msg == null) {
            msg = new Message();
            msg.what = what;
            msg.obj = obj;
        }
        return msg;
    }

    @Override
    public void removeMessages(int what) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) h.removeMessages(what);
        }
    }

    @Override
    public void removeCallbacksAndMessages(Object token) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) h.removeCallbacksAndMessages(token);
        }
    }

    @Override
    public CustomHandler getH() {
        if (isStop())
            return null;
        if (h == null)
            synchronized (CustomHandler.class) {
                if (h == null) {
                    if (isMainLoop)
                        h = new CustomHandler(Looper.getMainLooper(), this);
                    else {
                        if (mHandlerThread == null) {
                            mHandlerThread = new HandlerThread("ChunkHandlerThread");
                            mHandlerThread.start();
                        }
                        h = new CustomHandler(mHandlerThread.getLooper(), this);
                    }
                }
            }
        return h;
    }

    @Override
    public void removeCallbacks(Runnable runnable, Object token) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) h.removeCallbacks(runnable, token);
        }
    }

    /**
     * 运行在非Ui线程
     *
     * @param run 执行体
     */
    @Override
    public final void run(@NonNull final IRun run) {
        if (isRunning() && run != null) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    run.run(HandlerHolder.this);
                }
            };
//            ThreadUtils.getHandlerHolder().execute(runnable);
        }
    }

    @Override
    public boolean hasMessages(int what, Object object) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.hasMessages(what, object);
        }
        return false;
    }

    @Override
    public boolean postAtFrontOfQueue(Runnable r) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) return h.postAtFrontOfQueue(r);
        }
        return false;
    }

    @Override
    public void removeMessages(int what, Object object) {
        if (isRunning()) {
            CustomHandler h = getH();
            if (h != null) h.removeMessages(what, object);
        }
    }

    protected static class CustomHandler extends BaseHandler {

        public CustomHandler(@NonNull Looper looper, @NonNull Handler.Callback callback) {
            super(looper, callback);
        }

        private CustomHandler(Handler.Callback callback) {
            super(callback);
        }
    }

}
