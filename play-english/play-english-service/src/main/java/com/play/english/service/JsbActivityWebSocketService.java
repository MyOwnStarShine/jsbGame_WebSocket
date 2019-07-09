package com.play.english.service;

import com.google.common.collect.Lists;
import com.play.english.data.JsbActivity;
import com.play.english.data.JsbActivityGroupMsg;
import com.play.english.data.JsbActivityPreMsg;
import com.play.english.data.JsbGameHome;
import com.play.english.util.JsbActivityUtil;
import com.play.english.util.JsbGameUtil;
import com.play.english.util.JsonUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;

/**
 * @author chaiqx
 */
@ServerEndpoint(value = "/jsb_activity/{userId}/{activityId}")
@Component
public class JsbActivityWebSocketService {

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, @PathParam("activityId") String activityId,
                       Session session) {
        System.out.println("Jsb Activity WebSocketService onOpen, userId = " + userId + ",activityId = " + activityId);
        int userId2 = Integer.parseInt(userId);
        int activityId2 = Integer.parseInt(activityId);
        JsbActivity jsbActivity = JsbActivityUtil.getActivityMap().get(activityId2);
        JsbActivityPreMsg jsbActivityPreMsg = new JsbActivityPreMsg();
        if (jsbActivity == null) {
            jsbActivityPreMsg.setCanPlay(false);
            jsbActivityPreMsg.setMsg("活动ID不存在啊~~");
            session.getAsyncRemote().sendText(JsonUtil.serialize(jsbActivityPreMsg));
            return;
        }
        List<Integer> players = jsbActivity.getPlayers();//有并发问题
        if (players != null && players.size() > jsbActivity.getTotalPlayers()) {
            jsbActivityPreMsg.setCanPlay(false);
            jsbActivityPreMsg.setMsg("活动参加人数已经满啦~~");
            session.getAsyncRemote().sendText(JsonUtil.serialize(jsbActivityPreMsg));
            return;
        }
        if (players != null && jsbActivity.getPlayers().contains(userId2)) {
            jsbActivityPreMsg.setCanPlay(false);
            jsbActivityPreMsg.setMsg("你已经在活动啦~~");
            session.getAsyncRemote().sendText(JsonUtil.serialize(jsbActivityPreMsg));
            return;
        }
        players = players == null ? Lists.newArrayList() : players;
        players.add(userId2);
        jsbActivity.setPlayers(players);
        JsbActivityUtil.getActivityMap().put(jsbActivity.getActivityId(), jsbActivity);
        JsbActivityUtil.getSessionMap().put(userId2, session);
        jsbActivityPreMsg.setCanPlay(true);
        jsbActivityPreMsg.setPlayers(players);
        players.forEach(player -> {
            JsbActivityUtil.getSessionMap().get(player).getAsyncRemote().sendText(JsonUtil.serialize(jsbActivityPreMsg));
        });
        if (players.size() == 4) {
            Map<Integer, Integer> rivalMap = JsbActivityUtil.groupPlayers(players);
            players.forEach(player -> {
                JsbActivityGroupMsg jsbActivityGroupMsg = new JsbActivityGroupMsg();
                jsbActivityGroupMsg.setUserId(player);
                JsbGameHome jsbGameHome = JsbGameUtil.getGameMap().get(JsbGameUtil.getUserGameMap().get(player));
                jsbActivityGroupMsg.setUserId(player);
                jsbActivityGroupMsg.setRival(rivalMap.get(player));
                jsbActivityGroupMsg.setRoomId(jsbGameHome.getId());
                JsbActivityUtil.getSessionMap().get(player).getAsyncRemote().sendText(JsonUtil.serialize(jsbActivityGroupMsg));
            });
        }
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        System.out.println("Jsb Activity WebSocketService onClose: " + userId);
        int intUserId = Integer.parseInt(userId);
        JsbActivityUtil.getSessionMap().remove(intUserId);
    }

    @OnMessage
    public void onMessage(@PathParam("userId") String userId, String msg) {
        System.out.println("Jsb Activity WebSocketService onMessage: " + userId + ":" + msg);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.out.println("Jsb Activity WebSocketService onMessage: " + t.getMessage());
    }

    synchronized public void sendMsg(int userId, Object object) {
        if (JsbActivityUtil.getSessionMap().get(userId) == null) {
            return;
        }
        System.out.println("Jsb Activity WebSocketService sendMsg: " + JsonUtil.serialize(object));
        JsbActivityUtil.getSessionMap().get(userId).getAsyncRemote().sendText(JsonUtil.serialize(object));
    }
}
