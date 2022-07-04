package kr.or.dev.repository;

import kr.or.dev.entity.group.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"kind"})
    @Query("select count(p) from Project p")
    int findByKindNo(@Param("kind_no") Long kind_no);
}
