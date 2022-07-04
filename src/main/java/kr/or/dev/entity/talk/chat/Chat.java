package kr.or.dev.entity.talk.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.talk.chat_con.ChatConFile;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Chat implements Comparable<Chat> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_no;			        // 채팅방번호
    private String chat_title;		        // 채팅방제목
    private LocalDateTime chat_time;		// 개설일시
    private String chat_del_chk;	        // 삭제여부

    @JsonIgnore
    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;	                // 개설자
    private String chat_ip;			        // ip주소
    private int chat_port;			        // port번호
    private String chat_cont;		        // 채팅내용
    private String chat_con_time;	        // 채팅일시
    private int in_mem_num;			        // 참여인원 수
    private String ptn_id;			        // 파트너
    private LocalDateTime time;		        // 채팅 내용 및 파일 등록 시간

    @ManyToMany
    @JoinTable(name = "chat_member",
            joinColumns = @JoinColumn(name = "chat_no"),
            inverseJoinColumns = @JoinColumn(name = "mem_no"))
    private List<Member> memberList = new ArrayList<>(); // 참여인원

//    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
//    private List<ChatCon> chatConList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
//    private List<ChatFile> chatFileList = new ArrayList<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<ChatConFile> chatConFileList = new ArrayList<>();

    @Builder
    public Chat(String chat_title, Member member, String chat_ip, int chat_port) {
        this.chat_title = chat_title;
        this.chat_time = LocalDateTime.now();
        this.chat_del_chk = "n";
        this.member = member;
        this.chat_ip = chat_ip;
        this.chat_port = chat_port;
        addChatMember(member);
    }

    public void addChatMember(Member member) {
        memberList.add(member);
        member.addChat(this);
    }

    public void remChatMember(Member member) {
        memberList.remove(member);
        member.remChat(this);
    }

//    public void addChatCon(ChatCon chatCon) {
//        chatConList.add(chatCon);
//    }
//
//    public void addChatFile(ChatFile chatFile) {
//        chatFileList.add(chatFile);
//    }

    public void updateChat(String chat_title) {
        this.chat_title = chat_title;
    }

    public void addChatConFile(ChatConFile chatConFile) {
        chatConFileList.add(chatConFile);
    }

    public void setChatInfo(String chat_cont, String chat_con_time, int in_mem_num) {
        this.chat_cont = chat_cont;
        this.chat_con_time = chat_con_time;
        this.in_mem_num = in_mem_num;
    }

    public void setPtn_id(String ptn_id) {
        this.ptn_id = ptn_id;
    }


    @Override
    public int compareTo(Chat chat) {
        return time.compareTo(chat.time);
    }
}
