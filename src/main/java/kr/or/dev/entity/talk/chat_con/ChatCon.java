package kr.or.dev.entity.talk.chat_con;

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
public class ChatCon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_con_no;		        // 채팅내용번호
    private String chat_cont;		        // 채팅내용
    private LocalDateTime chat_con_time;	// 채팅일시

    @JoinColumn(name = "chat_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;			            // 채팅방번호

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;			        // 참여자

    @Transient
    private LocalDateTime time;			    // 채팅시간 : entity에서만 사용함

    @Transient
    private String chat_title;		        // 채팅제목 : entity에서만 사용

    @Transient
    private int in_mem_num;		            // 채팅참여자수 : entity에서만 사용

    @Transient
    private String chat_ip;			        // 채팅아이피 : entity에서만 사용

    @Builder
    public ChatCon(String chat_cont, Chat chat, Member member) {
        this.chat_cont = chat_cont;
        this.chat_con_time = LocalDateTime.now();
        this.chat = chat;
        this.member = member;
    }
}
