package kr.or.dev.entity.talk.chat_con;

import kr.or.dev.entity.talk.chat.Chat;
import kr.or.dev.entity.talk.chat_file.ChatFile;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatConFile implements Comparable<ChatConFile>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_con_file_no;

    @JoinColumn(name = "chat_con_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatCon chatCon;

    @JoinColumn(name = "chat_file_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatFile chatFile;

    @JoinColumn(name = "chat_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    private LocalDateTime time;

    @Builder
    public ChatConFile(ChatCon chatCon, ChatFile chatFile, Chat chat) {
        if(chatCon != null){
            this.chatCon = chatCon;
        }else if(chatFile != null){
            this.chatFile = chatFile;
        }
        this.chat = chat;
        this.time = LocalDateTime.now();
    }

    @Override
    public int compareTo(ChatConFile o) {
        return time.compareTo(o.time);
    }

    public void addChatCon(ChatCon chatCon) {
        this.chatCon = chatCon;
    }

    public void addChatFile(ChatFile chatFile) {
        this.chatFile = chatFile;
    }
}
