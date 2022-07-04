package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.schedule.ScheduleInsertDto;
import kr.or.dev.dto.timeline.schedule.ScheduleUpdateDto;
import kr.or.dev.entity.Format;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.schedule.Schedule;
import kr.or.dev.service.MemberService;
import kr.or.dev.service.ProjectService;
import kr.or.dev.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schd")
public class ScheduleController {

    private final ScheduleService scheduleService;

    private final MemberService memberService;

    private final ProjectService projectService;

    @PostMapping("/insert")
    public String insertSchedule(ScheduleInsertDto scheduleInsertDto,
                                 @RequestParam("alert_time") String alert_time,
                                 @RequestParam("datetime") String datetime,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 Model model) throws ParseException {

        scheduleService.insertSchd(scheduleInsertDto, alert_time, datetime, userDetails);

        model.addAttribute("message", "글이 등록되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + scheduleInsertDto.getPro_no());

        return "alertMessage";
    }

    @PostMapping("/update")
    public String updateSchedule(ScheduleUpdateDto scheduleUpdateDto,
                                 @RequestParam("alert_time") String alert_time,
                                 @RequestParam("datetime") String datetime,
                                 @RequestParam("defaultDate") String defaultDate,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 Model model) throws ParseException {

        scheduleService.updateSchd(scheduleUpdateDto, alert_time, datetime, defaultDate, userDetails);

        model.addAttribute("message", "글이 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + scheduleUpdateDto.getPro_no());

        return "alertMessage";
    }

    @GetMapping("/delete")
    public String deleteSchedule(@RequestParam("timeline_no") Long schd_no,
                                 Model model) {

        Schedule schedule = scheduleService.getSchdDetail(schd_no);
        Long pro_no = schedule.getProject().getPro_no();

        scheduleService.deleteSchd(schd_no);

        model.addAttribute("message", "글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";

    }

    @GetMapping("/fix")
    public String basicFix(@RequestParam("timeline_no") Long schd_no,
                           @RequestParam("fix_chk") String schd_fix_chk,
                           Model model) {

        Schedule schedule = scheduleService.getSchdDetail(schd_no);
        scheduleService.fixChkSchd(schd_no, schd_fix_chk);


        if(schd_fix_chk.equalsIgnoreCase("y")){
            model.addAttribute("message", "상단고정 되었습니다.");
        }else {
            model.addAttribute("message", "상단고정 해제 되었습니다.");
        }
        model.addAttribute("searchUrl", "/pro/detail/" + schedule.getProject().getPro_no());

        return "alertMessage";

    }

    @GetMapping("/callCalendar")
    public String callCalendar() {

        return "fullCalendar";
    }

    @PostMapping("/callCalendar2")
    @ResponseBody
    public ResponseEntity<?> scheduleParser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               HttpServletResponse response) {

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());

        member.getScheduleList().forEach(schedule -> {
            schedule.setPro_no(schedule.getProject().getPro_no());
            schedule.setMem_nick(schedule.getMember().getMem_nick());
        });

        Map map = new HashMap();
        map.put("mySchedule", member.getScheduleList());
        map.put("invitedSchd", "");

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @GetMapping("/callSchd")
    public String callCalendarById_no(@RequestParam("pro_no") Long pro_no,
                                      Model model) {

        Project project = projectService.getProDetail(pro_no);

        model.addAttribute("project", project);
        return "proFullCalendar";
    }

    @PostMapping("/callSchd2")
    @ResponseBody
    public ResponseEntity<?> projectScheduleParser(@RequestParam("pro_no") Long pro_no,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<Schedule> scheduleList = new ArrayList<>();

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());

        Project project = projectService.getProDetail(pro_no);
        project.getScheduleList().forEach(schedule -> {
            if(schedule.getMember().getMem_no() == member.getMem_no()){
                scheduleList.add(schedule);
            }
        });

        Map map = new HashMap();
        map.put("mySchedule", scheduleList);
        map.put("invitedSchd", "");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/dbcheck")
    @ResponseBody
    public ResponseEntity<?> alimAjax(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      Model model) throws ParseException {

        List<Schedule> scheduleList = new ArrayList<>();

        String notificationFormatter = Format.notificationFormatter(Format.timeFormat(new Date()), 0);

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());
        if(member.getScheduleList() != null){
            member.getScheduleList().forEach(schedule -> {
                if(schedule.getSchd_alarm().equals(notificationFormatter) && schedule.getSchd_del_chk().equals("n")){
                    scheduleList.add(schedule);
                }
            });
        }

        return new ResponseEntity<>(scheduleList, HttpStatus.OK);
    }
}
