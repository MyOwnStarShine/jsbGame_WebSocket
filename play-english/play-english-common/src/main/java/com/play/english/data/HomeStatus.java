package com.play.english.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chaiqx on 2019/6/12
 */
public enum HomeStatus {

    NOT_PRE(0, "两家未准备"),
    ONE_PRE(1, "一家准备"),
    TWO_PRE(2, "两家准备"),
    ONE_CHU(3, "一家出招");

    private static Map<Integer, HomeStatus> homeStatusMap = new HashMap<>();

    static {
        for (HomeStatus status : values()) {
            homeStatusMap.put(status.getStatus(), status);
        }
    }

    private int status;
    private String name;

    HomeStatus(int status, String name) {
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
    public static HomeStatus parse(int v) {
        return homeStatusMap.getOrDefault(v, NOT_PRE);
    }
}
