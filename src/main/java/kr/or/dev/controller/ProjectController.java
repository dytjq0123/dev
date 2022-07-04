package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.project.ProjectUpdateDto;
import kr.or.dev.entity.group.kind.Kind;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.partner.Partner;
import kr.or.dev.entity.talk.facechat.FaceChat;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.service.*;
import kr.or.dev.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pro")
public class ProjectController {

    private final MemberService memberService;

    private final ProjectService projectService;

    private final BasicService basicService;

    private final TaskService taskService;

    private final TodoService todoService;

    private final ScheduleService scheduleService;

    private final VoteService voteService;

    private final KindService kindService;

    private final PartnerService partnerService;

    private final EmoticonService emoticonService;

    private final FaceChatService faceChatService;

    @GetMapping("/main")
    public String proMain(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpSession session, Model model) {
        String mem_id = userDetails.getMember().getMem_id();

        Member member = memberService.getMemberDetail(mem_id);

        List<Member> memAllList = memberService.getMemAllList();
        memAllList.remove(member);

        List<Member> memProMemList = memberService.getMemProMemList(member.getMem_no());
        memProMemList.remove(member);

        List<Kind> kindAllList = kindService.getKindAllList();

        List<Partner> myPtnList = partnerService.getMyPtnList(member.getMem_no());
        for (Partner partner : myPtnList) {
            member.setPartner(partner);
        }

        userDetails.setMember(member);

        member.getProjectList().forEach(project -> {
            if(member.getImportant() != null){
                if(member.getImportant().getProjectList().contains(project)){
                    project.setImp_chk();
                }
            }
        });

        session.setAttribute("kindList", kindAllList);
        session.setAttribute("memAllList", memAllList);
        session.setAttribute("memProMemList", memProMemList);
        model.addAttribute("projectList", member.getProjectList());


        return "project/main";
    }

    @GetMapping("/detail/{pro_no}")
    public String detailPro(@PathVariable("pro_no") Long pro_no,
                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                            HttpServletRequest request,
                            Model model) {
        List<Emoticon> emoticonList = emoticonService.getEmoAllList();

        Project project = projectService.getProDetail(pro_no);

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());

        List<TimeLine> timeLineList = new ArrayList<>();

        List<TimeLine> basicProList = basicService.getBasicProList(pro_no, member);
        List<TimeLine> taskProList = taskService.getTaskProList(pro_no, member);
        List<TimeLine> schdProList = scheduleService.getSchdProList(pro_no, member);
        List<TimeLine> todoProList = todoService.getTodoProList(pro_no, member);
        List<TimeLine> voteProList = voteService.getVoteProList(pro_no, member);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime today = LocalDateTime.now();

        for (TimeLine timeLine : todoProList) {
            timeLine.getTodo().getTodoItemList().forEach(todoItem -> {
                LocalDateTime day = LocalDateTime.parse(todoItem.getTi_date() + " 01:00:00.001", formatter);

                int compare = day.compareTo(today);

                if(compare > 0){
                    todoItem.setFinish(true);
                }else {
                    todoItem.setFinish(false);
                }
            });
        }

        timeLineList.addAll(basicProList);
        timeLineList.addAll(taskProList);
        timeLineList.addAll(schdProList);
        timeLineList.addAll(todoProList);
        timeLineList.addAll(voteProList);

        Collections.sort(timeLineList);

        member.getBoxList().forEach(box -> {
            for (Project pro : box.getProjectList()) {
                if(pro.getPro_no() == project.getPro_no()){
                    box.setPro_box_chk();
                }
            }
        });

        FaceChat faceChat = project.getFaceChat();

        String clientIP = CommonUtil.getClientIP(request);

        model.addAttribute("ip", clientIP);
        model.addAttribute("faceChat", faceChat);
        model.addAttribute("project", project);
        model.addAttribute("member", member);
        model.addAttribute("timeLineList", timeLineList);
        model.addAttribute("emoticonList", emoticonList);
        model.addAttribute("currentDate", LocalDateTime.now());

