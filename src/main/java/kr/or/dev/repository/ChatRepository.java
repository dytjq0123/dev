package kr.or.dev.repository;

import kr.or.dev.entity.talk.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c from Chat c left join fetch c.member m where m.mem_no = :mem_no")
    List<Chat> findByMemNo(@Param("mem_no") Long mem_no);
}
