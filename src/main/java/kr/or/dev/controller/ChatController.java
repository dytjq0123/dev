package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.chat.ChatUpdateDto;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.partner.Partner;
import kr.or.dev.entity.talk.chat.Chat;
import kr.or.dev.entity.talk.chat_con.ChatCon;
import kr.or.dev.entity.talk.chat_file.ChatFile;
import kr.or.dev.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    private final MemberService memberService;

    private final PartnerService partnerService;

    private final ChatConService chatConService;

    private final ChatFileService chatFileService;


    @PostMapping("/search")
    @ResponseBody
    public Map chatList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List chatList = chatService.getChatList(userDetails.getMember().getMem_no());

        Map map = new HashMap();
        map.put("chatList", chatList);

        return map;
    }

    @GetMapping("/createChat")
    public String createChat(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) throws UnknownHostException {

        Member member = userDetails.getMember();

        List<Partner> myPtnList = partnerService.getMyPtnList(member.getMem_no());

        model.addAttribute("member", member);
        model.addAttribute("myPtnList", myPtnList);

        return "chat/createChat";
    }

    @GetMapping("/insertMulti")
    public String insertMulti(@RequestParam("ptn") List<String> ptnIdList,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              HttpServletRequest request,
                              Model model) {

        List<Member> partnerList = new ArrayList<>();

        String chat_title = "";

        if(ptnIdList != null){
            for (String ptnId : ptnIdList) {
                Member partnerMember = memberService.getMemberDetail(ptnId);
                chat_title += (chat_title.isEmpty() ? "" : ", ") + partnerMember.getMem_nick();
                partnerList.add(partnerMember);
            }
        }

        Member member = userDetails.getMember();

        long chat_no = chatService.insertChat(member, chat_title, null, request);

        Chat chat = chatService.getChatDetail(chat_no);

        chatService.addChatUser(chat_no, partnerList);

        List chatUserList = chatService.chatUserList(chat_no);

        model.addAttribute("chat", chat);
        model.addAttribute("member", member);
        model.addAttribute("chatUserList", chatUserList);

        return "chat/chat";

    }

    @GetMapping("/insert")
    public String insertChat(@RequestParam("mem_id") String mem_id,
                             @RequestParam("chat_title") String chat_title,
                             @RequestParam("ptn_id") String ptn_id,
                             HttpServletRequest request,
                             Model model) throws UnknownHostException {

        Member member = memberService.getMemberDetail(mem_id);

        Member partner = null;
        if(!ptn_id.isEmpty()){
            partner = memberService.getMemberDetail(ptn_id);
        }

        long chat_no = chatService.insertChat(member, chat_title, partner, request);

        Chat chat = chatService.getChatDetail(chat_no);

        List chatUserList = chatService.chatUserList(chat_no);

        model.addAttribute("chat", chat);
        model.addAttribute("member", member);
        model.addAttribute("chatUserList", chatUserList);

        return "chat/chat";

    }

    @GetMapping("/detail")
    public String detailChat(@RequestParam("chat_no") Long chat_no,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) {

        Chat chat = chatService.getChatDetail(chat_no);

        Member member = userDetails.getMember();

        model.addAttribute("member", member);
        model.addAttribute("chat", chat);

        return "chat/chatRoom";

    }

    @PostMapping("/deleteChat")
    @ResponseBody
    public long deleteChat(@RequestParam("chat_no") Long chat_no,
                           @RequestParam("mem_id") String mem_id) {
        Member member = memberService.getMemberDetail(mem_id);

        chatService.deleteChat(chat_no, member);

        return chat_no;
    }

    @PostMapping("/insertChatCon")
    @ResponseBody
    public Map insertChatCon(@RequestParam("msg") String chat_cont,
                                 @RequestParam("chat_no") Long chat_no,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();

        Chat chat = chatService.getChatDetail(chat_no);

        ChatCon chatCon = chatConService.insertChatCon(ChatCon.builder()
                .chat_cont(chat_cont)
                .chat(chat)
                .member(member)
                .build());

        Map map = new HashMap<>();
        map.put("mem_id", member.getMem_id());
        map.put("chat_cont", chatCon.getChat_cont());
        map.put("time", chatCon.getChat_con_time().format(DateTimeFormatter.ofPattern("MM/dd a HH:mm")));

//        return chatCon;
        return map;

    }

    @PostMapping("/insertChatFile")
    @ResponseBody
    public Map insertChatFile(@RequestParam("sendFile") MultipartFile sendFile,
                              @RequestParam("chat_no") Long chat_no,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        Member member = userDetails.getMember();

        Chat chat = chatService.getChatDetail(chat_no);

        ChatFile chatFile = chatFileService.insertChatFile(sendFile, chat_no, userDetails);

        Map map = new HashMap();
        map.put("mem_id", member.getMem_id());
        map.put("chat_file_no", chatFile.getChat_file_no());
        map.put("time", chatFile.getCreateDate().format(DateTimeFormatter.ofPattern("MM/dd a HH:mm")));

        return map;
    }

    @PostMapping("/chatUserList")
    @ResponseBody
    public Map chatUserList(@RequestParam("chat_no") Long chat_no) {

        List chatUserList = chatService.chatUserList(chat_no);

        Map map = new HashMap();
        map.put("chatUserList", chatUserList);

        return map;
    }

    @GetMapping("/img")
    public void img(@RequestParam("chat_file_no") Long chat_file_no,
                    HttpServletResponse response) throws FileNotFoundException {

        ChatFile chatFile = chatFileService.getChatFileDetail(chat_file_no);

        String filePath = chatFile.getChat_file_path() + chatFile.getChat_file_upload();

        File file = new File(filePath);

        FileInputStream fis = new FileInputStream(file);

        BufferedInputStream in = null;
        ByteArrayOutputStream bStream = null;
        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            bStream = new ByteArrayOutputStream();
            int imgByte;
            while ((imgByte = in.read()) != -1) {
                bStream.write(imgByte);
            }

            String type = "";
            String ext = FilenameUtils.getExtension(file.getName());
            if (ext != null && !"".equals(ext)) {
                if ("jpg".equals(ext.toLowerCase())) {
                    type = "image/jpeg";
                } else {
                    type = "image/" + ext.toLowerCase();
                }

            }

            response.setHeader("Content-Type", type);
            response.setContentLength(bStream.size());

            bStream.writeTo(response.getOutputStream());

            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bStream != null) {
                try {
                    bStream.close();
                } catch (Exception est) {
                    est.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ei) {
                    ei.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception efis) {
                    efis.printStackTrace();
                }
            }
        }

    }

    @GetMapping("/fileDownload")
    public void fileDownload(@RequestParam("chat_file_no") Long chat_file_no,
                             HttpServletResponse response) throws IOException {

        ChatFile chatFile = chatFileService.getChatFileDetail(chat_file_no);

        String filePath = chatFile.getChat_file_path();

        String fileUploadName = chatFile.getChat_file_upload();

        response.setHeader("Content-Disposition", "attachment; filename=\""+chatFile.getChat_file_name()+"\"");
        response.setContentType("application/octet-stream");

        ServletOutputStream sos = null;
        FileInputStream fis = null;

        // file 객체 생성
        File f = new File(filePath, fileUploadName);

        try {
            if(f.length() > 0){
                // file 입출력을 위한 준비
                sos = response.getOutputStream();


                fis = new FileInputStream(f);
                byte[] b = new byte[512];
                int len;

                while ((len = fis.read(b)) != -1) {
                    sos.write(b, 0, len);
                }
            }
        } catch (Exception e) {
            throw new FileNotFoundException("파일이 없습니다.");
        } finally {
            if(fis != null){
                fis.close();
            }
            if(sos != null){
                sos.close();
            }
        }


    }

    @PostMapping("/chatModify")
    @ResponseBody
    public Map chatModify(@RequestBody ChatUpdateDto chatUpdateDto) {

        chatService.updateChat(chatUpdateDto);

        Map map = new HashMap();
        map.put("chat_title", chatUpdateDto.getChat_title());

        return map;

    }

    @PostMapping("/pop")
    @ResponseBody
    public Map memChk(@RequestParam("mem_id") String mem_id) {
        Member member = memberService.getMemberDetail(mem_id);

        Map map = new HashMap();
        map.put("mem_nick", member.getMem_nick());
        map.put("mem_id", member.getMem_id());
        map.put("mem_phone", member.getMem_phone());

        return map;
    }

    @PostMapping("/pop2")
    @ResponseBody
    public Map memChk2(@RequestParam("mem_id") String mem_id) {
        Member member = memberService.getMemberDetail(mem_id);

        Map map = new HashMap();
        map.put("mem_nick", member.getMem_nick());
        map.put("mem_id", member.getMem_id());
        map.put("mem_phone", member.getMem_phone());

        return map;
    }

}
