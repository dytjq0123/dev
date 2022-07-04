package kr.or.dev.entity.timeline.collection;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.entity.timeline.schedule.Schedule;
import kr.or.dev.entity.timeline.task.Task;
import kr.or.dev.entity.timeline.todo.Todo;
import kr.or.dev.entity.timeline.vote.Vote;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Collection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coll_no;		// 담아둔글번호

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;      // 글을 담아둔 사용자

    @ManyToMany
    @JoinTable(name = "coll_schd",
            joinColumns = @JoinColumn(name = "coll_no"),
            inverseJoinColumns = @JoinColumn(name = "schd_no"))
    private List<Schedule> scheduleList = new ArrayList<>(); // 담아둔 일정글

    @ManyToMany
    @JoinTable(name = "coll_vote",
            joinColumns = @JoinColumn(name = "coll_no"),
            inverseJoinColumns = @JoinColumn(name = "vote_no"))
    private List<Vote> voteList = new ArrayList<>(); // 담아둔 투표글

    @ManyToMany
    @JoinTable(name = "coll_todo",
            joinColumns = @JoinColumn(name = "coll_no"),
            inverseJoinColumns = @JoinColumn(name = "todo_no"))
    private List<Todo> todoList = new ArrayList<>(); // 담아둔 할일

    @ManyToMany
    @JoinTable(name = "coll_basic",
            joinColumns = @JoinColumn(name = "coll_no"),
            inverseJoinColumns = @JoinColumn(name = "basic_no"))
    private List<Basic> basicList = new ArrayList<>(); // 담아둔 일반글

    @ManyToMany
    @JoinTable(name = "coll_task",
            joinColumns = @JoinColumn(name = "coll_no"),
            inverseJoinColumns = @JoinColumn(name = "task_no"))
    private List<Task> taskList = new ArrayList<>(); // 담아둔 업무글

    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @Builder
    public Collection(Member member) {
        this.member = member;
    }

    public void addTimeLine(TimeLine timeLine) {
        if(timeLine != null){
            if(timeLine.getSchedule() != null){
                addCollSchedule(timeLine.getSchedule());
            }
            if(timeLine.getVote() != null){
                addCollVote(timeLine.getVote());
            }
            if(timeLine.getTodo() != null){
                addCollTodo(timeLine.getTodo());
            }
            if(timeLine.getBasic() != null){
                addCollBasic(timeLine.getBasic());
            }
            if(timeLine.getTask() != null){
                addCollTask(timeLine.getTask());
            }

        }
    }

    public void addCollSchedule(Schedule schedule) {
        getScheduleList().add(schedule);
    }

    public void addCollVote(Vote vote) {
        getVoteList().add(vote);
    }

    public void addCollTodo(Todo todo) {
        getTodoList().add(todo);
    }

    public void addCollBasic(Basic basic) {
        getBasicList().add(basic);
    }

    public void addCollTask(Task task) {
        getTaskList().add(task);
    }

    public void removeTimeLine(TimeLine timeLine) {
        if(timeLine != null){
            if(timeLine.getSchedule() != null){
                removeCollSchedule(timeLine.getSchedule());
            }
            if(timeLine.getVote() != null){
                removeCollVote(timeLine.getVote());
            }
            if(timeLine.getTodo() != null){
                removeCollTodo(timeLine.getTodo());
            }
            if(timeLine.getBasic() != null){
                removeCollBasic(timeLine.getBasic());
            }
            if(timeLine.getTask() != null){
                removeCollTask(timeLine.getTask());
            }

        }
    }

    public void removeCollSchedule(Schedule schedule) {
        getScheduleList().remove(schedule);
    }

    public void removeCollVote(Vote vote) {
        getVoteList().remove(vote);
    }

    public void removeCollTodo(Todo todo) {
        getTodoList().remove(todo);
    }

    public void removeCollBasic(Basic basic) {
        getBasicList().remove(basic);
    }

    public void removeCollTask(Task task) {
        getTaskList().remove(task);
    }

}
