package kr.or.dev.entity.timeline.task;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.collection.Collection;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.entity.timeline.reply.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
//@DiscriminatorValue("task")
@Getter
@NoArgsConstructor
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long task_no;				// 업무글번호
    private String task_title;			// 제목
    private String task_state;			// 업무상태
    private String task_cont;			// 내용
    private String task_start_date;		// 시작일
    private String task_end_date;		// 종료일
    private int task_rate;				// 진행률
    private String task_priority;		// 우선순위
    private String task_del_chk;		// 삭제여부
    private String task_fix_chk;		// 고정유무
    private LocalDateTime task_fix_date;// 고정일

    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;			// 프로젝트번호

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;				// 작성자

    @Transient
    private int coll_chk;				// 담아두기 유무(entity에서만 사용)

    @ManyToMany
    @JoinTable(name = "task_member",
            joinColumns = @JoinColumn(name = "task_no"),
            inverseJoinColumns = @JoinColumn(name = "mem_no"))
    private List<Member> memberList = new ArrayList<>();

    @ManyToMany(mappedBy = "taskList")
    private List<Collection> collectionList = new ArrayList<>();

    @OneToMany(mappedBy = "task")
    private List<Reply> replyList = new ArrayList<>();

    @OneToMany(mappedBy = "task")
    private List<Files> filesList = new ArrayList<>();

    @ManyToMany(mappedBy = "taskList")
    private List<Emoticon> emoticonList = new ArrayList<>();

    @Builder
    public Task(String task_title, String task_state, String task_cont, String task_start_date, String task_end_date, int task_rate, String task_priority, Project project, Member member) {
        this.task_title = task_title;
        this.task_state = task_state;
        this.task_cont = task_cont;
        this.task_start_date = task_start_date;
        this.task_end_date = task_end_date;
        this.task_rate = task_rate;
        this.task_priority = task_priority;
        this.task_del_chk = "n";
        this.task_fix_chk = "n";
        this.project = project;
        this.member = member;
    }

    public void updateTask(String task_title, String task_state, String task_cont, String task_start_date, String task_end_date, int task_rate, String task_priority) {
        this.task_title = task_title;
        this.task_state = task_state;
        this.task_cont = task_cont;
        this.task_start_date = task_start_date;
        this.task_end_date = task_end_date;
        this.task_rate = task_rate;
        this.task_priority = task_priority;
    }

    public void fixChkTask(String task_fix_chk) {
        this.task_fix_chk = task_fix_chk;
        if(task_fix_chk.equalsIgnoreCase("y")){
            this.task_fix_date = LocalDateTime.now();
        }
    }

    public void deleteTask() {
        this.task_del_chk = "y";
    }

//    public void setTimeLine(TimeLine timeLine) {
//        this.timeLine = timeLine;
//        timeLine.setTask(this);
//    }

    public void addTaskMember(Member member) {
        memberList.add(member);
        member.setTask(this);
    }

    public void removeTaskMember(Member member) {
        memberList.remove(member);
        member.removeTask(this);
    }

    public void updateState(String task_state) {
        this.task_state = task_state;
    }

    public void updateRate(int task_rate) {
        this.task_rate = task_rate;
    }
}
