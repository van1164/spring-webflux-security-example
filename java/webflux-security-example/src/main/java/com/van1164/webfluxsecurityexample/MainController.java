package com.van1164.webfluxsecurityexample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainPage(@RequestParam(value = "token", required = false) String token) {
        //Need to modify the code for the token you received.
        //받은 token에 대한 코드를 수정할 필요가 있음.
        return (token == null) ? "You are not logged in." : "Your JWT Token : " + token;
    }
}