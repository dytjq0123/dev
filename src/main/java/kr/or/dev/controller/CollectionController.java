package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coll")
public class CollectionController {

    private final CollectionService collectionService;

    private final EmoticonService emoticonService;

    private final BasicService basicService;

    private final TaskService taskService;

    private final ScheduleService scheduleService;

    private final TodoService todoService;

    private final VoteService voteService;


    @GetMapping("/insert")
    @ResponseBody
    public long insertColl(@RequestParam("timeline_col") String timeline_col,
                           @RequestParam("timeline_no") Long timeline_no,
                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();

        TimeLine timeLine = getTimeLine(timeline_col, timeline_no);

        long coll_no = collectionService.insertColl(member, timeLine);

        return coll_no;


    }

    @GetMapping("/delete")
    @ResponseBody
    public int deleteColl(@RequestParam("timeline_col") String timeline_col,
                          @RequestParam("timeline_no") Long timeline_no,
                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();

        TimeLine timeLine = getTimeLine(timeline_col, timeline_no);

        collectionService.removeColl(member, timeLine);

        return 1;
    }



    @GetMapping("/collArticle")
    public String colArticle(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) {
        Member member = userDetails.getMember();

        List<TimeLine> myCollList = collectionService.getMyCollList(member.getMem_no());

        List<Emoticon> emoAllList = emoticonService.getEmoAllList();

        model.addAttribute("timeLineList", myCollList);
        model.addAttribute("emoticonList", emoAllList);
        model.addAttribute("artKind", "coll");
        model.addAttribute("member", "member");

        return "collection_article";

    }

    @GetMapping("/myArticle")
    public String myArticle(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) {
        Member member = userDetails.getMember();

        List<TimeLine> myTimeLineList = collectionService.getMyTimeLineList(member.getMem_no());

        List<Emoticon> emoAllList = emoticonService.getEmoAllList();

        model.addAttribute("timeLineList", myTimeLineList);
        model.addAttribute("emoticonList", emoAllList);
        model.addAttribute("artKind", "my");
        model.addAttribute("member", "member");

        return "collection_article";

    }

    public TimeLine getTimeLine(String timeline_col, Long timeline_no) {
        TimeLine timeLine = new TimeLine();

        if(timeline_col.equalsIgnoreCase("basic_no")){
            timeLine.setBasic(basicService.getBasicDetail(timeline_no));
        }else if(timeline_col.equalsIgnoreCase("task_no")){
            timeLine.setTask(taskService.getTaskDetail(timeline_no));
        }else if(timeline_col.equalsIgnoreCase("schd_no")){
            timeLine.setSchedule(scheduleService.getSchdDetail(timeline_no));
        }else if(timeline_col.equalsIgnoreCase("todo_no")){
            timeLine.setTodo(todoService.getTodoDetail(timeline_no));
        }else if(timeline_col.equalsIgnoreCase("vote_no")){
            timeLine.setVote(voteService.getVoteDetail(timeline_no));
        }

        return timeLine;
    }
}
