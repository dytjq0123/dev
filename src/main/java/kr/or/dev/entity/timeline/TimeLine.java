package kr.or.dev.entity.timeline;

import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.entity.timeline.reply.Reply;
import kr.or.dev.entity.timeline.schedule.Schedule;
import kr.or.dev.entity.timeline.task.Task;
import kr.or.dev.entity.timeline.todo.Todo;
import kr.or.dev.entity.timeline.vote.Vote;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Entity
@Getter
@Setter
public class TimeLine implements Comparable<TimeLine>{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long tl_no;

    private Basic basic;

    private Schedule schedule;

    private Task task;

    private Todo todo;

    private Vote vote;

    private Member member;
    private String fix_chk;                 // 고정 유무
    private LocalDateTime time;             // 작성일
    private int coll_chk;                   // 담아두기 유무
    private boolean emo_user_chk = false;   // 이모티콘 사용자에 본인이 포함된 경우
    private Long emo_no;
    private String emo_name;
    private Project project;

    private List<Files> filesList = new ArrayList<>();
    private List<Reply> replyList = new ArrayList<>();
    private List<Emoticon> emoticonList = new ArrayList<>();

    @Override
    public int compareTo(TimeLine timeLine) {
        return timeLine.getTime().compareTo(time);
    }

    public void addReplyList(Reply reply) {
        getReplyList().add(reply);
    }

}
