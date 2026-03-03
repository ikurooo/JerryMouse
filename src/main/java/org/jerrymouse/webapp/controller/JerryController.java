package org.jerrymouse.webapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JerryController {

    @GetMapping("/greet")
    public Map<String, String> greet(@RequestParam(name = "name", defaultValue = "Guest") String name) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, " + name + "!");
        response.put("poweredBy", "JerryMouse-Server");
        response.put("status", "Spring-Integrated");
        return response;
    }
}