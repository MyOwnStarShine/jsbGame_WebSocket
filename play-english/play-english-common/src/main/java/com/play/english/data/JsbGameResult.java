package com.play.english.data;

/**
 * @author chaiqx on 2019/6/10
 */
public class JsbGameResult {

    private int winner;

    private int pingCnt;

    private int myWinCnt;

    private int rivalWinCnt;

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getPingCnt() {
        return pingCnt;
    }

    public void setPingCnt(int pingCnt) {
        this.pingCnt = pingCnt;
    }

    public int getMyWinCnt() {
        return myWinCnt;
    }

    public void setMyWinCnt(int myWinCnt) {
        this.myWinCnt = myWinCnt;
    }

    public int getRivalWinCnt() {
        return rivalWinCnt;
    }

    public void setRivalWinCnt(int rivalWinCnt) {
        this.rivalWinCnt = rivalWinCnt;
    }
}
