package com.example.projecttest.scheduler;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projecttest.base.HandlerHolder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by rgy on 2021/12/23 0023.
 * 队列执行器
 */
public abstract class QueueExecutor<T> extends HandlerHolder {

    public static final String TAG =  QueueExecutor.class.getSimpleName();

    /**
     * 闲置，队列为空，无任务执行
     */
    private static final int STATE_IDLE = 0;
    /**
     * 上个动画结束，等待下个动画开始
     */
    private static final int STATE_END = 1;
    /**
     * 暂停
     */
    private static final int STATE_PAUSE = 2;
    /**
     * 任务运行中
     */
    private static final int STATE_RUNNING = 3;
    /**
     * 阻塞中
     */
    private static final int STATE_BLOCK = 4;

    private static final int MSG_NEXT = 1;
    private static final int MSG_ADD = 2;
    private static final int MSG_END = 3;
    private static final int MSG_PAUSE = 4;
    private static final int MSG_RESUME = 5;
    /**
     * 调用这个清保证任务已清空，并且如果继续有消息会进行处理（比如动画会覆盖当前没有清掉的动画）
     */
    private static final int MSG_RESET = 6;

    /**
     * 队列
     */
    @NonNull
    private final Queue<T> mQueue = new LinkedList<>();

    private int mState = STATE_IDLE;
    /**
     * 当isCanRun为false时是否阻塞
     */
    private boolean isBlockWhenCantRun = false;
    /**
     * 阻塞时开启守护线程，每半分钟检查一次
     */
    private final long BLOCK_DELAY = 30 * 1000;
    /**
     * 执行超时
     */
    private long mTimeout = 0;// 5 * 60 * 1000;
    private boolean isDaemonWhenBlock = true;
    private Runnable mDaemonRunnable;
    @NonNull
    private Map<T, Runnable> timeoutMap = new HashMap<>();

    @Nullable
    private T mCurTask;

    /**
     * 尽可能在主线程协同
     * @param msg
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean handleMessage(@NonNull Message msg) {

        switch (msg.what){
            case MSG_ADD:
                try {
                    T o = (T) msg.obj;
                    if (o != null) {
                        beforeAddList((LinkedList<T>) mQueue, o);
                        log(o, "add");
                        int index = -1;
                        if (o instanceof QueuePriority && !isQueueEmpty()) {
                            try {
                                /**
                                 * 越小越靠前
                                 */
                                List<T> list = (List<T>) mQueue;
                                for (int i = 0; i < list.size(); i++) {
                                    if (((QueuePriority) o).queueLevel() < ((QueuePriority) list.get(i)).queueLevel()) {
                                        log(o, "is more important than------->");
                                        log(list.get(i), "less important one,index=" + i);
                                        index = i;
                                        break;
                                    }
                                }
                                if (index > -1)
                                    list.add(index, o);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if (index < 0)
                            mQueue.offer(o);

                        checkSendNext();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case MSG_NEXT:

                T bean = peekNotNull();

