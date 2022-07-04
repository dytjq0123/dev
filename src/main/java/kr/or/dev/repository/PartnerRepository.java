package kr.or.dev.repository;

import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.partner.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    @Query("select p from Partner p where p.ptn_chk = 'y' and (p.ptn_apply = :member or p.ptn_accept = :member)")
    List<Partner> getPtnMyList(@Param("member") Member member);

//    @Query("select p from Partner p where p.ptn_chk = 'y' and p.ptn_apply = :member")
//    List<Partner> getPtnMyListApply(@Param("member") Member member);

//    @Query("select p from Partner p where p.ptn_chk = 'y' and p.ptn_accept = :member")
//    List<Partner> getPtnMyListAccept(@Param("member") Member member);

    @Query("select count(p) from Partner p")
    int getPtnSeq();
}
