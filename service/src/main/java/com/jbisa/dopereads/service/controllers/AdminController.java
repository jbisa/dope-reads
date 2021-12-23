package com.jbisa.dopereads.service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin/health-check")
    public String healthCheck() {
        return "Service is healthy!";
    }
}
