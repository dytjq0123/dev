package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.reply.ReplyInsertDto;
import kr.or.dev.dto.timeline.reply.ReplyUpdateDto;
import kr.or.dev.entity.member.Member;
import kr.or.dev.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rep")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/insert")
    public String repInsert(ReplyInsertDto replyInsertDto,
                            @RequestParam(value="articleFile", required=false) List<MultipartFile> fileList,
                            @RequestParam(value="imageFile", required=false) List<MultipartFile> imgList,
                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                            Model model) throws IOException {

        Member member = userDetails.getMember();

        long proNo = replyService.insertRep(replyInsertDto, fileList, imgList, userDetails);

        model.addAttribute("message", "댓글이 등록되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + replyInsertDto.getPro_no());

        return "alertMessage";

    }

    @PostMapping("/update")
    public String repUpdate(ReplyUpdateDto replyUpdateDto,
                            @RequestParam(value="articleFile", required=false) List<MultipartFile> fileList,
                            @RequestParam(value="imageFile", required=false) List<MultipartFile> imgList,
                            @RequestParam(value="del_files_no", required=false) List<Long> delFileList,
                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                            Model model) throws IOException {

        replyService.updateRep(replyUpdateDto, fileList, imgList, delFileList, userDetails);

        model.addAttribute("message", "댓글이 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + replyUpdateDto.getPro_no());

        return "alertMessage";

    }

    @GetMapping("/delete")
    public String repDelete(@RequestParam("pro_no") Long pro_no,
                            @RequestParam("timeline_no") Long rep_no,
                            Model model) {

        replyService.deleteRep(rep_no);

        model.addAttribute("message", "댓글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";
    }
}
