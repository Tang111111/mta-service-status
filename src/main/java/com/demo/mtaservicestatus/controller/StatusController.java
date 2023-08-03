package com.demo.mtaservicestatus.controller;

import com.demo.mtaservicestatus.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class StatusController {
    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/status/{route}")
    public String getStatusByRoute(@PathVariable String route){
        String response = statusService.getStatusByRouteName(route);
        if (response.equals("NOT FOUND")) {
            return "Line not found";
        }
        return "The status of line " + route + " is " + response;
    }

    @GetMapping("/uptime/{route}")
    public String getUptimeByRoute(@PathVariable String route){
        double uptime = statusService.getUptimeByRouteName(route);
        if (uptime == -1.0) {
            return "Line not found";
        }
        return "The uptime rate of line " + route + " is " + uptime;
    }

}
