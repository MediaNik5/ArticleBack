package org.catblocks.articleback.controller;

import org.catblocks.articleback.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AController{
    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal UserPrincipal userPrincipal){
        return userPrincipal.getName();
    }
}
