package kr.or.dev.controller;

import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.service.FilesService;
import kr.or.dev.service.MemberService;
import kr.or.dev.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FilesController {

    private final FilesService filesService;

    private final MemberService memberService;

    private final ProjectService projectService;

    @GetMapping("/view")
    public void memPic(@RequestParam("files_no") Long files_no, HttpServletResponse response) throws IOException {

        Files files = filesService.getFilesDetail(files_no);

        String filePath = files.getFiles_path() + files.getFiles_upload();

        File file = new File(filePath);
        if(!file.isFile()){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("<script type='text/javascript'>alert('조회된 정보가 없습니다.'); self.close();</script>");
            out.flush();
            return;
        }

        FileInputStream fis = null;
        new FileInputStream(file);

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

    @GetMapping("fileBox")
    public String filesBox(@RequestParam("mem_id") String mem_id,
                           Model model) {
        Member member = memberService.getMemberDetail(mem_id);
        List<Files> filesList = member.getFilesList();

        model.addAttribute("filesList", filesList);

        return "fileBox";
    }

    @GetMapping("/proFilesBox")
    public String proFileBox(@RequestParam("pro_no") Long pro_no,
                             Model model) {

        Project project = projectService.getProDetail(pro_no);

        List<Files> filesList = project.getFilesList();

        model.addAttribute("filesKind", "project");
        model.addAttribute("project", project);
        model.addAttribute("filesList", filesList);

        return "fileBox";

    }

    @GetMapping("/download")
    public void fileDownload(@RequestParam("files_no") Long files_no,
                               HttpServletResponse response) throws IOException {

        Files files = filesService.getFilesDetail(files_no);

        String filePath = files.getFiles_path();

        String fileUploadName = files.getFiles_upload();

        response.setHeader("Content-Disposition", "attachment; filename=\""+files.getFiles_name()+"\"");
        response.setContentType("application/octet-stream");

        System.out.println(files.getFiles_name());
        System.out.println(files.getFiles_path());
        System.out.println(files.getFiles_upload());

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
}
