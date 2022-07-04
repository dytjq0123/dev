package kr.or.dev.entity.timeline.vote_item;

import kr.or.dev.entity.member.Member;
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
public class VoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vi_no;			// 투표항목번호
    private String vi_cont;		// 투표항목내용
    private String vi_del_chk;	// 삭제여부

    @JoinColumn(name = "vote_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Vote vote;		// 투표글번호

    @Transient
    private int viu_cnt;		// 투표한 회원 수 (entity에서만 사용)

    private int viu_chk;		// 투표 유무 체크

    @ManyToMany
    @JoinTable(name = "vote_item_member",
            joinColumns = @JoinColumn(name = "vi_no"),
            inverseJoinColumns = @JoinColumn(name = "mem_no"))
    private List<Member> memberList = new ArrayList<>();

    @Builder
    public VoteItem(String vi_cont, Vote vote) {
        this.vi_cont = vi_cont;
        this.vi_del_chk = "n";
        this.vote = vote;
    }

    public void addVoteItemMember(Member member) {
        memberList.add(member);
    }

    public void addVote(Vote vote) {
        this.vote = vote;
    }
}
