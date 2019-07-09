package com.play.english.service;

import com.play.english.data.*;
import com.play.english.exception.InvalidStatusException;
import com.play.english.util.JsbGameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * @author chaiqx on 2019/6/4
 */
@Service
public class JsbGameServie {

    @Autowired
    private JsbGameWebSocketService jsbGameWebSocketService;

    public int newHome(int userId) throws InvalidStatusException {
        if (JsbGameUtil.getUserGameMap().containsKey(userId)) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.HAS_IN_GAME);
        }
        int roomId = -1;
        Random random = new Random();
        roomId = random.nextInt(10000);
        while (JsbGameUtil.getGameMap().containsKey(roomId)) {
            roomId = random.nextInt();
        }
        JsbGameHome jsbGameHome = new JsbGameHome();
        jsbGameHome.setId(roomId);
        jsbGameHome.setP1(userId);
        JsbGameUtil.getGameMap().put(roomId, jsbGameHome);
        JsbGameUtil.getUserGameMap().put(userId, roomId);
        return roomId;
    }

    public boolean inRoom(int userId, int roomId) throws InvalidStatusException {
        if (JsbGameUtil.getUserGameMap().containsKey(userId)) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.HAS_IN_GAME);
        }
        JsbGameHome jsbGameHome = JsbGameUtil.getGameMap().get(roomId);
        if (jsbGameHome == null || jsbGameHome.getP1() <= 0) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID);
        }
        if (jsbGameHome.getP2() > 0) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.ROOM_IS_FULL);
        }
        jsbGameHome.setP2(userId);
        JsbGameUtil.getGameMap().put(roomId, jsbGameHome);
        JsbGameUtil.getUserGameMap().put(userId, roomId);
        return true;
    }

    public boolean startGame(int userId) throws InvalidStatusException {
        Integer roomId = JsbGameUtil.getUserGameMap().get(userId);
        if (roomId == null || roomId <= 0) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        JsbGameHome jsbGameHome = JsbGameUtil.getGameMap().get(roomId);
        if (jsbGameHome == null) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID);
        }
        if (jsbGameHome.getP1() != userId && jsbGameHome.getP2() != userId) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        int status = JsbGameUtil.getHomeStatusMap().getOrDefault(roomId, 0);
        if (status == HomeStatus.TWO_PRE.getStatus()) {
            JsbGameUtil.getRoomGameMap().remove(roomId);
            JsbGameUtil.getRoundMap().remove(roomId);
            JsbGameUtil.getHomeStatusMap().remove(roomId);
            status = HomeStatus.NOT_PRE.getStatus();
        }
        if (status == HomeStatus.NOT_PRE.getStatus()) {
            status = HomeStatus.ONE_PRE.getStatus();//1个玩家准备
            JsbGameUtil.getHomeStatusMap().put(roomId, status);
            return true;
        } else if (status == HomeStatus.ONE_PRE.getStatus()) {
            status = HomeStatus.TWO_PRE.getStatus();//2个玩家准备
            JsbGameUtil.getHomeStatusMap().put(roomId, status);
            jsbGameWebSocketService.sendMsg(jsbGameHome.getP1(), UserMsg.START);
            jsbGameWebSocketService.sendMsg(jsbGameHome.getP2(), UserMsg.START);
        }
        return true;
    }

    public boolean pk(int userId, String pkWhat) throws InvalidStatusException {
        Integer roomId = JsbGameUtil.getUserGameMap().get(userId);
        if (roomId == null || roomId <= 0) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        JsbGameHome jsbGameHome = JsbGameUtil.getGameMap().get(roomId);
        if (jsbGameHome == null) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID);
        }
        if (jsbGameHome.getP1() != userId && jsbGameHome.getP2() != userId) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        Map<Integer, JsbGameRoundState> jsbGameRoundStateMap = JsbGameUtil.getRoomGameMap().
                getOrDefault(roomId, new HashMap<>());
        int round = JsbGameUtil.getRoundMap().getOrDefault(roomId, 1);
        JsbGameRoundState jsbGameState = new JsbGameRoundState();
        jsbGameState = jsbGameRoundStateMap.getOrDefault(round, jsbGameState);
        int status = JsbGameUtil.getHomeStatusMap().getOrDefault(roomId, 0);
        if (status < HomeStatus.TWO_PRE.getStatus()) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID);
        }
        if (status == HomeStatus.TWO_PRE.getStatus()) {
            status = HomeStatus.ONE_CHU.getStatus();//1个玩家已经出招
            if (jsbGameHome.getP1() == userId) {
                jsbGameState.setP1(userId);
                jsbGameState.setP1Pk(pkWhat);
            } else {
                jsbGameState.setP2(userId);
                jsbGameState.setP2Pk(pkWhat);
            }
            jsbGameState.setRound(round);
            JsbGameUtil.getHomeStatusMap().put(roomId, status);
            jsbGameRoundStateMap.put(round, jsbGameState);
            JsbGameUtil.getRoomGameMap().put(roomId, jsbGameRoundStateMap);
        } else if (status == HomeStatus.ONE_CHU.getStatus()) {//2个玩家已经出招
            if (jsbGameHome.getP1() == userId) {
                jsbGameState.setP1(userId);
                jsbGameState.setP1Pk(pkWhat);
            } else {
                jsbGameState.setP2(userId);
                jsbGameState.setP2Pk(pkWhat);
            }
            jsbGameState.setRound(round);
            int winner = JsbGameUtil.getWinner(jsbGameState.getP1(), jsbGameState.getP2(),
                    jsbGameState.getP1Pk(), jsbGameState.getP2Pk());
            System.out.println("该轮对决胜者是：" + winner);
            jsbGameState.setWinner(winner);
            jsbGameRoundStateMap.put(round, jsbGameState);
            JsbGameUtil.getRoomGameMap().put(roomId, jsbGameRoundStateMap);
            jsbGameWebSocketService.sendMsg(jsbGameHome.getP1(), jsbGameState);
            jsbGameWebSocketService.sendMsg(jsbGameHome.getP2(), jsbGameState);
            JsbGameUtil.getRoundMap().put(roomId, round + 1);
            status = HomeStatus.TWO_PRE.getStatus();//重新准备状态
            JsbGameUtil.getHomeStatusMap().put(roomId, status);
        }
        return true;
    }

    public JsbGameResult getResult(int userId) throws InvalidStatusException {
        Integer roomId = JsbGameUtil.getUserGameMap().get(userId);
        if (roomId == null || roomId <= 0) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        JsbGameHome jsbGameHome = JsbGameUtil.getGameMap().get(roomId);
        if (jsbGameHome == null) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID);
        }
        if (jsbGameHome.getP1() != userId && jsbGameHome.getP2() != userId) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        int status = JsbGameUtil.getHomeStatusMap().getOrDefault(roomId, 0);
        JsbGameResult jsbGameResult = new JsbGameResult();
        if (jsbGameHome.getP1() > 0 && jsbGameHome.getP2() <= 0 && status >= HomeStatus.ONE_CHU.getStatus()) {
            //有一家逃出了游戏,直接返回赢的对面选手
            jsbGameResult.setWinner(userId);
            JsbGameUtil.getHomeStatusMap().put(roomId, HomeStatus.NOT_PRE.getStatus());
            return jsbGameResult;
        }
        Map<Integer, JsbGameRoundState> jsbGameRoundStateMap = JsbGameUtil.getRoomGameMap()
                .getOrDefault(roomId, new HashMap<>());
        if (jsbGameRoundStateMap.isEmpty()) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.GAME_IS_NOT_END);
        }
        int p1WinCnt = 0;
        int p2WinCnt = 0;
        for (JsbGameRoundState jsbGameRoundState : jsbGameRoundStateMap.values()) {
            if (jsbGameRoundState.getWinner() == -1) {
                jsbGameResult.setPingCnt(jsbGameResult.getPingCnt() + 1);
            } else if (jsbGameRoundState.getWinner() == userId) {
                jsbGameResult.setMyWinCnt(jsbGameResult.getMyWinCnt() + 1);
            } else {
                jsbGameResult.setRivalWinCnt(jsbGameResult.getRivalWinCnt() + 1);
            }
            if (jsbGameRoundState.getWinner() == jsbGameRoundState.getP1()) {
                p1WinCnt++;
            } else {
                p2WinCnt++;
            }
        }
        if (p1WinCnt > p2WinCnt) {
            jsbGameResult.setWinner(jsbGameHome.getP1());
        } else if (p1WinCnt < p2WinCnt) {
            jsbGameResult.setWinner(jsbGameHome.getP2());
        } else {
            jsbGameResult.setWinner(-1);
        }
        JsbGameUtil.getHomeStatusMap().put(roomId, HomeStatus.NOT_PRE.getStatus());
        return jsbGameResult;
    }

    public boolean leaveRoom(int userId) throws InvalidStatusException {
        Integer roomId = JsbGameUtil.getUserGameMap().get(userId);
        if (roomId == null || roomId <= 0) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        JsbGameHome jsbGameHome = JsbGameUtil.getGameMap().get(roomId);
        if (jsbGameHome == null) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.ROOM_IS_INVALID);
        }
        if (jsbGameHome.getP1() != userId && jsbGameHome.getP2() != userId) {
            throw new InvalidStatusException(InvalidStatusException.InvalidStatus.NOT_IN_ROOM);
        }
        JsbGameUtil.getUserGameMap().remove(userId);
        JsbGameUtil.getRoomGameMap().remove(roomId);
        JsbGameUtil.getRoundMap().remove(roomId);
        if (jsbGameHome.getP1() == userId && jsbGameHome.getP2() <= 0) {
            //没人了
            JsbGameUtil.getGameMap().remove(roomId);
            JsbGameUtil.getHomeStatusMap().remove(roomId);
        } else if (jsbGameHome.getP1() == userId && jsbGameHome.getP2() > 0) {
            //还有一个人，让他坐P1
            jsbGameHome.setP1(jsbGameHome.getP2());
            jsbGameHome.setP2(0);
            JsbGameUtil.getGameMap().put(roomId, jsbGameHome);
            JsbGameUtil.getHomeStatusMap().put(roomId, HomeStatus.NOT_PRE.getStatus());
            jsbGameWebSocketService.sendMsg(jsbGameHome.getP1(), UserMsg.RIVAL_LEAVE);
        } else if (jsbGameHome.getP2() == userId && jsbGameHome.getP1() > 0) {
            //还有一个人，让他坐P1
            jsbGameHome.setP2(0);
            JsbGameUtil.getGameMap().put(roomId, jsbGameHome);
            JsbGameUtil.getHomeStatusMap().put(roomId, HomeStatus.NOT_PRE.getStatus());
            jsbGameWebSocketService.sendMsg(jsbGameHome.getP1(), UserMsg.RIVAL_LEAVE);
        } else if (jsbGameHome.getP2() == userId && jsbGameHome.getP1() <= 0) {
            JsbGameUtil.getGameMap().remove(roomId);
        }
        return true;
    }
}