                if (bean != null) {

                    if (timeoutMap.isEmpty() && !isStatePaused()) {

                        boolean isCanRun = isCanRun(bean);
                        log(bean, "isCanRun()=" + isCanRun);

                        if (mQueue.size() > 1
                                && !isBlockWhenCantRun
                                && !isCanRun){
                            try {
                                List<T> list = (List<T>) mQueue;
                                for (int i = 1; i < list.size(); i++) {
                                    T obj = list.get(i);
                                    if (isCanRun(obj)){
                                        list.remove(obj);
                                        list.add(0, obj);
                                        log(obj, "changed this obj position " + i + " as isCanRun");
                                        log(bean, "is can't run");
                                        break;
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            bean = peekNotNull();
                            if (bean == null) {
                                log("queue is empty, isBlockWhenCantRun=" + isBlockWhenCantRun);
                                setState(STATE_IDLE);
                                break;
                            }
                        }

                        isCanRun = isCanRun(bean);
                        log(bean, "isCanRun()=" + isCanRun);

                        if (isCanRun) {
                            mCurTask = mQueue.poll();
                            setState(STATE_RUNNING);
                            onItemStart(bean);
                            boolean isExecute = false;
                            try {
                                log(mCurTask, "running this object");
                                isExecute = execute(bean);
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            if (isExecute) {
                                /**
                                 * 超时处理，同时也是记录当前task是否结束
                                 */
                                final T finalBean = bean;
                                Runnable runnable = null;
                                if (mTimeout > 0) {
                                    postDelayed(runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            log(finalBean, "time out runnable execute");
                                            notifyTaskEnd(finalBean);
                                            onTimeout(finalBean);
                                        }
                                    }, mTimeout);
                                }
                                timeoutMap.put(finalBean, runnable);
                            } else {
                                log(mCurTask, "running this object fail");
                                notifyTaskEnd(bean);
                            }
                        } else {
                            log("nothing is canRun change block state!");
                            setState(STATE_BLOCK);
                        }
                    }
                } else {
                    log("queue is empty");
                    setState(STATE_IDLE);
                }
                break;
            case MSG_END:
                final T key = (T)msg.obj;
                log(key, "end key=" + key);
                if (mCurTask == key && mCurTask != null) {
                    log(key, "MSG_END");
                    onItemEnd(mCurTask);
                    if (!isStatePaused()) {
                        log(key, "is not state paused, change state=end");
                        setState(STATE_END);
                    }
                }

                if (timeoutMap.containsKey(key)) {
                    log(key, "contain this key");
                    Runnable r = timeoutMap.get(key);
                    timeoutMap.remove(key);
                    if (r != null) {
                        removeCallbacks(r);
                        log(key, "remove runnable");
                    }
                }
                if (!isStatePaused())
                    checkSendNext();
                break;
            case MSG_PAUSE:
                if (!isStatePaused()) {
                    log("pause it");
                    setState(STATE_PAUSE);
                    onPause();
                }
                break;
            case MSG_RESUME:
                if (isStatePaused()) {
                    log("resume it");
                    setState(STATE_BLOCK);
                    checkSendNext();
                    onResume();
                }
                break;
            case MSG_RESET:
                log("reset it");
                removeCallbacksAndMessages(null);
                resetQueue();
                setState(STATE_IDLE);
                break;
        }

        return super.handleMessage(msg);
    }

    private void checkSendNext(){
        log("checkSendNext");
        if (!hasMessages(MSG_NEXT))
            sendEmptyMessage(MSG_NEXT);
    }

    @Nullable
    private T peekNotNull(){
        T bean = mQueue.peek();

        while (!mQueue.isEmpty() && bean == null){
            mQueue.poll();
            bean = mQueue.peek();
        }

        return bean;
    }

    /**
     * 执行
     * @param bean
     * @return
     */
    @MainThread
    protected abstract boolean execute(@NonNull T bean);

    /**
     * 当前对象是否可执行
     * @param bean
     * @return
     */
    protected boolean isCanRun(@NonNull T bean){
        return true;
    }

