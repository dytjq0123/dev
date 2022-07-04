package kr.or.dev.entity.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.Role;
import kr.or.dev.entity.alim.Alim;
import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.box.Box;
import kr.or.dev.entity.group.important.Important;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.partner.Partner;
import kr.or.dev.entity.talk.chat.Chat;
import kr.or.dev.entity.talk.facechat.FaceChat;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.entity.timeline.collection.Collection;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.entity.timeline.schedule.Schedule;
import kr.or.dev.entity.timeline.task.Task;
import kr.or.dev.entity.timeline.todo.Todo;
import kr.or.dev.entity.timeline.vote.Vote;
import kr.or.dev.entity.timeline.vote_item.VoteItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mem_no;

    private String mem_id;			// 이메일
    private String mem_name;		// 이름
    private String mem_nick;		// 닉네임
    private String mem_pw;			// 비밀번호
    private String mem_chk;			// 활성화유무
    private String mem_alim_kind;	// 알림구분
    private String mem_pic_name;	// 프로필사진명
    private String mem_pic_path;	// 프로필사진경로
    private String mem_pic_upload;	// 프로필업로드명
    private String mem_phone;		// 핸드폰번호
    private String mem_howjoin;		// 가입방법

    @Enumerated(EnumType.STRING)
    private Role role;              // 권한

    @JsonIgnore
    @ManyToMany(mappedBy = "memberList")
    private List<Chat> chatList = new ArrayList<>();

    @JoinColumn(name = "fc_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private FaceChat faceChat;

    @OneToMany(mappedBy = "member")
    private List<Task> tasks = new ArrayList<>(); // 작성한 업무

    @OneToMany(mappedBy = "member")
    private List<Project> projects = new ArrayList<>(); // 작성한 프로젝트

    @ManyToMany(mappedBy = "memberList")
    private List<VoteItem> voteItemList = new ArrayList<>(); // 참여중인 투표의 투표 목록

    @ManyToMany(mappedBy = "memberList")
    private List<Project> projectList = new ArrayList<>(); // 참여중인 프로젝트

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Important important;

    @ManyToMany(mappedBy = "memberList")
    private List<Emoticon> emoticonList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Basic> basicList = new ArrayList<>();

    @ManyToMany(mappedBy = "memberList")
    private List<Task> taskList = new ArrayList<>(); // 지정된 업무

    @ManyToMany(mappedBy = "memberList")
    private List<Vote> voteList = new ArrayList<>(); // 참여중인 투표

    @OneToMany(mappedBy = "member")
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Todo> todoList = new ArrayList<>();

    @OneToOne(mappedBy = "member")
    private Collection collection;

    @OneToMany(mappedBy = "member")
    private List<Box> boxList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Files> filesList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Alim> alimList = new ArrayList<>();

    @Transient
    private List<Partner> partnerList = new ArrayList<>();


    @Builder
    public Member(String mem_id, String mem_name, String mem_nick, String mem_pw, String mem_chk, String mem_alim_kind, String mem_pic_name, String mem_pic_path, String mem_pic_upload, String mem_phone, String mem_howjoin, Role role) {
        this.mem_id = mem_id;
        this.mem_name = mem_name;
        this.mem_nick = mem_nick;
        this.mem_pw = mem_pw;
        this.mem_chk = mem_chk;
        this.mem_alim_kind = mem_alim_kind;
        this.mem_pic_name = mem_pic_name;
        this.mem_pic_path = mem_pic_path;
        this.mem_pic_upload = mem_pic_upload;
        this.mem_phone = mem_phone;
        this.mem_howjoin = mem_howjoin;
        this.role = role;
    }


    public void update(String mem_nick, String mem_pw, String mem_alim_kind, String mem_pic_name, String mem_pic_path, String mem_phone) {
        this.mem_nick = mem_nick;
        this.mem_pw = mem_pw;
        this.mem_alim_kind = mem_alim_kind;
        this.mem_pic_name = mem_pic_name;
        this.mem_pic_path = mem_pic_path;
        this.mem_phone = mem_phone;
    }

    public void updatePassword(String mem_pw) {
        this.mem_pw = mem_pw;
    }

    public void updateProfile(String mem_name, String mem_nick, String mem_pic_name, String mem_pic_path, String mem_pic_upload) {
        this.mem_name = mem_name;
        this.mem_nick = mem_nick;
        this.mem_pic_name = mem_pic_name;
        this.mem_pic_path = mem_pic_path;
        this.mem_pic_upload = mem_pic_upload;
    }

    public void removeMember() {
        this.mem_chk = "n";
    }

    public void setProject(Project project) {
        projectList.add(project);
    }

    public void remProject(Project project) {
        projectList.remove(project);
    }

    public void addChat(Chat chat) {
        chatList.add(chat);
    }

    public void remChat(Chat chat) {
        chatList.remove(chat);
    }

    public void setFaceChat(FaceChat faceChat) {
        this.faceChat = faceChat;
    }

    public void setTask(Task task) {
        taskList.add(task);
    }

    public void setVoteItem(VoteItem voteItem) {
        voteItemList.add(voteItem);
    }

    public void setEmoticon(Emoticon emoticon) {
        emoticonList.add(emoticon);
    }

    public void setPartner(Partner partner) {
        partnerList.add(partner);
    }

    public void removeChat(Chat chat) {
        getChatList().remove(chat);
    }

    public void removeVoteItem(VoteItem voteItem) {
        getVoteItemList().remove(voteItem);
    }

    public void removeProject(Project project) {
        getProjectList().remove(project);
    }

    public void removeEmoticon(Emoticon emoticon) {
        getEmoticonList().remove(emoticon);
    }

    public void removeBasic(Basic basic) {
        getBasicList().remove(basic);
    }

    public void removeTask(Task task) {
        getTaskList().remove(task);
    }

    public void removeSchedule(Schedule schedule) {
        getScheduleList().remove(schedule);
    }

    public void removeVote(Vote vote) {
        getVoteList().remove(vote);
    }

    public void removeTodo(Todo todo) {
        getTodoList().remove(todo);
    }

    public void removeFaceChat() {
        this.faceChat = null;
    }

    public void setImportant(Important important) {
        this.important  = important;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
}
