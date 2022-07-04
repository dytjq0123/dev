package kr.or.dev.controller;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.basic.BasicUpdateDto;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.service.BasicService;
import kr.or.dev.service.FilesService;
import kr.or.dev.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/basic")
public class BasicController {

    private final BasicService basicService;

    private final FilesService filesService;

    private final ProjectService projectService;

    @Value("${uploadFile.path}")
    private String uploadFolder;

    @PostMapping("/insert")
    public String basicInsert(@RequestParam("pro_no") Long pro_no,
                              @RequestParam("basic_cont") String basic_cont,
                              @RequestParam(value = "articleFile", required = false)List<MultipartFile> fileList,
                              @RequestParam(value="imageFile", required=false) List<MultipartFile> imgList,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              Model model) throws IOException {

        Member member = userDetails.getMember();
        Project project = projectService.getProDetail(pro_no);

        basicService.insertBasic(Basic.builder()
                .basic_cont(basic_cont)
                .project(project)
                .member(member)
                .build(), fileList, imgList);



        model.addAttribute("message", "글이 등록되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";

    }

    @PostMapping("/update")
    public String basicUpdate(BasicUpdateDto basicUpdateDto,
                              @RequestParam(value="articleFile", required=false) List<MultipartFile> fileList,
                              @RequestParam(value="imageFile", required=false) List<MultipartFile> imgList,
                              @RequestParam(value="del_files_no", required=false) List<Long> delFileList,
                              Model model) throws IOException {

        basicService.updateBasic(basicUpdateDto, fileList, imgList, delFileList);
        Basic basic = basicService.getBasicDetail(basicUpdateDto.getBasic_no());

        model.addAttribute("message", "글이 수정되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + basic.getProject().getPro_no());

        return "alertMessage";
    }

    @GetMapping("/delete")
    public String basicDelete(@RequestParam("timeline_no") Long basic_no, Model model) {
        Basic basic = basicService.getBasicDetail(basic_no);
        Long pro_no = basic.getProject().getPro_no();

        basicService.deleteBasic(basic_no);

        model.addAttribute("message", "글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/pro/detail/" + pro_no);

        return "alertMessage";
    }

    @GetMapping("/fix")
    public String basicFix(@RequestParam("timeline_no") Long basic_no,
                           @RequestParam("fix_chk") String basic_fix_chk,
                           Model model) {

        Basic basic = basicService.getBasicDetail(basic_no);
        basicService.fixChkBasic(basic_no, basic_fix_chk);


        if(basic_fix_chk.equalsIgnoreCase("y")){
            model.addAttribute("message", "상단고정 되었습니다.");
        }else {
            model.addAttribute("message", "상단고정 해제 되었습니다.");
        }
        model.addAttribute("searchUrl", "/pro/detail/" + basic.getProject().getPro_no());

        return "alertMessage";

    }

}
