package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.box.BoxUpdateDto;
import kr.or.dev.entity.group.box.Box;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.service.BoxService;
import kr.or.dev.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    private final MemberService memberService;

    @GetMapping("/box/insert")
    public String boxInsert(@RequestParam("box_name") String box_name,
                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                            Model model) {

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());


        long box_no = boxService.insertBox(Box.builder()
                .box_name(box_name)
                .member(member)
                .build());

        model.addAttribute("message", "보관함이 추가되었습니다.");
        model.addAttribute("searchUrl", "/pro/main");

        return "alertMessage";
    }

    @GetMapping("/box/update")
    public String boxUpdate(BoxUpdateDto boxUpdateDto, Model model) {

        boxService.updateBox(boxUpdateDto);

        model.addAttribute("message", "보관함이 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/main");

        return "alertMessage";
    }

    @GetMapping("/box/delete")
    public String boxDelete(@RequestParam("box_no") Long box_no,
                            Model model) {
        boxService.deleteBox(box_no);

        model.addAttribute("message", "보관함이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/main");

        return "alertMessage";
    }

    @GetMapping("/boxPro/list")
    public String boxProList(@RequestParam("box_no") Long box_no,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) {
        Box box = boxService.getBoxDetail(box_no);

        Member member = userDetails.getMember();

        box.getProjectList().forEach(project -> {
            for(Project pro : member.getImportant().getProjectList()){
                if(pro.getPro_no() == project.getPro_no()){
                    project.setImp_chk();
                }
            }
        });

        model.addAttribute("projectList", box.getProjectList());
        model.addAttribute("member", member);
        model.addAttribute("what", box.getBox_name() + " 보관함 ");

        return "projectList";
    }

    @GetMapping("/boxPro/insert")
    @ResponseBody
    public long boxProInsert(@RequestParam("box_no") Long box_no,
                            @RequestParam("pro_no") Long pro_no) {

        boxService.addProject(box_no, pro_no);

        return box_no;
    }

    @GetMapping("/boxPro/delete")
    @ResponseBody
    public long boxProDelete(@RequestParam("box_no") Long box_no,
                            @RequestParam("pro_no") Long pro_no) {

        boxService.delProject(box_no, pro_no);

        return box_no;
    }


}
