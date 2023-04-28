package com.example.projecttest.base;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2022/11/10.
 */
public class BaseHandler<T> extends Handler {
    private WeakReference<T> mReference;

    public BaseHandler() {
        super();
    }

    public BaseHandler(@NonNull Looper looper, @NonNull Callback callback) {
        super(looper, callback);
    }

    public BaseHandler(@Nullable Callback callback) {
        super(callback);
    }

    public BaseHandler(@NonNull T target, @NonNull Looper looper, @NonNull Callback callback) {
        super(looper, callback);
        init(target);
    }

    public BaseHandler(@NonNull Looper looper) {
        super(looper);
    }

    public BaseHandler(@Nullable T target) {
        super();
        init(target);
    }

    public BaseHandler(@NonNull T target, @NonNull Looper looper) {
        super(looper);
        init(target);
    }

    public BaseHandler(@NonNull T target, @Nullable Callback callback) {
        super(callback);
        init(target);
    }

    protected T getReference() {
        return mReference == null ? null : mReference.get();
    }

    private void init(T target) {
        if (target != null) {
            mReference = new WeakReference<>(target);
        }
    }
}
