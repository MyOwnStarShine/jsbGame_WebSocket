package com.play.english.data;

import java.util.List;

/**
 * @author chaiqx on 2019/7/5
 */
public class JsbActivityPreMsg extends JsbActivityMsg {

    private List<Integer> players;

    private boolean canPlay;

    private String msg;

    @Override
    public int getType() {
        return JsbActivityMsg.PLAYERS_PRE_MSG_TYPE;
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public void setPlayers(List<Integer> players) {
        this.players = players;
    }

    public boolean isCanPlay() {
        return canPlay;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
