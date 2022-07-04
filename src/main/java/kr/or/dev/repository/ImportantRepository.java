package kr.or.dev.repository;

import kr.or.dev.entity.group.important.Important;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportantRepository extends JpaRepository<Important, Long> {

//    @EntityGraph(attributePaths = {"member"})
    @Query("select i from Important i " +
            "left join fetch i.member m " +
            "where m.mem_no =:mem_no")
    Important getImpProList(@Param("mem_no") Long mem_no);

}