        return "project/project";
    }

    @GetMapping("/all")
    public String allPro(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         Model model) {

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());

        member.getProjectList().forEach(project -> {
            if(member.getImportant().getProjectList().contains(project)){
                project.setImp_chk();
            }
        });

        model.addAttribute("member", member);
        model.addAttribute("projectList", member.getProjectList());
        model.addAttribute("what", "전체 ");

        return "projectList";
    }

    @PostMapping("/insert")
    public String insertPro(@RequestParam("pro_name") String pro_name,
                            @RequestParam("kind_no") Long kind_no,
                            @RequestParam("pro_cont") String pro_cont,
                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                            Model model) {

        Kind kind = kindService.getKindDetail(kind_no);

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());

        long pro_no = projectService.insertPro(Project.builder()
                .pro_name(pro_name)
                .pro_cont(pro_cont)
                .kind(kind)
                .member(member)
                .build());

        model.addAttribute("message", "프로젝트가 추가되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";
    }

    @GetMapping("/update")
    public String updatePro(ProjectUpdateDto projectUpdateDto,
                            Model model) {

        projectService.updatePro(projectUpdateDto);

        model.addAttribute("message", "프로젝트가 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + projectUpdateDto.getPro_no());

        return "alertMessage";
    }

    @GetMapping("/deleteProUser")
    public String deleteProUser(@RequestParam("pro_no") Long pro_no,
                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                Model model) {

        Project project = projectService.getProDetail(pro_no);

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());

        if(project.getMember() != member){ // 프로젝트 참여자 일 경우

            project.remProjectMember(member);
        }else { // 프로젝트 작성자 일 경우
            model.addAttribute("message", "프로젝트 관리자는 최소 한명이 존재해야 합니다.");
            model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

            return "alertMessage";
        }

        model.addAttribute("message", "나가기 되었습니다.");
        model.addAttribute("searchUrl", "/pro/main");

        return "alertMessage";

    }

    @GetMapping("/delete")
    public String deletePro(@RequestParam("pro_no") Long pro_no,
                            Model model) throws NotFoundException {

        projectService.deletePro(pro_no);

        model.addAttribute("message", "프로젝트가 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/main");

        return "alertMessage";
    }

    @GetMapping("/colorUpdate")
    @ResponseBody
    public long colorUpdate(@RequestParam("pro_no") Long pro_no,
                           @RequestParam("pro_color") String pro_color) {

        projectService.updateColor(pro_no, pro_color);

        return pro_no;
    }

    @GetMapping("/chk")
    @ResponseBody
    public long proUserCheck(@RequestParam("pro_no") Long pro_no,
                             @RequestParam("mem_id") String mem_id) {

        Project project = projectService.getProDetail(pro_no);

        for (Member member : project.getMemberList()) {
            if(member.getMem_id().equalsIgnoreCase(mem_id)){
                return 1;
            }
        }

        return 0;
    }

    @PostMapping("/invite")
    public String invite(@RequestParam("pro_no") Long pro_no,
                         @RequestParam("mem_id") List<String> mem_idList,
                         Model model) {


        projectService.addProjectMember(pro_no, mem_idList);

        model.addAttribute("message", "프로젝트에 초대하였습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";

    }

    @PostMapping("/sendMail")
    public String sendMail(@RequestParam("pro_no") Long pro_no,
                           @RequestParam("toEmail") List<String> emailList,
                           @RequestParam(value="email_con", defaultValue="Flowolf로 업무관리, 채팅, 파일공유, 일정 공지를 한 곳에서!\n pc에서 사용해보세요.")String email_con,
                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                           Model model) throws MessagingException {

        projectService.sendMail(pro_no, emailList, email_con, userDetails.getMember().getMem_id());

        model.addAttribute("message", "프로젝트에 초대하였습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";

    }

    @GetMapping("/copy")
    public String copyToInvite(@RequestParam("pro_no") Long pro_no,
                               @RequestParam("mem_id") String mem_id,
                               Model model) {

        Member member = memberService.getMemberDetail(mem_id);

        Project project = projectService.getProDetail(pro_no);

        model.addAttribute("project", project);
        model.addAttribute("member", member);

        return "invite";
    }

}
