package com.example.projecttest.communication.coremanager;

public interface IAppInfoCore extends IBaseCore {
    String BANNED_ALL = "0";
    String BANNED_ROOM = "1";
    String BANNED_P2P = "2";
    String BANNED_PUBLIC_ROOM = "3";

    String getSensitiveWord();
//
//    Json getBannedMap();

    void checkBanned();

    void checkBanned(boolean resetTime);

}
