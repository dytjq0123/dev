package kr.or.dev.entity.timeline.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.or.dev.entity.BaseTimeEntity;
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
//@DiscriminatorValue("schd")
@Getter
@NoArgsConstructor
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schd_no;			        // 일정글번호
    private String schd_title;		        // 제목
    private String schd_loc;		        // 장소
    private String schd_memo;		        // 메모
    private String schd_alarm;		        // 알람
    private String schd_start_time;	        // 시작일시
    private String schd_end_time;	        // 종료일시
    private String schd_del_chk;	        // 삭제여부
    private String schd_fix_chk;	        // 고정유무
    private LocalDateTime schd_fix_date;	// 고정일

    @JsonIgnore
    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;				// 프로젝트번호

    @JsonIgnore
    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;			        // 작성자

    private String schd_lat;		        // 위도
    private String schd_lon;		        // 경도

    @Transient
    private int coll_chk;			        // 담아두기 유무(entity에서만 사용)

    @Transient
    private String minute;			        // 계산된 알람시간 (entity에서만 사용)

    @Transient
    private String mem_nick;

    @Transient
    private Long pro_no;

    @ManyToMany(mappedBy = "scheduleList")
    private List<Collection> collectionList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "schedule")
    private List<Reply> replyList = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "scheduleList")
    private List<Emoticon> emoticonList = new ArrayList<>();

    @Builder
    public Schedule(String schd_title, String schd_loc, String schd_memo, String schd_alarm, String schd_start_time, String schd_end_time, Project project, Member member, String schd_lat, String schd_lon) {
        this.schd_title = schd_title;
        this.schd_loc = schd_loc;
        this.schd_memo = schd_memo;
        this.schd_alarm = schd_alarm;
        this.schd_start_time = schd_start_time;
        this.schd_end_time = schd_end_time;
        this.schd_del_chk = "n";
        this.schd_fix_chk = "n";
        this.project = project;
        this.member = member;
        this.schd_lat = schd_lat;
        this.schd_lon = schd_lon;
    }

    public void updateSchedule(String schd_title, String schd_loc, String schd_memo, String schd_alarm, String schd_start_time, String schd_end_time, String schd_lat, String schd_lon) {
        this.schd_title = schd_title;
        this.schd_loc = schd_loc;
        this.schd_memo = schd_memo;
        this.schd_alarm = schd_alarm;
        this.schd_start_time = schd_start_time;
        this.schd_end_time = schd_end_time;
        this.schd_lat = schd_lat;
        this.schd_lon = schd_lon;
    }

    public void fixChkSchd(String schd_fix_chk) {
        this.schd_fix_chk = schd_fix_chk;
        if(schd_fix_chk.equalsIgnoreCase("y")){
            this.schd_fix_date = LocalDateTime.now();
        }
    }

    public void deleteSchedule() {
        this.schd_del_chk = "y";
    }

    public void setMem_nick(String mem_nick) {
        this.mem_nick = mem_nick;
    }

    public void setPro_no(Long pro_no) {
        this.pro_no = pro_no;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }
}
