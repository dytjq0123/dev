package kr.or.dev.entity.alim;

import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.partner.Partner;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.entity.timeline.pick.Pick;
import kr.or.dev.entity.timeline.schedule.Schedule;
import kr.or.dev.entity.timeline.task.Task;
import kr.or.dev.entity.timeline.todo.Todo;
import kr.or.dev.entity.timeline.vote.Vote;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
public class Alim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alim_no;		            // 알림번호
    private LocalDateTime alim_time;		// 알림일시
    private String alim_chk;	            // 확인여부
    private String alim_cont;	            // 알림내용

    @JoinColumn(name = "basic_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Basic basic;		            // 일반글번호

    @JoinColumn(name = "vote_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Vote vote;		                // 투표글번호

    @JoinColumn(name = "todo_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Todo todo;		                // 할일번호

    @JoinColumn(name = "schd_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;		        // 일정글번호

    @JoinColumn(name = "task_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;		                // 업무글번호

    @JoinColumn(name = "pick_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Pick pick;		                // 지정한글번호

    @JoinColumn(name = "ptn_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Partner partner;			    // 동료신청번호

    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;			    // 프로젝트번호

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;		            // 회원ID

    @JoinColumn(name = "apply")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member apply;		            // 알림 등록 회원ID

    private LocalDateTime accept_date;      // 친구 신청 수락일

    public Alim(String alim_cont, Map paramMap, Member member, Member apply) {
        String alim_col = (String)paramMap.get("alim_col");
        Object alim_col_val = (Object)paramMap.get("alim_col_val");
        this.alim_cont = (String)paramMap.get("alim_cont");
        if(alim_col.equalsIgnoreCase("basic_no")){
            this.basic = (Basic) alim_col_val;
        }else if(alim_col.equalsIgnoreCase("vote_no")){
            this.vote = (Vote) alim_col_val;
        }else if(alim_col.equalsIgnoreCase("todo_no")){
            this.todo = (Todo) alim_col_val;
        }else if(alim_col.equalsIgnoreCase("schd_no")){
            this.schedule = (Schedule) alim_col_val;
        }else if(alim_col.equalsIgnoreCase("task_no")){
            this.task = (Task) alim_col_val;
        }else if(alim_col.equalsIgnoreCase("pick_no")){
            this.pick = (Pick) alim_col_val;
        }else if(alim_col.equalsIgnoreCase("ptn_no")){
            this.partner = (Partner) alim_col_val;
        }else if(alim_col.equalsIgnoreCase("pro_no")){
            this.project = (Project) alim_col_val;
        }
        this.member = (Member)paramMap.get("member");
        this.apply = (Member)paramMap.get("apply");;
    }

    public void updateAlim() {
        this.alim_chk = "y";
    }
}
