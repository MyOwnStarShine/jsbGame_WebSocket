package com.play.english.skiplist;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author chaiqx on 2019/7/3
 */
public class SkipList {

    public static void main(String[] args){
        Map<String, String> map = new ConcurrentSkipListMap<>();
        map.put("2","2");
        map.put("1","1");
        System.out.println(map);
        Set<String> set = new ConcurrentSkipListSet<>();
        set.add("2");
        set.add("1");
        System.out.println(set);
    }
}
