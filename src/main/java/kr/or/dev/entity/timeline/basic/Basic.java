package kr.or.dev.entity.timeline.basic;

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
//@DiscriminatorValue("basic")
@Getter
@NoArgsConstructor
public class Basic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long basic_no;			        // 일반글번호
    private String basic_cont;		        // 내용
    private String basic_del_chk;	        // 삭제여부
    private String basic_fix_chk;	        // 고정유무
    private LocalDateTime basic_fix_date;	// 고정일

    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;		        // 프로젝트

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;			        // 작성자

    @Transient
    private int coll_chk;			        // 담아두기 유무(entity에서만 사용)

    @ManyToMany(mappedBy = "basicList")
    private List<Collection> collectionList = new ArrayList<>();

    @OneToMany(mappedBy = "basic")
    private List<Reply> replyList = new ArrayList<>();

    @OneToMany(mappedBy = "basic")
    private List<Files> filesList = new ArrayList<>();

    @ManyToMany(mappedBy = "basicList")
    private List<Emoticon> emoticonList = new ArrayList<>();

    @Builder
    public Basic(String basic_cont, Project project, Member member) {
        this.basic_cont = basic_cont;
        this.basic_del_chk = "n";
        this.basic_fix_chk = "n";
        this.project = project;
        this.member = member;
    }

    public void fixChkBasic(String basic_fix_chk) {
        this.basic_fix_chk = basic_fix_chk;
        if(basic_fix_chk.equalsIgnoreCase("y")){
            this.basic_fix_date = LocalDateTime.now();
        }
    }

    public void updateBasic(String basic_cont) {
        this.basic_cont = basic_cont;
    }

    public void deleteBasic() {
        this.basic_del_chk = "y";
    }

//    public void setTimeLine(TimeLine timeLine) {
//        this.timeLine = timeLine;
//        timeLine.setBasic(this);
//    }
}
