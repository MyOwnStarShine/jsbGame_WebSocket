package com.play.english.service;

import com.play.english.data.*;
import com.play.english.exception.InvalidStatusException;
import com.play.english.util.JsbActivityUtil;
import com.play.english.util.JsbGameUtil;
import com.play.english.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * @author chaiqx on 2019/6/4
 */
@Service
public class JsbActivityGameServie {

    @Autowired
    private JsbActivityGameWebSocketService jsbActivityGameWebSocketService;

    @Autowired
    private JsbActivityWebSocketService jsbActivityWebSocketService;

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
            jsbActivityGameWebSocketService.sendMsg(jsbGameHome.getP1(), jsbGameState);
            jsbActivityGameWebSocketService.sendMsg(jsbGameHome.getP2(), jsbGameState);
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
        int round = JsbActivityUtil.getRoundMap().get(1);
        Map<Integer, JsbActivityRoundState> jsbActivityRoundStateMap = JsbActivityUtil.getActivityRoundStateMap()
                .get(1);
        JsbActivityRoundState jsbActivityRoundState = jsbActivityRoundStateMap.get(round);
        synchronized (this) {//防止并发
            List<Integer> winPlayers = jsbActivityRoundState.getWinPlayers();
            if (!winPlayers.contains(jsbGameResult.getWinner())) {//双方只有一个win
                winPlayers.add(jsbGameResult.getWinner());
                jsbActivityRoundState.setWinPlayers(winPlayers);
                jsbActivityRoundStateMap.put(round, jsbActivityRoundState);
                JsbActivityUtil.getActivityRoundStateMap().put(1, jsbActivityRoundStateMap);
                JsbActivity jsbActivity = JsbActivityUtil.getActivityMap().get(1);
                jsbActivity.getPlayers().forEach(player -> {
                    JsbActivityRoundResultMsg roundResultMsg = new JsbActivityRoundResultMsg();
                    roundResultMsg.setUserId(userId);
                    roundResultMsg.setRound(round);
                    roundResultMsg.setWinPlayers(jsbActivityRoundState.getWinPlayers());
                    jsbActivityWebSocketService.sendMsg(player, roundResultMsg);
                });
                if (round == 1 && winPlayers.size() == 2) {
                    Map<Integer, Integer> rivalMap = JsbActivityUtil.groupPlayers(winPlayers);
                    winPlayers.forEach(winPlayer -> {
                        JsbActivityGroupMsg jsbActivityGroupMsg = new JsbActivityGroupMsg();
                        JsbGameHome _jsbGameHome = JsbGameUtil.getGameMap().get(JsbGameUtil.getUserGameMap().get(winPlayer));
                        jsbActivityGroupMsg.setUserId(winPlayer);
                        jsbActivityGroupMsg.setRival(rivalMap.get(winPlayer));
                        jsbActivityGroupMsg.setRoomId(_jsbGameHome.getId());
                        jsbActivityWebSocketService.sendMsg(winPlayer, jsbActivityGroupMsg);
                    });
                    JsbActivityUtil.getRoundMap().put(1, round + 1);
                }
            }
        }
        return jsbGameResult;
    }
}