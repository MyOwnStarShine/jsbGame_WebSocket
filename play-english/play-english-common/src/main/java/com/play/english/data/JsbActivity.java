package com.play.english.data;

import java.util.List;

/**
 * @author chaiqx on 2019/7/5
 */
public class JsbActivity {

    private int activityId;

    private int totalPlayers;

    private List<Integer> players;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public void setPlayers(List<Integer> players) {
        this.players = players;
    }
}
