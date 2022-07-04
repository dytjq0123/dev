package kr.or.dev.repository;

import kr.or.dev.entity.alim.Alim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlimRepository extends JpaRepository<Alim, Long> {

    // 알림 리스트
//    @EntityGraph(attributePaths = {"member"})
    @Query("select a from Alim a left join fetch a.member m where m.mem_no =:mem_no")
    List<Alim> findByMemNo(@Param("mem_no") Long mem_no);

    // 알림 상세정보
    Optional<Alim> findById(@Param("alim_no") Long alim_no);
}
