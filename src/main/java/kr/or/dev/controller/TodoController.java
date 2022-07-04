package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.todo.TodoItemUpdateDto;
import kr.or.dev.dto.timeline.todo.TodoUpdateDto;
import kr.or.dev.entity.timeline.todo.Todo;
import kr.or.dev.service.MemberService;
import kr.or.dev.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    private final MemberService memberService;

    @PostMapping("/insert")
    public String todoInsert(@RequestParam("pro_no") Long pro_no,
                             @RequestParam("todo_title") String todo_title,
                             @RequestParam("ti_cont") List<String> ti_contList,
                             @RequestParam("ti_date") List<String> ti_dateList,
                             @RequestParam("ti_mem_id") List<String> ti_mem_idList,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) {

        todoService.insertTodo(pro_no, todo_title, ti_contList, ti_dateList, ti_mem_idList, userDetails);

        model.addAttribute("message", "글이 등록되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";
    }

    @PostMapping("/update")
    public String todoUpdate(TodoUpdateDto todoUpdateDto,
                             Model model) {

        todoService.updateTodo(todoUpdateDto);

        model.addAttribute("message", "글이 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + todoUpdateDto.getPro_no());

        return "alertMessage";

    }

    @GetMapping("/delete")
    public String todoDelete(@RequestParam("timeline_no") Long todo_no,
                             Model model) {
        Todo todo = todoService.getTodoDetail(todo_no);

        todoService.deleteTodo(todo_no);

        model.addAttribute("message", "글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + todo.getProject().getPro_no());

        return "alertMessage";
    }

    @GetMapping("/fix")
    public String todoFix(@RequestParam("timeline_no") Long todo_no,
                           @RequestParam("fix_chk") String todo_fix_chk,
                           Model model) {

        Todo todo = todoService.getTodoDetail(todo_no);
        todoService.fixChkTodo(todo_no, todo_fix_chk);


        if(todo_fix_chk.equalsIgnoreCase("y")){
            model.addAttribute("message", "상단고정 되었습니다.");
        }else {
            model.addAttribute("message", "상단고정 해제 되었습니다.");
        }
        model.addAttribute("searchUrl", "/pro/detail/" + todo.getProject().getPro_no());

        return "alertMessage";

    }

    @PostMapping("/todoItemUpdate")
    @ResponseBody
    public long todoItemUpdate(TodoItemUpdateDto todoItemUpdateDto) {

        try {
            todoService.todoItemUpdate(todoItemUpdateDto);
        } catch (Exception e) {
            return 0;
        }

        return 1;
    }


}
