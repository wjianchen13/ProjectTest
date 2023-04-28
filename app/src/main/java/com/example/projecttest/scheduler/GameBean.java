package com.example.projecttest.scheduler;

/**
 *
 */
public class GameBean extends QueueAnimBean {

    private String userHead;
    private String content;
    private String gameHead;

    public GameBean() {
    }

    public GameBean(String userHead, String content, String gameHead) {
        this.userHead = userHead;
        this.content = content;
        this.gameHead = gameHead;
    }

    @Override
    public String toString() {
        return "WSBaseBroatcastBean{" +

                '}';
    }
}
