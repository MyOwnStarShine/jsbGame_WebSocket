package com.play.english.data;

import java.util.List;

/**
 * @author chaiqx on 2019/7/5
 */
public class JsbActivityRoundResultMsg extends JsbActivityMsg {

    private int round;

    private List<Integer> winPlayers;

    @Override
    public int getType() {
        return JsbActivityMsg.PLAYERS_ROUND_MSG_TYPE;
    }

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
