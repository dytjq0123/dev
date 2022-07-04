package kr.or.dev.service;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.reply.ReplyInsertDto;
import kr.or.dev.dto.timeline.reply.ReplyUpdateDto;
import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.reply.Reply;
import kr.or.dev.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final BasicRepository basicRepository;

    private final TaskRepository taskRepository;

    private final ScheduleRepository scheduleRepository;

    private final TodoRepository todoRepository;

    private final VoteRepository voteRepository;

    private final FilesRepository filesRepository;

    private final ProjectRepository projectRepository;

    @Value("${uploadFile.path}")
    private String uploadFolder;

    public long insertRep(ReplyInsertDto replyInsertDto,
                          List<MultipartFile> fileList,
                          List<MultipartFile> imgList,
                          UserDetailsImpl userDetails) throws IOException {

        Member member = userDetails.getMember();

        TimeLine timeLine = new TimeLine();
        Project project = projectRepository.findById(replyInsertDto.getPro_no()).get();

        String timeline_col = replyInsertDto.getTimeline_col();
        Long timeline_no = replyInsertDto.getTimeline_no();
        String rep_cont = replyInsertDto.getRep_cont();

        if(timeline_col.equals("basic_no")){
            timeLine.setBasic(basicRepository.findById(timeline_no).get());
        }else if(timeline_col.equals("task_no")){
            timeLine.setTask(taskRepository.findById(timeline_no).get());
        }else if(timeline_col.equals("schd_no")){
            timeLine.setSchedule(scheduleRepository.findById(timeline_no).get());
        }else if(timeline_col.equals("todo_no")){
            timeLine.setTodo(todoRepository.findById(timeline_no).get());
        }else if(timeline_col.equals("vote_no")){
            timeLine.setVote(voteRepository.findById(timeline_no).get());
        }


        Reply saveReply = replyRepository.save(Reply.builder()
                .rep_cont(rep_cont)
                .member(member)
                .timeLine(timeLine)
                .build());

        String files_path = uploadFolder;	// 파일 경로
        String files_name = "";
        String files_upload = "";	// 파일 업로드명
        String files_size = "";
        Map paramMap;

        DecimalFormat df = new DecimalFormat("#,###.0");
        DecimalFormat df1 = new DecimalFormat("#,###");

        // 첨부파일 List
        if(fileList != null) {
            for (MultipartFile artFile : fileList) {

                if (!artFile.isEmpty()) {

                    files_name = artFile.getOriginalFilename();		// 파일 이름
                    files_upload = UUID.randomUUID().toString();	// 파일 업로드명

                    // 파일 크기
                    double fileSize = artFile.getSize();
                    files_size = df1.format(fileSize) + " byte";

                    if (Math.round(fileSize*10)/10 >= 1024) {
                        fileSize = fileSize/1024;
                        files_size = df.format(fileSize) + " KB";
                    }
                    if (Math.round(fileSize*10)/10 >= 1024) {
                        files_size = df.format(fileSize/1024) + " MB";
                    }

                    // paramMap setting
                    paramMap = new HashMap<String, Object>();
                    paramMap.put("timeline_col", "reply_no");
                    paramMap.put("timeline_col_val", saveReply);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("file")
                            .paramMap(paramMap)
                            .project(project)
                            .member(member)
                            .build());


                    if (files.getFiles_no() > 0) {
                        // 경로에 파일 저장
                        File uploadFile = new File(files_path, files_upload);
                        artFile.transferTo(uploadFile);
                    }
                }
            }
        }

        // ImageFile List
        if(imgList != null) {
            for (MultipartFile imgFile : imgList) {

                if (!imgFile.isEmpty()) {

                    files_name = imgFile.getOriginalFilename();		// 파일 이름
                    files_upload = UUID.randomUUID().toString();	// 파일 업로드명

                    // 파일 크기
                    double fileSize = imgFile.getSize()/1024.0;
                    files_size = Math.round(fileSize*10)/10.0 + " KB";
                    if (fileSize >= 1000) {
                        files_size = Math.round(fileSize/1024.0*10)/10.0 + " MB";
                    }

                    // paramMap setting
                    paramMap = new HashMap<String, Object>();
                    paramMap.put("timeline_col", "reply_no");
                    paramMap.put("timeline_col_val", saveReply);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("img")
                            .paramMap(paramMap)
                            .project(project)
                            .member(member)
                            .build());


                    if (files.getFiles_no() > 0) {
                        // 경로에 파일 저장
                        File uploadFile = new File(files_path, files_upload);
                        imgFile.transferTo(uploadFile);
                    }
                }
            }
        }

        return saveReply.getRep_no();
    }

    public void updateRep(ReplyUpdateDto replyUpdateDto,
                          List<MultipartFile> fileList,
                          List<MultipartFile> imgList,
                          List<Long> delFileList,
                          UserDetailsImpl userDetails) throws IOException {
        Reply findReply = replyRepository.findById(replyUpdateDto.getRep_no()).get();
        findReply.updateReply(replyUpdateDto.getRep_cont());

        Project project = projectRepository.findById(replyUpdateDto.getPro_no()).get();


        // 삭제할 파일리스트
        if(delFileList != null){
            delFileList.forEach(files_no -> {
                Files files = filesRepository.getById(files_no);
                File savedFile = new File(files.getFiles_path() + files.getFiles_name());
                if(savedFile.exists()){
                    savedFile.delete();
                }
                filesRepository.deleteById(files_no);
            });
        }

        String files_path = uploadFolder;	// 파일 경로
        String files_name = "";
        String files_upload = "";	// 파일 업로드명
        String files_size = "";
        Map paramMap;

        DecimalFormat df = new DecimalFormat("#,###.0");
        DecimalFormat df1 = new DecimalFormat("#,###");

        // 첨부파일 List
        if(fileList != null) {
            for (MultipartFile artFile : fileList) {

                if (!artFile.isEmpty()) {

                    files_name = artFile.getOriginalFilename();		// 파일 이름
                    files_upload = UUID.randomUUID().toString();	// 파일 업로드명

                    // 파일 크기
                    double fileSize = artFile.getSize();
                    files_size = df1.format(fileSize) + " byte";

                    if (Math.round(fileSize*10)/10 >= 1024) {
                        fileSize = fileSize/1024;
                        files_size = df.format(fileSize) + " KB";
                    }
                    if (Math.round(fileSize*10)/10 >= 1024) {
                        files_size = df.format(fileSize/1024) + " MB";
                    }

                    // paramMap setting
                    paramMap = new HashMap<String, Object>();
                    paramMap.put("timeline_col", "reply_no");
                    paramMap.put("timeline_col_val", findReply);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("file")
                            .paramMap(paramMap)
                            .project(project)
                            .member(findReply.getMember())
                            .build());


                    if (files.getFiles_no() > 0) {
                        // 경로에 파일 저장
                        File uploadFile = new File(files_path, files_upload);
                        artFile.transferTo(uploadFile);
                    }
                }
            }
        }

        // ImageFile List
        if(imgList != null) {
            for (MultipartFile imgFile : imgList) {

                if (!imgFile.isEmpty()) {

                    files_name = imgFile.getOriginalFilename();		// 파일 이름
                    files_upload = UUID.randomUUID().toString();	// 파일 업로드명

                    // 파일 크기
                    double fileSize = imgFile.getSize()/1024.0;
                    files_size = Math.round(fileSize*10)/10.0 + " KB";
                    if (fileSize >= 1000) {
                        files_size = Math.round(fileSize/1024.0*10)/10.0 + " MB";
                    }

                    // paramMap setting
                    paramMap = new HashMap<String, Object>();
                    paramMap.put("timeline_col", "reply_no");
                    paramMap.put("timeline_col_val", findReply);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("img")
                            .paramMap(paramMap)
                            .project(project)
                            .member(findReply.getMember())
                            .build());


                    if (files.getFiles_no() > 0) {
                        // 경로에 파일 저장
                        File uploadFile = new File(files_path, files_upload);
                        imgFile.transferTo(uploadFile);
                    }
                }
            }
        }
    }

    public void deleteRep(Long rep_no) {
        Reply reply = replyRepository.findById(rep_no).get();
        reply.deleteReply();
    }

//    public List<TimeLine> getRepList() {}

}
