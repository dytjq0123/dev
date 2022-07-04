package kr.or.dev.service;

import kr.or.dev.dto.chat.ChatUpdateDto;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.talk.chat.Chat;
import kr.or.dev.entity.talk.chat_con.ChatCon;
import kr.or.dev.entity.talk.chat_file.ChatFile;
import kr.or.dev.repository.*;
import kr.or.dev.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final MemberRepository memberRepository;

    private final ChatConFileRepository chatConFileRepository;

    private final ChatConRepository chatConRepository;

    private final ChatFileRepository chatFileRepository;

    public List getChatList(Long mem_no) {

        Member member = memberRepository.findById(mem_no).get();

        List<Chat> chatList = member.getChatList();

        List resultList = new ArrayList();

        chatList.forEach(chat -> {
            Map map = new HashMap();
            map.put("chat_no", chat.getChat_no());
            map.put("chat_title", chat.getChat_title());
            map.put("mem_nick", chat.getMember().getMem_nick());
            map.put("chat_ip", chat.getChat_ip());
            map.put("in_mem_num", chat.getIn_mem_num());
            map.put("mem_id", chat.getMember().getMem_id());
            map.put("ptn_id", chat.getPtn_id());
            map.put("chat_cont", chat.getChat_cont());
            map.put("chat_con_time", chat.getChat_con_time());

            resultList.add(map);

        });

        return resultList;
    }

    public Chat getChatDetail(Long chat_no) {
        Chat chat = chatRepository.findById(chat_no).get();

        return chat;
    }

    public long insertChat(Member member, String chat_title, Member partner, HttpServletRequest request) {

        String clientIP = CommonUtil.getClientIP(request);
        int serverPort = request.getServerPort();

        Chat saveChat = chatRepository.save(Chat.builder()
                .chat_title(chat_title)
                .member(member)
                .chat_ip(clientIP)
                .chat_port(serverPort)
                .build());

        if(partner != null){
            saveChat.addChatMember(partner);
        }

        return saveChat.getChat_no();
    }

    public void addChatUser(Long chat_no, List<Member> partnerList) {
        Chat chat = chatRepository.findById(chat_no).get();

        if(partnerList != null){
            for (Member partner : partnerList) {
                chat.addChatMember(partner);
            }
        }
    }

    public void updateChat(ChatUpdateDto chatUpdateDto) {
        Chat findChat = chatRepository.findById(chatUpdateDto.getChat_no()).get();
        findChat.updateChat(chatUpdateDto.getChat_title());
    }

    public void deleteChat(Long chat_no, Member member) {
        Chat chat = chatRepository.findById(chat_no).get();

        int memCnt = chat.getMemberList().size();

        if(0 < memCnt && memCnt < 2){
            chat.getChatConFileList().forEach(chatConFile -> {
                ChatCon chatCon = chatConFile.getChatCon();
                ChatFile chatFile = chatConFile.getChatFile();
                chatConFileRepository.delete(chatConFile);
                if(chatCon != null){
                    chatConRepository.delete(chatCon);
                }
                if(chatFile != null){
                    chatFileRepository.delete(chatFile);
                }
            });
            chat.remChatMember(member);
            chatRepository.delete(chat);
        }else {
            chat.remChatMember(member);

            chat.setChatInfo(chat.getChat_cont(), chat.getChat_con_time(), chat.getMemberList().size());
        }






    }

    public List chatUserList(Long chat_no) {
        List chatUserList = new ArrayList();

        Chat chat = chatRepository.findById(chat_no).get();

        chat.getMemberList().forEach(member -> {
            Map memberMap = new HashMap();
            memberMap.put("mem_id", member.getMem_id());
            memberMap.put("mem_nick", member.getMem_nick());
            chatUserList.add(memberMap);
        });

        return chatUserList;
    }




}
