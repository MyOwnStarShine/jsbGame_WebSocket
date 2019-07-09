package com.play.english.data;

/**
 * @author chaiqx on 2019/6/10
 */
public class JsbGameRoundState {

    private int round = 1;

    private int winner = -1;

    private int p1;

    private int p2;

    private String p1Pk = "";

    private String p2Pk = "";

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public String getP1Pk() {
        return p1Pk;
    }

    public void setP1Pk(String p1Pk) {
        this.p1Pk = p1Pk;
    }

    public String getP2Pk() {
        return p2Pk;
    }

    public void setP2Pk(String p2Pk) {
        this.p2Pk = p2Pk;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }
}
