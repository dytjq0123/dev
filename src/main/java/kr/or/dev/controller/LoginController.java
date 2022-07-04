package kr.or.dev.controller;

import kr.or.dev.dto.member.MemberSignUpDto;
import kr.or.dev.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final MemberService memberService;
    private final PasswordEncoder encoder;

    @GetMapping("/view")
    public String loginView(@RequestParam(value = "pro_no", defaultValue = "0") int pro_no,
                            @RequestParam(value = "mem_nick", required = false) String mem_nick,
                            Model model) {

        if(pro_no != 0){
            model.addAttribute("pro_no", pro_no);
            model.addAttribute("mem_nick", mem_nick);
        }
        return "login";
    }

    @PostMapping("/idChk")
    @ResponseBody
    public int idChk(@RequestParam("mem_id") String mem_id) {
        int result = memberService.chMemId(mem_id);
        return result;
    }

    @PostMapping("/nickChk")
    @ResponseBody
    public int nickChk(@RequestParam("mem_nick") String mem_nick) {
        int result = memberService.chkMemNick(mem_nick);
        return result;
    }

    @Value("${profileImg.path}")
    private String profileUploadFolder;

    @PostMapping("/signup")
    @ResponseBody
    public int signUp(MemberSignUpDto memberSignUpDto,
                      @RequestParam("mem_pic") MultipartFile mem_pic) throws IllegalStateException, IOException {

        int result = memberService.save(memberSignUpDto, mem_pic);

        return result;
    }

    @PostMapping("/findId")
    @ResponseBody
    public Map findId(@RequestParam("mem_name") String mem_name,
                      @RequestParam("mem_phone") String mem_phone) {

        Map result = new HashMap();
        result.put("mem_id", memberService.findId(mem_name, mem_phone));

        return result;
    }


    @PostMapping("/memChk")
    @ResponseBody
    public Map memChk(@RequestParam("mem_name") String mem_name,
                      @RequestParam("mem_id") String mem_id,
                      @RequestParam("mem_phone") String mem_phone) {

        Map result = new HashMap();
        result.put("mem_id", memberService.getMemChk(mem_name, mem_id, mem_phone));

        return result;
    }

    @PostMapping("/memPwSet")
    @ResponseBody
    public void memPwSet(@RequestParam("mem_id") String mem_id,
                        @RequestParam("mem_pw") String mem_pw) {

        memberService.changePw(mem_id, encoder.encode(mem_pw));

    }

    @GetMapping("/logout")
    private String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/login/view";
    }



}
