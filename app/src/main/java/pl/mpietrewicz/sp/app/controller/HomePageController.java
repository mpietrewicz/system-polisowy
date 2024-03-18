package pl.mpietrewicz.sp.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePageController {

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @GetMapping("/")
    public String getActiveProfile() {
        return "SP Active Profile: " + activeProfiles;
    }

}