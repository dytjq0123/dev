package kr.or.dev.repository;

import kr.or.dev.entity.talk.chat_file.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {

    @Query("select cf from ChatFile cf left join fetch cf.chat c where c.chat_no =:chat_no")
    List<ChatFile> findByChatNo(@Param("chat_no") Long chat_no);
}
