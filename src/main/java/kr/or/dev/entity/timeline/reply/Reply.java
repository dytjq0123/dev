package kr.or.dev.entity.timeline.reply;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.files.Files;
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
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rep_no;				// 댓글번호(pk)
    private String rep_cont;		// 댓글내용
    private String rep_del_chk;		// 삭제여부

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;			// 작성자ID

    @JoinColumn(name = "basic_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Basic basic;

    @JoinColumn(name = "task_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @JoinColumn(name = "schd_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @JoinColumn(name = "Todo_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Todo todo;

    @JoinColumn(name = "vote_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Vote vote;

    @OneToMany(mappedBy = "reply")
    private List<Files> filesList = new ArrayList<>();

    @Builder
    public Reply(String rep_cont, Member member, TimeLine timeLine) {
        this.rep_cont = rep_cont;
        this.rep_del_chk = "n";
        this.member = member;
        if(timeLine.getBasic() != null){
            this.basic = timeLine.getBasic();
        }else if(timeLine.getTask() != null){
            this.task = timeLine.getTask();
        }else if(timeLine.getSchedule() != null){
            this.schedule = timeLine.getSchedule();
        }else if(timeLine.getTodo() != null){
            this.todo = timeLine.getTodo();
        }else if(timeLine.getVote() != null){
            this.vote = timeLine.getVote();
        }
    }

    public void updateReply(String rep_cont) {
        this.rep_cont = rep_cont;
    }

    public void deleteReply() {
        this.rep_del_chk = "y";
    }

}
