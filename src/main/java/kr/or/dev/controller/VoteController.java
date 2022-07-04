package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.vote.VoteUpdateDto;
import kr.or.dev.entity.timeline.vote.Vote;
import kr.or.dev.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/insert")
    public String voteInsert(@RequestParam("pro_no") Long pro_no,
                             @RequestParam("vote_title") String vote_title,
                             @RequestParam("vi_cont") List<String> vi_contList,
                             @RequestParam("vote_end_time") String vote_end_time,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model) {

        voteService.insertVote(pro_no, vote_title, vi_contList, vote_end_time, userDetails);

        model.addAttribute("message", "글이 등록되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";
    }

    @PostMapping("/update")
    public String voteUpdate(VoteUpdateDto voteUpdateDto,
                             Model model) {

        voteService.updateVote(voteUpdateDto);

        model.addAttribute("message", "글이 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + voteUpdateDto.getPro_no());

        return "alertMessage";
    }

    @GetMapping("/delete")
    public String todoDelete(@RequestParam("timeline_no") Long vote_no,
                             Model model) {
        Vote vote = voteService.getVoteDetail(vote_no);
        voteService.deleteVote(vote_no);

        model.addAttribute("message", "글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + vote.getProject().getPro_no());

        return "alertMessage";
    }

    @GetMapping("/fix")
    public String todoFix(@RequestParam("timeline_no") Long vote_no,
                          @RequestParam("fix_chk") String vote_fix_chk,
                          Model model) {

        Vote vote = voteService.getVoteDetail(vote_no);
        voteService.fixChkVote(vote_no, vote_fix_chk);


        if(vote_fix_chk.equalsIgnoreCase("y")){
            model.addAttribute("message", "상단고정 되었습니다.");
        }else {
            model.addAttribute("message", "상단고정 해제 되었습니다.");
        }
        model.addAttribute("searchUrl", "/pro/detail/" + vote.getProject().getPro_no());

        return "alertMessage";

    }

    @PostMapping("/viu")
    public String voting(@RequestParam("vote_no") Long vote_no,
                         @RequestParam("vi_no") Long vi_no,
                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                         Model model) {

        Vote vote = voteService.getVoteDetail(vote_no);
        voteService.voting(vote_no, vi_no, userDetails);

        model.addAttribute("message", "투표하였습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + vote.getProject().getPro_no());

        return "alertMessage";

    }

    @PostMapping("/resetVote")
    @ResponseBody
    public long resetVote(@RequestParam("vote_no") Long vote_no,
                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        long voteNo = voteService.resetVote(vote_no, userDetails);

        return voteNo;
    }

}
