package com.play.english.util;

import com.play.english.data.*;

import javax.websocket.Session;
import java.util.*;

/**
 * @author chaiqx on 2019/7/5
 */
public class JsbActivityUtil {

    private static final Map<Integer, JsbActivity> activityMap = new HashMap<>();

    private static final Map<Integer, Session> sessionMap = new HashMap<>();

    private static final Map<Integer, Map<Integer, JsbActivityRoundState>> activityRoundStateMap = new HashMap<>();

    private static final Map<Integer, Integer> roundMap = new HashMap<>();

    static {
        JsbActivity jsbActivity = new JsbActivity();
        jsbActivity.setActivityId(1);
        jsbActivity.setTotalPlayers(4);
        jsbActivity.setPlayers(null);
        activityMap.put(jsbActivity.getActivityId(), jsbActivity);
    }


    public static Map<Integer, Session> getSessionMap() {
        return sessionMap;
    }

    public static Map<Integer, JsbActivity> getActivityMap() {
        return activityMap;
    }


    public static Map<Integer, Map<Integer, JsbActivityRoundState>> getActivityRoundStateMap() {
        return activityRoundStateMap;
    }

    public static Map<Integer, Integer> getRoundMap() {
        return roundMap;
    }

    public static Map<Integer, Integer> groupPlayers(List<Integer> players) {
        Collections.shuffle(players);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < players.size() / 2; i++) {
            map.put(players.get(i), players.get(i + players.size() / 2));
            map.put(players.get(i + players.size() / 2), players.get(i));
            int roomId = -1;
            Random random = new Random();
            roomId = random.nextInt(10000);
            while (JsbGameUtil.getGameMap().containsKey(roomId)) {
                roomId = random.nextInt();
            }
            JsbGameHome jsbGameHome = new JsbGameHome();
            jsbGameHome.setId(roomId);
            JsbGameUtil.getGameMap().put(roomId, jsbGameHome);
            JsbGameUtil.getUserGameMap().put(players.get(i), roomId);
            JsbGameUtil.getUserGameMap().put(players.get(i + players.size() / 2), roomId);
            JsbGameUtil.getHomeStatusMap().put(roomId, HomeStatus.TWO_PRE.getStatus());
        }
        return map;
    }
}
