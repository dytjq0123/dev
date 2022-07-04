package kr.or.dev.service;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.talk.chat.Chat;
import kr.or.dev.entity.talk.chat_con.ChatConFile;
import kr.or.dev.entity.talk.chat_file.ChatFile;
import kr.or.dev.repository.ChatConFileRepository;
import kr.or.dev.repository.ChatFileRepository;
import kr.or.dev.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatFileService {

    private final ChatFileRepository chatFileRepository;

    private final ChatRepository chatRepository;

    private final ChatConFileRepository chatConFileRepository;

    @Value("${uploadFile.path}")
    private String uploadFolder;

    public List<ChatFile> getChatFileList(Long chat_no) {
        List<ChatFile> chatFileList = chatFileRepository.findByChatNo(chat_no);

        return chatFileList;
    }

    public ChatFile insertChatFile(MultipartFile sendFile,
                              Long chat_no,
                              UserDetailsImpl userDetails) throws IOException {

        String file_path = uploadFolder;	// 파일 경로
        String file_name = sendFile.getOriginalFilename();
        String file_upload = UUID.randomUUID().toString();	// 파일 업로드명
        String file_type = file_name.substring(file_name.indexOf(".") + 1).toLowerCase();
        Map paramMap;

        Chat chat = chatRepository.findById(chat_no).get();

        Member member = userDetails.getMember();

        ChatFile saveChatFile = chatFileRepository.save(ChatFile.builder()
                .chat_file_name(file_name)
                .chat_file_path(file_path)
                .chat_file_upload(file_upload)
                .chat(chat)
                .member(member)
                .type(file_type)
                .build());

        ChatConFile saveChatConFile = chatConFileRepository.save(ChatConFile.builder()
                .chat(chat)
                .chatFile(saveChatFile)
                .build());

        chat.setChatInfo(saveChatFile.getChat_file_name(),
                saveChatFile.getCreateDate().format(DateTimeFormatter.ofPattern("MM/dd a HH:mm")).toString(),
                chat.getMemberList().size());

        if(chat.getMemberList().size() == 2){
            chat.setPtn_id(chat.getMemberList().get(1).getMem_id());
        }


        // 첨부파일
        if (!sendFile.isEmpty()) {

            if (saveChatConFile.getChat_con_file_no() > 0) {
                // 경로에 파일 저장
                File uploadFile = new File(file_path, file_upload);
                sendFile.transferTo(uploadFile);
            }
        }

        return saveChatFile;


    }

    public ChatFile getChatFileDetail(Long chat_file_no) {
        ChatFile chatFile = chatFileRepository.findById(chat_file_no).get();

        return chatFile;
    }


}
