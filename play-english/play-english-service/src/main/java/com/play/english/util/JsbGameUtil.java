package com.play.english.util;

import com.play.english.data.JsbGameHome;
import com.play.english.data.JsbGameRoundState;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chaiqx on 2019/6/5
 */
public class JsbGameUtil {

    private static final Map<Integer, Session> sessionMap = new HashMap<>();

    private static final Map<Integer, JsbGameHome> gameMap = new HashMap<>();

    private static final Map<Integer, Integer> userGameMap = new HashMap<>();

    private static final Map<Integer, Map<Integer, JsbGameRoundState>> roomGameMap = new HashMap<>();

    private static final Map<Integer, Integer> roundMap = new HashMap<>();

    private static final Map<Integer, Integer> homeStatusMap = new HashMap<>();

    public static Map<Integer, JsbGameHome> getGameMap() {
        return gameMap;
    }

    public static Map<Integer, Integer> getUserGameMap() {
        return userGameMap;
    }

    public static Map<Integer, Session> getSessionMap() {
        return sessionMap;
    }

    public static Map<Integer, Map<Integer, JsbGameRoundState>> getRoomGameMap() {
        return roomGameMap;
    }

    public static Map<Integer, Integer> getRoundMap() {
        return roundMap;
    }

    public static Map<Integer, Integer> getHomeStatusMap() {
        return homeStatusMap;
    }

    public static int getWinner(int p1, int p2, String p1PkStr, String p2PkStr) {
        if (p1PkStr.equals("jiandao") && p2PkStr.equals("bu")) {
            return p1;
        } else if (p1PkStr.equals("jiandao") && p2PkStr.equals("shitou")) {
            return p2;
        } else if (p1PkStr.equals("shitou") && p2PkStr.equals("jiandao")) {
            return p1;
        } else if (p1PkStr.equals("shitou") && p2PkStr.equals("bu")) {
            return p2;
        } else if (p1PkStr.equals("bu") && p2PkStr.equals("shitou")) {
            return p1;
        } else if (p1PkStr.equals("bu") && p2PkStr.equals("jiandao")) {
            return p2;
        }
        return -1;
    }
}
