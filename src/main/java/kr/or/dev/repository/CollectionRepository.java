package kr.or.dev.repository;

import kr.or.dev.entity.timeline.collection.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    @Query("select c from Collection c left join fetch c.member m where m.mem_no = :mem_no")
    List<Collection> findByMemNo(@Param("mem_no") Long mem_no);

    @Query("select c from Collection c left join fetch c.member m left join fetch c.project p where m.mem_no = :mem_no and p.pro_no = :pro_no")
    List<Collection> findByProMemNo(@Param("mem_no") Long mem_no, @Param("pro_no") Long pro_no);
}
