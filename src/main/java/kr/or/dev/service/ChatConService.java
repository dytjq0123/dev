package kr.or.dev.service;

import kr.or.dev.entity.talk.chat.Chat;
import kr.or.dev.entity.talk.chat_con.ChatCon;
import kr.or.dev.entity.talk.chat_con.ChatConFile;
import kr.or.dev.repository.ChatConFileRepository;
import kr.or.dev.repository.ChatConRepository;
import kr.or.dev.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatConService {

    private final ChatConRepository chatConRepository;

    private final ChatConFileRepository chatConFileRepository;

    private final ChatRepository chatRepository;

    public List<ChatCon> getChatConList(Long chat_no) {
        List<ChatCon> chatConList = chatConRepository.findByChatNo(chat_no);

        return chatConList;
    }

    public ChatCon insertChatCon(ChatCon chatCon) {
        ChatCon saveChatCon = chatConRepository.save(chatCon);
        ChatConFile saveChatConFile = chatConFileRepository.save(ChatConFile.builder()
                .chat(chatCon.getChat())
                .chatCon(chatCon)
                .build());

        Chat chat = chatRepository.findById(chatCon.getChat().getChat_no()).get();
        chat.setChatInfo(saveChatCon.getChat_cont(),
                saveChatCon.getChat_con_time().format(DateTimeFormatter.ofPattern("MM/dd a HH:mm")).toString(),
                chat.getMemberList().size());

        if(chat.getMemberList().size() == 2){
            chat.setPtn_id(chat.getMemberList().get(1).getMem_id());
        }

        return saveChatCon;
    }

    public ChatCon getChatConDetail(Long chat_con_no) throws NotFoundException {
        ChatCon chatCon = chatConRepository.findById(chat_con_no).orElseThrow(() -> new NotFoundException("Not Found ChatCon"));

        return chatCon;
    }


}
