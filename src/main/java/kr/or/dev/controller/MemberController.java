package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.member.MemberUpdateProfileDto;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.partner.Partner;
import kr.or.dev.service.MemberService;
import kr.or.dev.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mem")
public class MemberController {

    private final MemberService memberService;

    private final PartnerService partnerService;

    private final PasswordEncoder encoder;

    @GetMapping("/pic")
    public void memPic(@RequestParam("mem_id") String mem_id,
                       HttpServletResponse response) throws IOException {

        Member member = memberService.getMemberDetail(mem_id);

        String filePath = member.getMem_pic_path() + member.getMem_pic_upload();

        File file = new File(filePath);
        if(!file.isFile()){
            file = new File("/Users/owner/Desktop/study/dev/src/main/resources/static/image/user-pic-sample.png");
//            response.setContentType("text/html; charset=UTF-8");
//            PrintWriter out = response.getWriter();
//            out.write("<script type='text/javascript'>alert('조회된 정보가 없습니다.'); self.close();</script>");
//            out.flush();
//            return;
        }

        FileInputStream fis = null;
        new FileInputStream(file);

        BufferedInputStream in = null;
        ByteArrayOutputStream bStream = null;
        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            bStream = new ByteArrayOutputStream();
            int imgByte;
            while ((imgByte = in.read()) != -1) {
                bStream.write(imgByte);
            }

            String type = "";
            String ext = FilenameUtils.getExtension(file.getName());
            if (ext != null && !"".equals(ext)) {
                if ("jpg".equals(ext.toLowerCase())) {
                    type = "image/jpeg";
                } else {
                    type = "image/" + ext.toLowerCase();
                }

            }

            response.setHeader("Content-Type", type);
            response.setContentLength(bStream.size());

            bStream.writeTo(response.getOutputStream());

            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bStream != null) {
                try {
                    bStream.close();
                } catch (Exception est) {
                    est.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ei) {
                    ei.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception efis) {
                    efis.printStackTrace();
                }
            }
        }
    }

    @GetMapping("/ptnChk")
    @ResponseBody
    public Map<String, String> getPhone(@RequestParam("mem_id") String mem_id,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){

        // 회원정보 가져오기
        Member findMember = memberService.getMemberDetail(mem_id);
        String phoneNum = findMember.getMem_phone();

        Map<String, String> map = new HashMap<String, String>();
        map.put("phoneNum", phoneNum);
        map.put("ptnResult", "false");

        // 동료 목록 가져오기
        if(findMember.getMem_no() != userDetails.getMember().getMem_no()){
            List<Partner> myPtnList = partnerService.getMyPtnList(findMember.getMem_no());
            for (Partner partner : myPtnList) {
                if(partner.getPtn_accept() == findMember || partner.getPtn_apply() == findMember){
                    map.put("ptnResult", "true");
                    break;
                } else {
                    map.put("ptnResult", "false");
                }
            }
        }

        return map;
    }

    @GetMapping("memEdit")
    public String memEdit() {
        return "memEdit";
    }

    @PostMapping("/memUpdate")
    public String memUpdate(MemberUpdateProfileDto memberUpdateProfileDto,
                            @RequestParam("mem_pic") MultipartFile mem_pic,
                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                            Model model) throws IOException {

        memberService.changeProfile(memberUpdateProfileDto, mem_pic, userDetails);

        model.addAttribute("message", "회원 정보가 수정되었습니다.");
        model.addAttribute("searchUrl", "/mem/memEdit");

        return "alertMessage";

    }

    @PostMapping("/memPwChk")
    @ResponseBody
    public Map memPwChk(@RequestParam("pw") String pw,
                        @RequestParam("newPw") String newPw,
                        @RequestParam("newPwChk") String newPwChk,
                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Map resultMap = new HashMap();

        //  현재 입력된 비밀번호와 db에 저장된 비밀번호 비교
        boolean matches = encoder.matches(pw, userDetails.getMember().getMem_pw());

        if(matches){
            if(!pw.equalsIgnoreCase(newPw)){
                if(newPw.equalsIgnoreCase(newPwChk)){
                    resultMap.put("upd", 1);
                }else {
                    resultMap.put("result", "새 비밀번호가 일치하지 않습니다.");
                }
            }else {
                resultMap.put("result", "현재 비밀번호와 새 비밀번호가 같습니다.");
            }
        }else {
            resultMap.put("result", "현재 비밀번호가 일치하지 않습니다.");
        }

        return resultMap;

    }

    @PostMapping("/memPwUpdate")
    public String memPwUpdate(@RequestParam("newPw") String newPw,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              Model model) {
        
        memberService.changePwLogIn(encoder.encode(newPw), userDetails);

        model.addAttribute("message", "비밀번호가 수정되었습니다.");
        model.addAttribute("searchUrl", "/mem/memEdit");

        return "alertMessage";

    }

    @PostMapping("/memOutPwChk")
    @ResponseBody
    public Map memPwChk(@RequestParam("mem_pw") String mem_pw,
                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Map resultMap = new HashMap();

        //  현재 입력된 비밀번호와 db에 저장된 비밀번호 비교
        boolean matches = encoder.matches(mem_pw, userDetails.getMember().getMem_pw());

        if(matches){
            resultMap.put("rem", 1);
        }else {
            resultMap.put("result", "비밀번호가 일치하지 않습니다.");
        }

        return resultMap;

    }

    @PostMapping("/memOut")
    public String memOut(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         Model model) {
        memberService.removeMember(userDetails.getMember().getMem_id());

        model.addAttribute("message", "계정이 탈퇴되었습니다.");
        model.addAttribute("searchUrl", "/login/logout");

        return "alertMessage";

    }
}
