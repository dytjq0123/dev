package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.entity.group.important.Important;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.service.ImportantService;
import kr.or.dev.service.MemberService;
import kr.or.dev.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/imp")
public class ImportantController {

    private final ImportantService importantService;

    private final MemberService memberService;

    private final ProjectService projectService;

    @GetMapping("/list")
    public String impProList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) {

        String mem_id = userDetails.getMember().getMem_id();
        Member member = memberService.getMemberDetail(mem_id);

        model.addAttribute("member", member);

        return "project/impProList";
    }

    @GetMapping("/insert")
    @ResponseBody
    public void insertImp(@RequestParam("pro_no") Long pro_no,
                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();

        Project project = projectService.getProDetail(pro_no);

        Important impProList = importantService.getImpProList(member.getMem_no());

        if(impProList == null){
            Important important = importantService.insertImp(Important.builder()
                    .member(member)
                    .build());

        }else {
            importantService.addImpProject(member.getMem_no(), project);
        }


    }

    @GetMapping("/delete")
    @ResponseBody
    public void deleteImp(@RequestParam("pro_no") Long pro_no,
                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();

        Project project = projectService.getProDetail(pro_no);

        Important impProList = importantService.getImpProList(member.getMem_no());

        if(impProList != null){
            importantService.removeImpProject(member.getMem_no(), project);
        }

    }

}
