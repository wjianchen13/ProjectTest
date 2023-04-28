package com.example.projecttest.scheduler;

/**
 * Created by rgy on 2022/4/28 0028.
 * 队列动画基类，主要用于直播间礼物动画，进场动画等队列
 */
public class QueueAnimBean {

    private static final String TAG = QueueAnimBean.class.getSimpleName();

    /**
     * 用于记录这个类创建时所在的直播间
     * 当直播间切换时不应该继续执行
     */
    private int roomId;

    public QueueAnimBean(){

    }

    /**
     * 是否在同一个直播间
     * @return
     */
    public boolean isSameRoom(){
        return true;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void removeRoomId(){
        setRoomId(0);
    }
}
