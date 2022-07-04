package kr.or.dev.repository;

import kr.or.dev.entity.group.box.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

//    @EntityGraph(attributePaths = {"member"})
    @Query("select c from Chat c left join fetch c.member m where m.mem_no = :mem_no")
    List<Box> findByMemNo(@Param("mem_no") Long mem_no);
}
