package kr.or.dev.entity.files;

import kr.or.dev.entity.board.post.Post;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.entity.timeline.reply.Reply;
import kr.or.dev.entity.timeline.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
public class Files implements Comparable<Files>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long files_no;			    // 파일번호
    private String files_name;		    // 파일명
    private String files_path;		    // 파일경로
    private String files_upload;	    // 업로드파일명
    private String files_size;		    // 파일크기
    private String files_kind;		    // 첨부 구분
    private LocalDateTime files_time;	// 작성일시

    @JoinColumn(name = "basic_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Basic basic;			    // 일반글번호

    @JoinColumn(name = "task_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;			        // 업무글번호

    @JoinColumn(name = "rep_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reply reply;				// 댓글번호

    @JoinColumn(name = "post_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;			        // 게시글번호

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;              // 작성자

    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @Builder
    public Files(String files_name, String files_path, String files_upload, String files_size, String files_kind, Map paramMap, Project project, Member member) {
        this.files_name = files_name;
        this.files_path = files_path;
        this.files_upload = files_upload;
        this.files_size = files_size;
        this.files_kind = files_kind;
        this.files_time = LocalDateTime.now();

        String timeline_col = (String)paramMap.get("timeline_col");
        Object timeline_col_val = (Object)paramMap.get("timeline_col_val");
        if(timeline_col.equalsIgnoreCase("basic_no")){
            this.basic = (Basic) timeline_col_val;
        }else if(timeline_col.equalsIgnoreCase("task_no")){
            this.task = (Task) timeline_col_val;
        }else if(timeline_col.equalsIgnoreCase("reply_no")){
            this.reply = (Reply) timeline_col_val;
        }else if(timeline_col.equalsIgnoreCase("post_no")){
            this.post = (Post) timeline_col_val;
        }
        this.project = project;
        this.member = member;
    }

    @Override
    public int compareTo(Files files) {
        return files.getFiles_time().compareTo(files_time);
    }
}
