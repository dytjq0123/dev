package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.task.TaskInsertDto;
import kr.or.dev.dto.timeline.task.TaskUpdateDto;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.task.Task;
import kr.or.dev.service.MemberService;
import kr.or.dev.service.ProjectService;
import kr.or.dev.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    private final MemberService memberService;

    private final ProjectService projectService;

    @GetMapping("taskAll")
    public String taskAllList(@RequestParam("mem_id") String mem_id,
                              Model model) {

        Member member = memberService.getMemberDetail(mem_id);
        member.getTaskList();
        member.getTasks();

        List<Task> taskList = taskService.findAll();

        model.addAttribute("taskList", taskList);

        return "taskAll";

    }

    @GetMapping("/proTask")
    public String proTaskList(@RequestParam("pro_no") Long pro_no,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              Model model) {

        Project project = projectService.getProDetail(pro_no);

        Member member = memberService.getMemberDetail(userDetails.getMember().getMem_id());


        model.addAttribute("taskList", project.getTaskList());
        model.addAttribute("member", member);

        return "taskAll";
    }

    @PostMapping("/insert")
    public String taskInsert(TaskInsertDto taskInsertDto,
                             @RequestParam(value="tu_email", required=false)List<String> tu_emailList,
                             @RequestParam(value="articleFile", required=false) List<MultipartFile> fileList,
                             @RequestParam(value="imageFile", required=false) List<MultipartFile> imgList,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) throws IOException {






        taskService.insertTask(taskInsertDto, tu_emailList, fileList, imgList, userDetails);


        model.addAttribute("message", "글이 등록되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + taskInsertDto.getPro_no());

        return "alertMessage";

    }

    @PostMapping("/update")
    public String taskUpdate(TaskUpdateDto taskUpdateDto,
                             @RequestParam(value="tu_email", required=false)List<String> tu_emailList,
                             @RequestParam(value="articleFile", required=false) List<MultipartFile> fileList,
                             @RequestParam(value="imageFile", required=false) List<MultipartFile> imgList,
                             @RequestParam(value="del_files_no", required=false) List<Long> delFileList,
                             @RequestParam(value="del_task_user_no", required=false)List<Long> delTuList,
                             Model model) throws IOException {

        taskService.updateTask(taskUpdateDto, tu_emailList, fileList, imgList, delFileList, delTuList);

        model.addAttribute("message", "글이 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + taskUpdateDto.getPro_no());

        return "alertMessage";

    }

    @GetMapping("/state")
    public String taskStateUpdate(@RequestParam("task_no") Long task_no,
                                  @RequestParam("task_state") String task_state,
                                  Model model) {

        Task task = taskService.getTaskDetail(task_no);
        taskService.updateState(task_no, task_state);

        model.addAttribute("message", "상태값이 변경되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + task.getProject().getPro_no());

        return "alertMessage";


    }

    @GetMapping("/rate")
    public String taskRateUpdate(@RequestParam("task_no") Long task_no,
                                 @RequestParam("task_rate") int task_rate,
                                 Model model) {

        Task task = taskService.getTaskDetail(task_no);
        taskService.updateRate(task_no, task_rate);

        model.addAttribute("message", "진척도가 변경되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + task.getProject().getPro_no());

        return "alertMessage";
    }

    @GetMapping("/delete")
    public String taskDelete(@RequestParam("timeline_no") Long task_no, Model model) throws NotFoundException {
        Task task = taskService.getTaskDetail(task_no);
        Long pro_no = task.getProject().getPro_no();

        taskService.deleteTask(task_no);

        model.addAttribute("message", "글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";
    }

    @GetMapping("/fix")
    public String taskFix(@RequestParam("timeline_no") long task_no,
                           @RequestParam("fix_chk") String task_fix_chk,
                           Model model) {

        Task task = taskService.getTaskDetail(task_no);
        taskService.fixChkTask(task_no, task_fix_chk);


        if(task_fix_chk.equalsIgnoreCase("y")){
            model.addAttribute("message", "상단고정 되었습니다.");
        }else {
            model.addAttribute("message", "상단고정 해제 되었습니다.");
        }
        model.addAttribute("searchUrl", "/pro/detail/" + task.getProject().getPro_no());

        return "alertMessage";

    }
}
