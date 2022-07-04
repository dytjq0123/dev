package kr.or.dev.entity.talk.chat_file;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.talk.chat.Chat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_file_no;			// 채팅첨부파일번호
    private String chat_file_name;		// 파일명
    private String chat_file_path;		// 파일경로
    private String chat_file_upload;	// 업로드파일명

    @JoinColumn(name = "chat_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;				// 채팅방번호

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;				// 채팅파일 등록자
    private LocalDateTime time;			// 채팅파일 시간
    private String type;				// 채팅파일 종류

    @Builder
    public ChatFile(String chat_file_name, String chat_file_path, String chat_file_upload, Chat chat, Member member, String type) {
        this.chat_file_name = chat_file_name;
        this.chat_file_path = chat_file_path;
        this.chat_file_upload = chat_file_upload;
        this.chat = chat;
        this.member = member;
        this.type = type;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
