package kr.or.dev.service;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.todo.TodoItemUpdateDto;
import kr.or.dev.dto.timeline.todo.TodoUpdateDto;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.todo.Todo;
import kr.or.dev.entity.timeline.todo_item.TodoItem;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import kr.or.dev.repository.TodoItemRepository;
import kr.or.dev.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    private final ProjectRepository projectRepository;

    private final MemberRepository memberRepository;

    private final TodoItemRepository todoItemRepository;

    public long insertTodo(Long pro_no,
                           String todo_title,
                           List<String> ti_contList,
                           List<String> ti_dateList,
                           List<String> ti_mem_idList,
                           UserDetailsImpl userDetails) {

        Member fromMember = userDetails.getMember();

        Project project = projectRepository.findById(pro_no).get();

        Todo saveTodo = todoRepository.save(Todo.builder()
                .todo_title(todo_title)
                .member(fromMember)
                .project(project)
                .build());

        if(ti_contList != null){
            for(int i = 0; i < ti_contList.size(); i++){
                Member toMember = memberRepository.findByMemId(ti_mem_idList.get(i)).get();
                TodoItem saveTodoItem = todoItemRepository.save(TodoItem.builder()
                        .ti_cont(ti_contList.get(i))
                        .ti_date(ti_dateList.get(i))
                        .toMember(toMember)
                        .fromMember(fromMember)
                        .build());
                saveTodo.addTodoItem(saveTodoItem);
            }
        }

        return saveTodo.getTodo_no();
    }

    public void updateTodo(TodoUpdateDto todoUpdateDto) {
        Todo findTodo = todoRepository.findById(todoUpdateDto.getTodo_no()).get();

        for (TodoItem todoItem : findTodo.getTodoItemList()) {
            findTodo.remTodoItem(todoItem);
            todoItemRepository.delete(todoItem);
        }

//        if(todoUpdateDto.getTiNoDelList().size() > 0){
//
//            for (Long ti_no : todoUpdateDto.getTiNoDelList()) {
//                TodoItem todoItem = todoItemRepository.findById(ti_no).get();
//                findTodo.remTodoItem(todoItem);
//                todoItemRepository.delete(todoItem);
//            }
//        }

        if(todoUpdateDto.getTi_contList().size() > 0){
            for(int i = 0; i < todoUpdateDto.getTi_contList().size(); i++){
                if(findTodo.getTodoItemList().size() < 6){
                    Member toMember = memberRepository.findByMemId(todoUpdateDto.getTi_mem_idList().get(i)).get();
                    TodoItem saveTodoItem = todoItemRepository.save(TodoItem.builder()
                            .ti_cont(todoUpdateDto.getTi_contList().get(i))
                            .ti_date(todoUpdateDto.getTi_dateList().get(i))
                            .toMember(toMember)
                            .fromMember(findTodo.getMember())
                            .build());
                    if(findTodo.getTodoItemList().size() < 6){
                        findTodo.addTodoItem(saveTodoItem);
                    }
                }
            }
        }
    }

    public void deleteTodo(Long todo_no) {
        Todo findTodo = todoRepository.findById(todo_no).get();

        findTodo.deleteTodo();
    }

    public Todo getTodoDetail(Long todo_no) {
        Todo findTodo = todoRepository.findById(todo_no).get();

        return findTodo;
    }

    public List<TimeLine> getTodoProList(Long pro_no, Member member) {
        List<TimeLine> resultList = new ArrayList<>();

        Project project = projectRepository.findById(pro_no).get();

        project.getTodoList().forEach(todo -> {
            if(todo.getTodo_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setTodo(todo);
                newTl.setMember(todo.getMember());
                newTl.setFix_chk(todo.getTodo_fix_chk());
                todo.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(todo.getEmoticonList());
                todo.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(todo.getCreateDate());
                if(member.getCollection() != null){
                    if(member.getCollection().getTodoList().contains(todo)){
                        newTl.setColl_chk(1);
                    }
                }
                resultList.add(newTl);
            }
        });

        return resultList;
    }

//    public List<TimeLine> getTodoCollList(Long mem_no) {
//        Member member = memberRepository.findById(mem_no).get();
//
//        List<TimeLine> resultList = new ArrayList<>();
//
//        member.getCollectionList().forEach(collection -> {
//            TimeLine newTl = new TimeLine();
//            newTl.setTodo(collection.getTodo());
//            newTl.setMember(collection.getTodo().getMember());
//            newTl.setFix_chk(collection.getTodo().getTodo_fix_chk());
//            newTl.setEmoticonList(collection.getTodo().getEmoticonList());
//            newTl.setReplyList(collection.getTodo().getReplyList());
//            resultList.add(newTl);
//        });
//
//        return resultList;
//
//    }

    public List<TimeLine> getMyTodoList(Long mem_no) {
        List<TimeLine> resultList = new ArrayList<>();

        Member member = memberRepository.findById(mem_no).get();

        member.getTodoList().forEach(todo -> {
            TimeLine newTl = new TimeLine();
            newTl.setTodo(todo);
            newTl.setMember(todo.getMember());
            newTl.setEmoticonList(todo.getEmoticonList());
            newTl.setReplyList(todo.getReplyList());
            resultList.add(newTl);
        });

        return resultList;
    }

    public void fixChkTodo(Long todo_no, String todo_fix_chk) {
        Todo todo = getTodoDetail(todo_no);
        todo.fixChkTodo(todo_fix_chk);
    }

    public void todoItemUpdate(TodoItemUpdateDto todoItemUpdateDto) {
        TodoItem todoItem = todoItemRepository.findById(todoItemUpdateDto.getTi_no()).get();
        todoItem.updateTodoItem(todoItemUpdateDto.getTi_cont(), todoItemUpdateDto.getTi_chk());

        Todo todo = todoRepository.findById(todoItemUpdateDto.getTodo_no()).get();
        todo.updateTodo("", todoItemUpdateDto.getTodo_rate());

    }
}
