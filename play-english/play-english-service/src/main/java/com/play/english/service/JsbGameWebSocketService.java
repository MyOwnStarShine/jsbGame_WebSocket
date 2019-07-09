package com.play.english.service;

import com.play.english.data.HomeStatus;
import com.play.english.data.JsbGameHome;
import com.play.english.data.Result;
import com.play.english.data.UserMsg;
import com.play.english.exception.InvalidStatusException;
import com.play.english.util.JsbGameUtil;
import com.play.english.util.JsonUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @author chaiqx
 */
@ServerEndpoint(value = "/jsb/{userId}")
@Component
public class JsbGameWebSocketService {

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        System.out.println("WebSocketService onOpen, userId = " + userId);
        int userId2 = Integer.parseInt(userId);
        JsbGameUtil.getSessionMap().put(userId2, session);
        Integer roomId = JsbGameUtil.getUserGameMap().get(userId2);
        if (roomId == null) {
            //房间不存在或者房间内玩家少了.简单都算房间不可用--todo 清理数据
            JsbGameUtil.getSessionMap().get(userId2).getAsyncRemote().sendObject(
                    new Result<>(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getStatus(),
                            InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getName())
            );
            return;
        }
        JsbGameHome gameHome = JsbGameUtil.getGameMap().get(roomId);
        if (gameHome == null || gameHome.getP1() <= 0 || gameHome.getP2() <= 0) {
            //房间不存在或者房间内玩家少了.简单都算房间不可用--todo 清理数据
            JsbGameUtil.getSessionMap().get(userId2).getAsyncRemote().sendObject(
                    new Result<>(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getStatus(),
                            InvalidStatusException.InvalidStatus.ROOM_IS_INVALID.getName())
            );
            return;
        }
        //通知两个玩家,准备开始游戏要
        JsbGameUtil.getSessionMap().get(gameHome.getP1()).getAsyncRemote().sendObject(UserMsg.PRE_START);
        JsbGameUtil.getSessionMap().get(gameHome.getP2()).getAsyncRemote().sendObject(UserMsg.PRE_START);
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        System.out.println("WebSocketService onClose: " + userId);
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
        System.out.println("WebSocketService onMessage: " + userId + ":" + msg);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.out.println("WebSocketService onMessage: " + t.getMessage());
    }

    synchronized public void sendMsg(int userId, Object object) {
        if (JsbGameUtil.getSessionMap().get(userId) == null) {
            return;
        }
        System.out.println("Jsb Game WebSocketService sendMsg: " + JsonUtil.serialize(object));
        JsbGameUtil.getSessionMap().get(userId).getAsyncRemote().sendText(JsonUtil.serialize(object));
    }
}
