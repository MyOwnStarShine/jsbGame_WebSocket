package com.play.english.data;

import java.util.List;

/**
 * @author chaiqx on 2019/7/8
 */
public class JsbActivityRoundState {

    private int round = 1;

    private List<Integer> winPlayers;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<Integer> getWinPlayers() {
        return winPlayers;
    }

    public void setWinPlayers(List<Integer> winPlayers) {
        this.winPlayers = winPlayers;
    }
}
