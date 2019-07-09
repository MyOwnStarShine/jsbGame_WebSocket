package com.play.english.deserializer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.play.english.data.JsbActivityGroupMsg;
import com.play.english.data.JsbActivityMsg;
import com.play.english.data.JsbActivityPreMsg;
import com.play.english.data.JsbActivityRoundResultMsg;
import com.play.english.util.JsonUtil;

/**
 * @author chaiqx on 2019/7/5
 */
public class JsbActivityMsgDeserializer extends JsonDesrializerWithType<JsbActivityMsg> {

    @Override
    protected JsbActivityMsg deserialize(ObjectNode node, int type) {
        if (type == JsbActivityMsg.PLAYERS_PRE_MSG_TYPE){
            return JsonUtil.deserialize(node.toString(), JsbActivityPreMsg.class);
        }else if (type == JsbActivityMsg.PLAYERS_GROUP_MSG_TYPE){
            return JsonUtil.deserialize(node.toString(), JsbActivityGroupMsg.class);
        }else if (type == JsbActivityMsg.PLAYERS_ROUND_MSG_TYPE){
            return JsonUtil.deserialize(node.toString(), JsbActivityRoundResultMsg.class);
        }
        return null;
    }
}
