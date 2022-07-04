package kr.or.dev.entity.partner;

import kr.or.dev.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ptn_uk",
                        columnNames = {"ptn_apply", "ptn_accept"}
                )
        }
)
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ptn_no;					    // 동료신청번호
    private LocalDateTime ptn_apply_date;		// 신청일
    private LocalDateTime ptn_accept_date;		// 수락일
    private String ptn_chk;				        // 수락여부

    @JoinColumn(name = "ptn_apply")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member ptn_apply;			        // 신청자ID

    @JoinColumn(name = "ptn_accept")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member ptn_accept;			        // 수락자ID

    @Transient
    private Member member;                      // 상대방ID

    @Builder
    public Partner(Member ptn_apply, Member ptn_accept) {
        this.ptn_apply_date = LocalDateTime.now();
        this.ptn_chk = "n";
        this.ptn_apply = ptn_apply;
        this.ptn_accept = ptn_accept;
    }

    public void updatePtn() {
        this.ptn_accept_date = LocalDateTime.now();
        this.ptn_chk = "y";
    }

    public void setMember(Member member) {
        this.member = member;
    }

}
