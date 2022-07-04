package kr.or.dev.repository;

import kr.or.dev.entity.talk.chat_con.ChatCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatConRepository extends JpaRepository<ChatCon, Long> {

    @Query("select cc from ChatCon cc left join fetch cc.chat c where c.chat_no = :chat_no")
    List<ChatCon> findByChatNo(@Param("chat_no") Long chat_no);
}
