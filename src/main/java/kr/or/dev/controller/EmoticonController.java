package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emo")
public class EmoticonController {

    private final EmoticonService emoticonService;

    private final MemberService memberService;

    private final BasicService basicService;

    private final TaskService taskService;

    private final ScheduleService scheduleService;

    private final TodoService todoService;

    private final VoteService voteService;

    @GetMapping("/view")
    public void memPic(@RequestParam("emo_no") Long emo_no,
                       HttpServletResponse response) throws IOException {

        Emoticon emoticon = emoticonService.getEmoDetail(emo_no);

        String filePath = emoticon.getEmo_pic_path() + emoticon.getEmo_pic_name();

        File file = new File(filePath);
        if(!file.isFile()){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("<script type='text/javascript'>alert('조회된 정보가 없습니다.'); self.close();</script>");
            out.flush();
            return;
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

    @GetMapping("/insert")
    @ResponseBody
    public long emoUserInsert(@RequestParam("emo_no") Long emo_no,
                             @RequestParam("timeline_col") String timeline_col,
                             @RequestParam("timeline_no") Long timeline_no,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {


        Map paramMap = new HashMap();

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());

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

        paramMap.put("member", member);
        paramMap.put("timeLine", timeLine);

        long result = emoticonService.insertEmoUser(emo_no, paramMap);

        return result;
    }

    @GetMapping("/delete")
    @ResponseBody
    public int emoUserDelete(@RequestParam("emo_no") Long emo_no,
                             @RequestParam("timeline_col") String timeline_col,
                             @RequestParam("timeline_no") Long timeline_no,
                             @RequestParam("emo_user_no") Long emo_user_no) {

        Map paramMap = new HashMap();

        Member member = memberService.findById(emo_user_no);

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

        paramMap.put("member", member);
        paramMap.put("timeLine", timeLine);

        emoticonService.deleteEmoUser(emo_no, paramMap);

        return 1;
    }
}
