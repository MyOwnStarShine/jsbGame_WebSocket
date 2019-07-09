package com.play.english.server.controller;

import com.play.english.data.JsbGameResult;
import com.play.english.data.Result;
import com.play.english.exception.InvalidStatusException;
import com.play.english.service.JsbActivityGameServie;
import com.play.english.service.JsbGameServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chaiqx
 */
@RestController
@RequestMapping(value = "/jsb_activity")
public class JSBActivityGameController {

    @Autowired
    private JsbActivityGameServie jsbGameServie;

    @RequestMapping(value = "/pk_what", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    public Result<Boolean> startGame(@RequestParam(value = "user_id") int userId,
                                     @RequestParam(value = "what") String what) {
        try {
            return new Result<>(jsbGameServie.pk(userId, what));
        } catch (InvalidStatusException e) {
            return new Result<>(e.getStatus().getStatus(), e.getStatus().getName());
        }
    }

    @RequestMapping(value = "/get_result", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET})
    public Result<JsbGameResult> getResult(@RequestParam(value = "user_id") int userId) {
        try {
            return new Result<>(jsbGameServie.getResult(userId));
        } catch (InvalidStatusException e) {
            return new Result<>(e.getStatus().getStatus(), e.getStatus().getName());
        }
    }
}
