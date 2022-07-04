package kr.or.dev.repository;

import kr.or.dev.entity.timeline.basic.Basic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicRepository extends JpaRepository<Basic, Long> {
}
