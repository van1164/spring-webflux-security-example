package com.van1164.webfluxsecurityexample

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class MainController {

    @GetMapping("/")
    @ResponseBody
    fun main(@RequestParam("token",required = false) token : String?): String {
        //Need to modify the code for the token you received.
        //받은 token에 대한 코드를 수정할 필요가 있음.
        return if(token == null){
            "You are not logged in."
        } else{
            "Your JWT Token : $token"
        }

    }
}