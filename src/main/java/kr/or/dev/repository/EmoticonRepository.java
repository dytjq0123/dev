package kr.or.dev.repository;

import kr.or.dev.entity.timeline.emoticon.Emoticon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {
}
