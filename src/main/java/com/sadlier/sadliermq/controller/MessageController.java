package com.sadlier.sadliermq.controller;

import com.sadlier.sadliermq.publisher.ServiceASender;
import com.sadlier.sadliermq.service.SadlierRabbitAdmin;
import com.sadlier.sadliermq.publisher.SadlierRabbitTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author sang.le-hoang
 * @created Oct 13, 2021
 */
@RestController
public class MessageController {
    @Autowired
    private SadlierRabbitAdmin sadlierRabbitAdmin;

    @Autowired
    private ServiceASender serviceASender;

    @GetMapping("/pub-good-message")
    public String sendAGoodMessage(@RequestParam("msg") String msg) {
        return serviceASender.sendAGoodMsgCreateUser(msg);
    }

    @GetMapping("/pub-bad-message")
    public String sendAbadMessage(@RequestParam("msg") String msg) {
        return serviceASender.sendABadMsgCreateUser(msg);
    }
}
