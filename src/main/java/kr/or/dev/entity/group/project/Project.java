package kr.or.dev.entity.group.project;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.box.Box;
import kr.or.dev.entity.group.important.Important;
import kr.or.dev.entity.group.kind.Kind;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.talk.facechat.FaceChat;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.entity.timeline.collection.Collection;
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
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pro_no;				// 프로젝트번호
    private String pro_name;		// 프로젝트명
    private String pro_cont;		// 프로젝트설명
    private String pro_del_chk;		// 삭제여부

    @JoinColumn(name = "kind_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Kind kind;			    // 분류코드

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;			// 개설자

    private String pro_color;	// 프로젝트 색상

    @ManyToMany(mappedBy = "projectList")
    private List<Important> importantList = new ArrayList<>();	// 중요프로젝트 체크

    @ManyToMany(mappedBy = "projectList")
    private List<Box> boxList = new ArrayList<>(); // 보관함 리스트

    @ManyToMany
    @JoinTable(name = "pro_member",
            joinColumns = @JoinColumn(name = "pro_no"),
            inverseJoinColumns = @JoinColumn(name = "mem_no"))
    private List<Member> memberList = new ArrayList<>(); // 참여자 목록

    @OneToMany(mappedBy = "project")
    private List<Basic> basicList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Task> taskList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Todo> todoList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Vote> voteList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Collection> collectionList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Files> filesList = new ArrayList<>();

    @OneToOne(mappedBy = "project", fetch = FetchType.LAZY)
    private FaceChat faceChat;

    @Transient
    private int imp_chk;

    @Builder
    public Project(String pro_name, String pro_cont, Kind kind, Member member) {
        this.pro_name = pro_name;
        this.pro_cont = pro_cont;
        this.pro_del_chk = "n";
        this.kind = kind;
        this.member = member;
    }

    public void addProjectMember(Member member) {
        memberList.add(member);
        member.setProject(this);
    }

    public void updateProject(String pro_name, String pro_cont, Kind kind) {
        this.pro_name = pro_name;
        this.pro_cont = pro_cont;
        this.kind = kind;
    }

    public void updateColor(String pro_color) {
        this.pro_color = pro_color;
    }

    public void remProjectMember(Member member) {
        memberList.remove(member);
        member.removeProject(this);
    }

    public void deleteProject() {
        this.pro_del_chk = "y";
    }

    public void setImp_chk() {
        this.imp_chk = 1;
    }


}
