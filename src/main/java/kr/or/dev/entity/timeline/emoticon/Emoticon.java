package kr.or.dev.entity.timeline.emoticon;

import kr.or.dev.entity.BaseTimeEntity;
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
public class Emoticon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emo_no;				// 이모티콘번호
    private String emo_name;			// 이모티콘명
    private String emo_pic_name;		// 이모티콘파일명
    private String emo_pic_upload;		// 이모티콘업로드명
    private String emo_pic_path;		// 이모티콘경로
    private String emo_del_chk;			// 이모티콘사용유무

    @ManyToMany
    @JoinTable(name = "emo_member",
            joinColumns = @JoinColumn(name = "emo_no"),
            inverseJoinColumns = @JoinColumn(name = "mem_no"))
    private List<Member> memberList = new ArrayList<>();  // 사용자

    @ManyToMany
    @JoinTable(name = "emo_basic",
            joinColumns = @JoinColumn(name = "emo_no"),
            inverseJoinColumns = @JoinColumn(name = "basic_no"))
    private List<Basic> basicList = new ArrayList<>();  // 이모티콘이 사용된 일반글 리스트

    @ManyToMany
    @JoinTable(name = "emo_task",
            joinColumns = @JoinColumn(name = "emo_no"),
            inverseJoinColumns = @JoinColumn(name = "task_no"))
    private List<Task> taskList = new ArrayList<>();  // 이모티콘이 사용된 업무글 리스트

    @ManyToMany
    @JoinTable(name = "emo_schedule",
            joinColumns = @JoinColumn(name = "emo_no"),
            inverseJoinColumns = @JoinColumn(name = "schd_no"))
    private List<Schedule> scheduleList = new ArrayList<>();  // 이모티콘이 사용된 일정글 리스트

    @ManyToMany
    @JoinTable(name = "emo_todo",
            joinColumns = @JoinColumn(name = "emo_no"),
            inverseJoinColumns = @JoinColumn(name = "todo_no"))
    private List<Todo> todoList = new ArrayList<>();  // 이모티콘이 사용된 할일 리스트

    @ManyToMany
    @JoinTable(name = "emo_vote",
            joinColumns = @JoinColumn(name = "emo_no"),
            inverseJoinColumns = @JoinColumn(name = "vote_no"))
    private List<Vote> voteList = new ArrayList<>();  // 이모티콘이 사용된 투표글 리스트


    @Builder
    public Emoticon(String emo_name, String emo_pic_name, String emo_pic_upload, String emo_pic_path) {
        this.emo_name = emo_name;
        this.emo_pic_name = emo_pic_name;
        this.emo_pic_upload = emo_pic_upload;
        this.emo_pic_path = emo_pic_path;
        this.emo_del_chk = "n";
    }

    public void addEmoUser(Member member, TimeLine timeLine) {
//        this.member = member;
        memberList.add(member);
        member.setEmoticon(this);
        addTimeLine(timeLine);
//        timeLine.setEmoticon(this);
    }

    public void addTimeLine(TimeLine timeLine) {
        if(timeLine.getBasic() != null) {
            getBasicList().add(timeLine.getBasic());
        }else if(timeLine.getTask() != null) {
            getTaskList().add(timeLine.getTask());
        }else if(timeLine.getTodo() != null) {
            getTodoList().add(timeLine.getTodo());
        }else if(timeLine.getSchedule() != null) {
            getScheduleList().add(timeLine.getSchedule());
        }else if(timeLine.getVote() != null) {
            getVoteList().add(timeLine.getVote());
        }
    }

    public void remEmoUser(Member member, TimeLine timeLine) {
        getMemberList().remove(member);
        member.removeEmoticon(this);
        remTimeLine(timeLine);
    }

    public void remTimeLine(TimeLine timeLine) {
        if(timeLine.getBasic() != null) {
            getBasicList().remove(timeLine.getBasic());
        }else if(timeLine.getTask() != null) {
            getTaskList().remove(timeLine.getTask());
        }else if(timeLine.getTodo() != null) {
            getTodoList().remove(timeLine.getTodo());
        }else if(timeLine.getSchedule() != null) {
            getScheduleList().remove(timeLine.getSchedule());
        }else if(timeLine.getVote() != null) {
            getVoteList().remove(timeLine.getVote());
        }
    }

}
