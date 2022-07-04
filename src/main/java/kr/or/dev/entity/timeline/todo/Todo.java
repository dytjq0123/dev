package kr.or.dev.entity.timeline.todo;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.collection.Collection;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.entity.timeline.reply.Reply;
import kr.or.dev.entity.timeline.todo_item.TodoItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
//@DiscriminatorValue("todo")
@Getter
@NoArgsConstructor
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todo_no;				// 할일번호
    private String todo_title;			// 제목
    private int todo_rate;				// 진행률
    private String todo_del_chk;		// 삭제여부
    private String todo_fix_chk;		// 고정유무
    private LocalDateTime todo_fix_date;// 고정일

    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;			// 프로젝트 번호

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;				// 작성자

    @Transient
    private int coll_chk;				// 담아두기 유무(entity에서만 사용)

    @ManyToMany(mappedBy = "todoList")
    private List<Collection> collectionList = new ArrayList<>();

    @OneToMany(mappedBy = "todo")
    private List<TodoItem> todoItemList = new ArrayList<>();

    @OneToMany(mappedBy = "todo")
    private List<Reply> replyList = new ArrayList<>();

    @ManyToMany(mappedBy = "todoList")
    private List<Emoticon> emoticonList = new ArrayList<>();

    @Builder
    public Todo(String todo_title, Project project, Member member) {
        this.todo_title = todo_title;
        this.todo_rate = 0;
        this.todo_del_chk = "n";
        this.todo_fix_chk = "n";
        this.project = project;
        this.member = member;
    }

    public void updateTodo(String todo_title, int todo_rate) {
        if(!todo_title.isEmpty()){
            this.todo_title = todo_title;
        }
        this.todo_rate = todo_rate;
    }

    public void fixChkTodo(String todo_fix_chk) {
        this.todo_fix_chk = todo_fix_chk;
        if(todo_fix_chk.equalsIgnoreCase("y")){
            this.todo_fix_date = LocalDateTime.now();
        }
    }

    public void deleteTodo() {
        this.todo_del_chk = "y";
    }

    public void addTodoItem(TodoItem todoItem) {
        getTodoItemList().add(todoItem);
        todoItem.addTodo(this);
    }

    public void remTodoItem(TodoItem todoItem) {
        getTodoItemList().remove(todoItem);
    }

}
