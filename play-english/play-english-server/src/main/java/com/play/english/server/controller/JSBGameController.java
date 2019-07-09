package com.play.english.server.controller;

import com.play.english.data.JsbGameResult;
import com.play.english.data.Result;
import com.play.english.exception.InvalidStatusException;
import com.play.english.service.JsbGameServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chaiqx
 */
@RestController
@RequestMapping(value = "/jsb")
public class JSBGameController {

    @Autowired
    private JsbGameServie jsbGameServie;

    @RequestMapping(value = "/new_home", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    public Result<Integer> newHome(@RequestParam(value = "user_id") int userId) {
        try {
            return new Result<>(jsbGameServie.newHome(userId));
        } catch (InvalidStatusException e) {
            return new Result<>(e.getStatus().getStatus(), e.getStatus().getName());
        }
    }

    @RequestMapping(value = "/in_home", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    public Result<Boolean> inHome(@RequestParam(value = "user_id") int userId,
                                  @RequestParam(value = "room_id") int roomId) {
        try {
            return new Result<>(jsbGameServie.inRoom(userId, roomId));
        } catch (InvalidStatusException e) {
            return new Result<>(e.getStatus().getStatus(), e.getStatus().getName());
        }
    }

    @RequestMapping(value = "/start_game", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    public Result<Boolean> startGame(@RequestParam(value = "user_id") int userId) {
        try {
            return new Result<>(jsbGameServie.startGame(userId));
        } catch (InvalidStatusException e) {
            return new Result<>(e.getStatus().getStatus(), e.getStatus().getName());
        }
    }

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

    @RequestMapping(value = "/leave_room", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    public Result<Boolean> leaveRoom(@RequestParam(value = "user_id") int userId) {
        try {
            return new Result<>(jsbGameServie.leaveRoom(userId));
        } catch (InvalidStatusException e) {
            return new Result<>(e.getStatus().getStatus(), e.getStatus().getName());
        }
    }
}
