package com.play.english.service;

import com.google.common.collect.Lists;
import com.play.english.data.*;
import com.play.english.exception.InvalidStatusException;
import com.play.english.util.JsbActivityUtil;
import com.play.english.util.JsbGameUtil;
import com.play.english.util.JsonUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chaiqx
 */
@ServerEndpoint(value = "/jsb_game/{userId}")
@Component
public class JsbActivityGameWebSocketService {

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        System.out.println("Jsb Activity Game WebSocketService onOpen, userId = " + userId);
        int userId2 = Integer.parseInt(userId);
        Integer roomId = JsbGameUtil.getUserGameMap().get(userId2);
        if (roomId == null) {
            //房间不存在或者房间内玩家少了.简单都算房间不可用--todo 清理数据
            session.getAsyncRemote().sendObject(
                    new Result<>(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getStatus(),
                            InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getName())
            );
            return;
        }
        JsbGameHome gameHome = JsbGameUtil.getGameMap().get(roomId);
        if (gameHome == null) {
            //房间不存在或者房间内玩家少了.简单都算房间不可用--todo 清理数据
            session.getAsyncRemote().sendObject(
                    new Result<>(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getStatus(),
                            InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getName())
            );
            return;
        }
        JsbGameUtil.getSessionMap().put(userId2, session);
        if (gameHome.getP1() <= 0 && gameHome.getP2() != userId2) {
            gameHome.setP1(userId2);
        }
        if (gameHome.getP2() <= 0 && gameHome.getP1() != userId2) {
            gameHome.setP2(userId2);
        }
        if (gameHome.getP1() <= 0 || gameHome.getP2() <= 0) {
            return;//房间人数还不够;
        }
        //通知两个玩家,准备开始游戏要
        JsbGameUtil.getSessionMap().get(gameHome.getP1()).getAsyncRemote().sendObject(UserMsg.START);
        JsbGameUtil.getSessionMap().get(gameHome.getP2()).getAsyncRemote().sendObject(UserMsg.START);
        int round = JsbActivityUtil.getRoundMap().getOrDefault(1, 1);
        JsbActivityUtil.getRoundMap().put(1, round);
        Map<Integer, JsbActivityRoundState> jsbActivityRoundStateMap = new HashMap<>();
        JsbActivityRoundState jsbActivityRoundState = new JsbActivityRoundState();
        jsbActivityRoundState.setRound(round);
        jsbActivityRoundState.setWinPlayers(Lists.newArrayList());
        jsbActivityRoundStateMap.put(round, jsbActivityRoundState);
        JsbActivityUtil.getActivityRoundStateMap().put(1, jsbActivityRoundStateMap);//暂时写死活动ID
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        System.out.println("Jsb Activity Game WebSocketService onClose: " + userId);
        int intUserId = Integer.parseInt(userId);
        JsbGameUtil.getSessionMap().remove(intUserId);
        Integer roomId = JsbGameUtil.getUserGameMap().get(intUserId);
        if (roomId == null || roomId <= 0) {
            return;
        }
        JsbGameHome jsbGameHome = JsbGameUtil.getGameMap().get(roomId);
        if (jsbGameHome == null) {
            return;
        }
        if (jsbGameHome.getP1() != intUserId && jsbGameHome.getP2() != intUserId) {
            return;
        }
        JsbGameUtil.getUserGameMap().remove(intUserId);
        JsbGameUtil.getRoomGameMap().remove(roomId);
        JsbGameUtil.getRoundMap().remove(roomId);
        if (jsbGameHome.getP1() == intUserId && jsbGameHome.getP2() <= 0) {
            //没人了
            JsbGameUtil.getGameMap().remove(roomId);
            JsbGameUtil.getHomeStatusMap().remove(roomId);
        } else if (jsbGameHome.getP1() == intUserId && jsbGameHome.getP2() > 0) {
            //还有一个人，让他坐P1
            jsbGameHome.setP1(jsbGameHome.getP2());
            jsbGameHome.setP2(0);
            JsbGameUtil.getGameMap().put(roomId, jsbGameHome);
            if (JsbGameUtil.getHomeStatusMap().get(roomId) >= HomeStatus.ONE_CHU.getStatus()) {
                //如果是比赛的时候逃走的
                this.sendMsg(jsbGameHome.getP1(), UserMsg.RIVAL_RUN);
                return;
            }
            JsbGameUtil.getHomeStatusMap().put(roomId, HomeStatus.NOT_PRE.getStatus());
            this.sendMsg(jsbGameHome.getP1(), UserMsg.RIVAL_LEAVE);
        } else if (jsbGameHome.getP2() == intUserId && jsbGameHome.getP1() > 0) {
            //还有一个人，让他坐P1
            jsbGameHome.setP2(0);
            JsbGameUtil.getGameMap().put(roomId, jsbGameHome);
            if (JsbGameUtil.getHomeStatusMap().get(roomId) >= HomeStatus.ONE_CHU.getStatus()) {
                //如果是比赛的时候逃走的
                this.sendMsg(jsbGameHome.getP1(), UserMsg.RIVAL_RUN);
                return;
            }
            JsbGameUtil.getHomeStatusMap().put(roomId, HomeStatus.NOT_PRE.getStatus());
            this.sendMsg(jsbGameHome.getP1(), UserMsg.RIVAL_LEAVE);
        } else if (jsbGameHome.getP2() == intUserId && jsbGameHome.getP1() <= 0) {
            JsbGameUtil.getGameMap().remove(roomId);
        }
    }

    @OnMessage
    public void onMessage(@PathParam("userId") String userId, String msg) {
        System.out.println("Jsb Activity Game WebSocketService onMessage: " + userId + ":" + msg);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.out.println("Jsb Activity Game WebSocketService onMessage: " + t.getMessage());
    }

    synchronized public void sendMsg(int userId, Object object) {
        if (JsbGameUtil.getSessionMap().get(userId) == null) {
            return;
        }
        System.out.println("Jsb Activity Game WebSocketService sendMsg: " + JsonUtil.serialize(object));
        JsbGameUtil.getSessionMap().get(userId).getAsyncRemote().sendText(JsonUtil.serialize(object));
    }
}
