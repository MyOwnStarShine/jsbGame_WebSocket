package com.play.english.server.controller;

import com.play.english.data.JsbGameResult;
import com.play.english.data.Result;
import com.play.english.exception.InvalidStatusException;
import com.play.english.service.JsbGameServie;
import com.play.english.util.MemCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chaiqx
 */
@RestController
@RequestMapping(value = "/test")
public class ExampleTestController {

    private Logger LOG = LoggerFactory.getLogger(ExampleTestController.class);

    private MemCache<String, String> memCache = new MemCache<>(200, 4);

    @RequestMapping(value = "/mem_cache", method = RequestMethod.GET)
    @ResponseBody
    public void testMemCache() {
        memCache.put("1", "t0");
        System.out.println(memCache.getOrPutIfAbsent("1", "t1"));
        System.out.println(memCache.getOrPutIfAbsent("2", "t2"));
        System.out.println(memCache.get("1"));
        System.out.println(memCache.get("2"));
    }
}
