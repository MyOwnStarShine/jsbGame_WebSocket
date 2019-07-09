
package com.play.english.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 无效状态异常
 *
 * @author chaiqx
 */
public class InvalidStatusException extends Exception {

    public enum InvalidStatus {

        HAS_IN_GAME(-2, "已经在房间中,请先退出游戏"),
        ROOM_IS_INVALID(-3, "房间不可用"),
        ROOM_IS_FULL(-4, "房间已满"),
        NOT_IN_ROOM(-5, "您不在该房间中，踹飞你."),
        GAME_IS_NOT_END(-6, "游戏未结束啊，别着急.");

        private static Map<Integer, InvalidStatus> STATUS_TYPE_MAP = new HashMap();

        static {
            for (InvalidStatus status : values()) {
                STATUS_TYPE_MAP.put(status.getStatus(), status);
            }
        }

        private int status;
        private String name;

        InvalidStatus(int status, String name) {
            this.status = status;
            this.name = name;
        }

        @JsonValue
        public int getStatus() {
            return status;
        }

        public String getName() {
            return name;
        }

        @JsonCreator
        public static InvalidStatus parse(int v) {
            return STATUS_TYPE_MAP.get(v);
        }
    }

    private InvalidStatus status;

    public InvalidStatusException(InvalidStatus status) {
        super(status.getStatus() + "-" + status.getName());
        this.status = status;
    }

    public InvalidStatus getStatus() {
        return status;
    }
}

