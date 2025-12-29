package com.example.difyintegration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "index"; // This will map to src/main/resources/templates/index.html
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat"; // This will map to src/main/resources/templates/chat.html
    }

    @GetMapping("/config")
    public String config() {
        return "config"; // This will map to src/main/resources/templates/config.html
    }
}