    /**
     * 添加一个任务
     * @param bean
     */
    public void queue(T bean){
        try {
            obtainMessage(MSG_ADD, bean).sendToTarget();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加个item到list之前
     * @param mQueue
     * @param item
     */
    public void beforeAddList(@NonNull LinkedList<T> mQueue, @NonNull T item){
        log(item, "beforeAddList");
    }

    /**
     * 通知执行结束，必须明确结束的task
     * @param task 结束的task
     */
    public void notifyTaskEnd(T task){
        try {
            log(task, "notifyTaskEnd");
            obtainMessage(MSG_END, task).sendToTarget();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通知当前任务结束
     */
    public void notifyCurTaskEnd(){
        log(mCurTask, "notifyCurTaskEnd");
        notifyTaskEnd(mCurTask);
    }

    /**
     * 通知下一个，只有空闲及没有暂停情况下才会执行
     */
    public void notifyNextIfIdle(){
        log("notifyNextIfIdle");
        checkSendNext();
    }

    /**
     * 清空队列
     */
    public void reset(){
        sendEmptyMessage(MSG_RESET);
    }

    /**
     * 某一项开始时
     * @param bean
     */
    @CallSuper
    @MainThread
    protected void onItemStart(@NonNull T bean){
        log(bean, "onItemStart");
    }

    @CallSuper
    @MainThread
    protected void onItemEnd(@NonNull T bean){
        mCurTask = null;
        log(bean, "onItemEnd");
    }

    @CallSuper
    @MainThread
    protected void onTimeout(T bean){
        log(bean, "onTimeout");
    }

    public void pause(){
        sendEmptyMessage(MSG_PAUSE);
    }

    public void resume(){
        sendEmptyMessage(MSG_RESUME);
    }

    /**
     * 触发暂停
     */
    @CallSuper
    @MainThread
    protected void onPause(){

    }

    /**
     * 触发恢复
     */
    @CallSuper
    @MainThread
    protected void onResume(){

    }

    /**
     * 全部处理完毕，不包括block状态
     */
    @CallSuper
    @MainThread
    protected void onLoadOut(){
        log("onLoadOut");
    }

    /**
     * 是否有东西在执行
     * @return
     */
    public boolean isExecuting(){
        return !timeoutMap.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public boolean isQueueCanRun(){
        if (!isQueueEmpty()){
            try {
                List<T> list = (List<T>) mQueue;
                for (int i = 0; i < list.size(); i++) {
                    if (isCanRun(list.get(i)))
                        return true;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return isExecuting();
    }

    public boolean isStateIdle(){
        return mState == STATE_IDLE;
    }

    public boolean isStateRunning(){
        return mState == STATE_RUNNING;
    }

    public boolean isStatePaused(){
        return mState == STATE_PAUSE;
    }

    public boolean isStateBlocking(){
        return mState == STATE_BLOCK;
    }

    public boolean isStateEnd(){
        return mState == STATE_END;
    }

    public boolean hasNext(){
        boolean hasNext = hasMessages(MSG_ADD) || !mQueue.isEmpty();
        log("hasNext=" + hasNext);
        return hasNext;
    }

    @MainThread
    @CallSuper
    protected void onStateChange(int pre, int now){
        log("onStateChange, pre=" + pre + ",now=" + now);

        if (isDaemonWhenBlock && isStateBlocking()){

            if (mDaemonRunnable == null) {
                log( "post daemon, time=" + BLOCK_DELAY);

                postDelayed(mDaemonRunnable = new Runnable() {
                    @Override
                    public void run() {
                        postDelayed(this, BLOCK_DELAY);
                        log("daemon run !");
                        checkSendNext();
                    }
                }, BLOCK_DELAY);
            }

        } else if (mDaemonRunnable != null){
            log( "remove daemon");
            removeCallbacks(mDaemonRunnable);
            mDaemonRunnable = null;
        }

        if (isStateIdle()){
            onLoadOut();
        }
    }

    public boolean isBlockWhenCantRun() {
        return isBlockWhenCantRun;
    }

    public void setBlockWhenCantRun(boolean blockWhenCantRun) {
        isBlockWhenCantRun = blockWhenCantRun;
    }

    public boolean isDaemonWhenBlock() {
        return isDaemonWhenBlock;
    }

    public void setDaemonWhenBlock(boolean daemonWhenBlock) {
        isDaemonWhenBlock = daemonWhenBlock;
    }

    public long getTimeout() {
        return mTimeout;
    }

    public void setTimeout(long timeout){
        this.mTimeout = timeout;
//        if (mTimeout <= 0)
//            throw new IllegalArgumentException("set timeout > 0");
    }

    public int getQueueSize(){
        return mQueue.size();
    }

    public boolean isQueueEmpty(){
        return getQueueSize() <= 0;
    }

    @Nullable
    public T getCurTask(){
        return mCurTask;
    }

    @MainThread
    private void setState(int state){
        if (mState != state){
            int pre = mState;
            mState = state;
            onStateChange(pre, mState);
        }
    }

    /**
     * 清空
     */
    private void resetQueue(){
        mQueue.clear();
        timeoutMap.clear();
    }

    protected abstract String getName();

    protected void log(String msg){
        log(null, msg);
    }

    protected void log(T bean, String msg){
        Log.i(TAG + "_" + getName(), msg);
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
        resetQueue();
    }

    /**
     * 优先级，越大越靠前
     */
    public interface QueuePriority{
        int queueLevel();
    }
}
