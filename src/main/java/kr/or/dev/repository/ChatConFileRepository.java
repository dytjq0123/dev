package kr.or.dev.repository;

import kr.or.dev.entity.talk.chat_con.ChatConFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatConFileRepository extends JpaRepository<ChatConFile, Long> {
}
