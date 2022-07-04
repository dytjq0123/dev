package kr.or.dev.entity.timeline.vote;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.collection.Collection;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.entity.timeline.reply.Reply;
import kr.or.dev.entity.timeline.vote_item.VoteItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
//@DiscriminatorValue("vote")
@Getter
@NoArgsConstructor
public class Vote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vote_no;			            // 투표글번호
    private String vote_title;		            // 제목
    private String vote_del_chk;	            // 삭제여부
    private String vote_fix_chk;	            // 고정유무
    private LocalDateTime vote_fix_date;		// 고정일

    @JoinColumn(name = "pro_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;				    // 프로젝트번호

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;			            // 작성자

    private String vote_end_time;	            // 투표종료일

    @Transient
    private int coll_chk;			            // 담아두기 유무(entity에서만 사용)

    @ManyToMany(mappedBy = "voteList")
    private List<Collection> collectionList = new ArrayList<>();

    @OneToMany(mappedBy = "vote")
    private List<VoteItem> voteItemList = new ArrayList<>();

    @OneToMany(mappedBy = "vote")
    private List<Reply> replyList = new ArrayList<>();

    @ManyToMany(mappedBy = "voteList")
    private List<Emoticon> emoticonList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "vote_member",
            joinColumns = @JoinColumn(name = "vote_no"),
            inverseJoinColumns = @JoinColumn(name = "mem_no"))
    private List<Member> memberList = new ArrayList<>();

    @Builder
    public Vote(String vote_title, Project project, Member member, String vote_end_time) {
        this.vote_title = vote_title;
        this.vote_del_chk = "n";
        this.vote_fix_chk = "n";
        this.project = project;
        this.member = member;
        this.vote_end_time = vote_end_time;
    }

    public void updateVote(String vote_title, String vote_end_time) {
        this.vote_title = vote_title;
        this.vote_end_time = vote_end_time;
    }

    public void fixChkVote(String vote_fix_chk) {
        this.vote_fix_chk = vote_fix_chk;
        if(vote_fix_chk.equalsIgnoreCase("y")){
            this.vote_fix_date = LocalDateTime.now();
        }
    }

    public void deleteVote() {
        this.vote_del_chk = "y";
    }

    public void addVoteItem(VoteItem voteItem) {
        getVoteItemList().add(voteItem);
        voteItem.addVote(this);
    }

    public void remVoteItem(VoteItem voteItem) {
        getVoteItemList().remove(voteItem);
    }

    public void addVoteMember(Member member) {
        getMemberList().add(member);
    }

    public void remVoteMember(Member member) {
        getMemberList().remove(member);
    }
}
