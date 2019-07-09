package com.play.english.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.play.english.deserializer.JsbActivityMsgDeserializer;

/**
 * @author chaiqx on 2019/7/5
 */
@JsonDeserialize(using = JsbActivityMsgDeserializer.class)
public abstract class JsbActivityMsg implements IType{

    public static int PLAYERS_PRE_MSG_TYPE = 1;

    public static int PLAYERS_GROUP_MSG_TYPE = 2;

    public static int PLAYERS_ROUND_MSG_TYPE = 3;

    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
