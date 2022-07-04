package kr.or.dev.repository;

import kr.or.dev.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 회원 조회
    @Query(value = "select m from Member m where m.mem_id = :mem_id")
    Optional<Member> findByMemId(@Param("mem_id") String mem_id);

    // 아이디 중복 체크
    @Query(value = "select count(m) from Member m where m.mem_id = :mem_id")
    int idChk(@Param("mem_id") String mem_id);

    // 닉네임 중복 체크
    @Query(value = "select count(m) from Member m where m.mem_nick = :mem_nick")
    int nickChk(@Param("mem_nick") String mem_nick);

    // 아이디 찾기
    @Query(value = "select m.mem_id from Member m where m.mem_name = :mem_name and m.mem_phone = :mem_phone")
    String findId(@Param("mem_name") String mem_name, @Param("mem_phone") String mem_phone);

    // 비밀번호 찾을 때 회원 조회
    @Query(value = "select m.mem_id from Member m where m.mem_name = :mem_name and m.mem_id = :mem_id and m.mem_phone = :mem_phone")
    String getMemChk(@Param("mem_name") String mem_name, @Param("mem_id") String mem_id, @Param("mem_phone") String mem_phone);

    @Query(value = "select count(m) from Member m ")
    int getMemAllCnt();

    @Query(value = "select count(m) from Member m where m.mem_howjoin = :mem_howjoin")
    int getMemHowjoinCnt(@Param("mem_howjoin") String mem_howjoin);

    Page<Member> findAll(Pageable pageable);

}
