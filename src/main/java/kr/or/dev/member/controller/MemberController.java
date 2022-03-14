package kr.or.dev.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("login")
    public String loginView() {
        return "login/login";
    }
}
