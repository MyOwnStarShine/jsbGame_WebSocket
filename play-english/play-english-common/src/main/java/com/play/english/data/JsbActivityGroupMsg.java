package com.play.english.data;

/**
 * @author chaiqx on 2019/7/5
 */
public class JsbActivityGroupMsg extends JsbActivityMsg {

    private int rival;

    private int roomId;

    @Override
    public int getType() {
        return JsbActivityMsg.PLAYERS_GROUP_MSG_TYPE;
    }

    public int getRival() {
        return rival;
    }

    public void setRival(int rival) {
        this.rival = rival;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
