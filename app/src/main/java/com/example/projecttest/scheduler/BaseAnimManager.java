package com.example.projecttest.scheduler;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public abstract class BaseAnimManager<V extends ViewGroup, T extends QueueAnimBean> extends QueueExecutor<T> {

    @NonNull
    protected final WeakReference<Activity> mActivity;                    // context
    protected ViewStub vs;
    protected V mFrameLayout;           // 动画容器View
    protected String mAnimName;                      // 动画名称
    protected ILiveAnimaListener onLiveAnimaListener; // 用于监听礼物动画队列
    protected boolean isInit = false;

    public BaseAnimManager(Activity activity, int parentViewResId, String animName, ILiveAnimaListener listener, boolean isLazy) {
        mActivity = new WeakReference<>(activity);
        if(isLazy) {
            vs = activity.findViewById(parentViewResId);
        } else {
            mFrameLayout = activity.findViewById(parentViewResId);
        }
        mAnimName = animName;
        onLiveAnimaListener = listener;
    }

    @Override
    protected void onItemStart(@NonNull T bean) {
        super.onItemStart(bean);
        if (!isInit) {
            lazyInitView(vs);
            isInit = true;
        }
        if (mFrameLayout != null) {
            mFrameLayout.setVisibility(View.VISIBLE);
            mFrameLayout.removeAllViews();
        }
    }

    @Override
    protected void onItemEnd(@NonNull T bean) {
        super.onItemEnd(bean);
        if (mFrameLayout != null)
            mFrameLayout.removeAllViews();
    }

    @Override
    public void queue(T bean) {

        if (getActivity() == null)
            return;

        super.queue(bean);

        if (onLiveAnimaListener != null)
            onLiveAnimaListener.onAddAnima(this, getName(), getQueueSize());
    }

    @MainThread
    @Override
    protected boolean execute(@NonNull T bean) {
        Activity a = getActivity();
        return a != null
                && bean.isSameRoom()
                && mFrameLayout != null
                && startAniming(a, bean, mFrameLayout);
    }

    public abstract void addAnim(T animData);
    @MainThread
    protected abstract boolean startAniming(@NonNull Activity activity, @NonNull T animData, @NonNull ViewGroup parentView);
    protected void lazyInitView(ViewStub vs) {}

    @Override
    protected String getName(){
        return mAnimName;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFrameLayout != null)
            mFrameLayout.removeAllViews();
    }

    @Override
    protected void onLoadOut() {
        super.onLoadOut();
        if (onLiveAnimaListener != null)
            onLiveAnimaListener.onAnimaFinish(this, getName());
    }

    @CallSuper
    public void pauseAnim() {
        pause();
        /**
         * 不调用这个，那么动画继续播放完
         */
        notifyCurTaskEnd();
    }

    @CallSuper
    public void resumeAnim() {
        resume();
    }

    /**
     * 清除当前正在播放的动画，请清空队列
     */
    @CallSuper
    public void clearCurrentAnim() {
        reset();
        if (mFrameLayout != null)
            mFrameLayout.removeAllViews();
    }

    /**
     * 清理回收
     */
    @CallSuper
    public void clearAnim() {
        onDestroy();
        if (mFrameLayout != null)
            mFrameLayout.removeAllViews();
        if (onLiveAnimaListener != null) {
            onLiveAnimaListener = null;
        }
    }

    @CallSuper
    protected boolean onKeyboardStateChanged(boolean isKeyboardShowing){
        log("onKeyboardStateChanged:" + isKeyboardShowing);
        if (mFrameLayout != null) {
            if (isKeyboardShowing) {
                log("onKeyboardStateChanged, hide");
                mFrameLayout.setVisibility(View.INVISIBLE);
            } else if (isQueueCanRun()) {
                log("onKeyboardStateChanged, show");
                mFrameLayout.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return false;
    }

    public ILiveAnimaListener getOnLiveAnimaListener() {
        return onLiveAnimaListener;
    }

    public void setOnLiveAnimaListener(ILiveAnimaListener onLiveAnimaListener) {
        this.onLiveAnimaListener = onLiveAnimaListener;
    }

    public boolean isEmpty() {
        return isQueueEmpty();


    }

    @Nullable
    public Activity getActivity(){
        return mActivity.get();
    }


}
