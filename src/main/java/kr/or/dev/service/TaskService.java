package kr.or.dev.service;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.task.TaskInsertDto;
import kr.or.dev.dto.timeline.task.TaskUpdateDto;
import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.task.Task;
import kr.or.dev.repository.FilesRepository;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import kr.or.dev.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    private final MemberRepository memberRepository;

    private final FilesRepository filesRepository;

    private final ProjectService projectService;

    @Value("${uploadFile.path}")
    private String uploadFolder;

    public long insertTask(TaskInsertDto taskInsertDto,
                           List<String> tu_emailList,
                           List<MultipartFile> fileList,
                           List<MultipartFile> imgList,
                           UserDetailsImpl userDetails) throws IOException {

        Member member = userDetails.getMember();
        Project project = projectService.getProDetail(taskInsertDto.getPro_no());

        String task_start_date = taskInsertDto.getTask_start_date();
        String task_end_date = taskInsertDto.getTask_end_date();

        if(!task_start_date.isEmpty()){
            task_start_date = task_start_date.substring(0,10);
        }

        if(!task_end_date.isEmpty()){
            task_end_date = task_end_date.substring(0,10);
        }

        Task saveTask = taskRepository.save(Task.builder()
                .task_title(taskInsertDto.getTask_title())
                .task_state(taskInsertDto.getTask_state())
                .task_start_date(task_start_date)
                .task_end_date(task_end_date)
                .task_rate(taskInsertDto.getTask_rate())
                .task_priority(taskInsertDto.getTask_priority())
                .task_cont(taskInsertDto.getTask_cont())
                .project(project)
                .member(member)
                .build());

        if(tu_emailList != null){
            tu_emailList.forEach(mem -> {
                Member member1 = memberRepository.findByMemId(mem).get();
                saveTask.addTaskMember(member1);
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
                    paramMap.put("timeline_col", "task_no");
                    paramMap.put("timeline_col_val", saveTask);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("file")
                            .paramMap(paramMap)
                            .project(saveTask.getProject())
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
                    paramMap.put("timeline_col", "task_no");
                    paramMap.put("timeline_col_val", saveTask);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("img")
                            .paramMap(paramMap)
                            .project(saveTask.getProject())
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

        return saveTask.getTask_no();

    }

    public void updateTask(TaskUpdateDto taskUpdateDto,
                           List<String> tu_emailList,
                           List<MultipartFile> fileList,
                           List<MultipartFile> imgList,
                           List<Long> delFileList,
                           List<Long> delTuList) throws IOException {

        Task findTask = taskRepository.findById(taskUpdateDto.getTask_no()).get();

        findTask.updateTask(taskUpdateDto.getTask_title(),
                taskUpdateDto.getTask_state(),
                taskUpdateDto.getTask_cont(),
                taskUpdateDto.getTask_start_date(),
                taskUpdateDto.getTask_end_date(),
                taskUpdateDto.getTask_rate(),
                taskUpdateDto.getTask_priority());

        if(delTuList != null){
            delTuList.forEach(delMember -> {
                Member member = memberRepository.findById(delMember).get();
                findTask.removeTaskMember(member);
            });
        }

        if(tu_emailList != null){
            tu_emailList.forEach(member -> {
                Member addMember = memberRepository.findByMemId(member).get();
                findTask.addTaskMember(addMember);
            });
        }

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
                    paramMap.put("timeline_col", "task_no");
                    paramMap.put("timeline_col_val", findTask);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("file")
                            .paramMap(paramMap)
                            .project(findTask.getProject())
                            .member(findTask.getMember())
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
                    paramMap.put("timeline_col", "task_no");
                    paramMap.put("timeline_col_val", findTask);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("img")
                            .paramMap(paramMap)
                            .project(findTask.getProject())
                            .member(findTask.getMember())
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

    public void deleteTask(Long task_no) {
        Task task = taskRepository.findById(task_no).get();
        task.deleteTask();
    }

    public List<TimeLine> getTaskProList(Long pro_no, Member member) {
        List<TimeLine> resultList = new ArrayList<>();

        Project project = projectRepository.findById(pro_no).get();

        project.getTaskList().forEach(task -> {
            if(task.getTask_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setTask(task);
                newTl.setMember(task.getMember());
                newTl.setFilesList(task.getFilesList());
                newTl.setFix_chk(task.getTask_fix_chk());
                task.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(task.getEmoticonList());
                task.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(task.getCreateDate());
                if(member.getCollection() != null){
                    if(member.getCollection().getTaskList().contains(task)){
                        newTl.setColl_chk(1);
                    }
                }
                resultList.add(newTl);
            }
        });

        return resultList;
    }

    public Task getTaskDetail(Long task_no) {
        Task task = taskRepository.findById(task_no).get();

        return task;
    }

    public List<TimeLine> getMyTaskList(Long mem_no) {
        List<TimeLine> resultList = new ArrayList<>();

        Member member = memberRepository.findById(mem_no).get();

        member.getTaskList().forEach(task -> {
            TimeLine newTl = new TimeLine();
            newTl.setTask(task);
            newTl.setMember(task.getMember());
            newTl.setFilesList(task.getFilesList());
            newTl.setFix_chk(task.getTask_fix_chk());
            newTl.setEmoticonList(task.getEmoticonList());
            newTl.setReplyList(task.getReplyList());
            resultList.add(newTl);
        });

        return resultList;
    }

    public List<Project> allTask(Long mem_no) {
        Member member = memberRepository.findById(mem_no).get();

        return member.getProjectList();
    }

//    public List<TimeLine> getTaskCollList(Long mem_no) {
//        Member member = memberRepository.findById(mem_no).get();
//
//        List<TimeLine> resultList = new ArrayList<>();
//
//        member.getCollectionList().forEach(collection -> {
//            TimeLine newTl = new TimeLine();
//            newTl.setTask(collection.getTask());
//            newTl.setMember(collection.getTask().getMember());
//            newTl.setFilesList(collection.getTask().getFilesList());
//            newTl.setFix_chk(collection.getTask().getTask_fix_chk());
//            newTl.setEmoticonList(collection.getTask().getEmoticonList());
//            newTl.setReplyList(collection.getTask().getReplyList());
//            resultList.add(newTl);
//        });
//
//        return resultList;
//
//    }

    public List<Task> findAll() {
        List<Task> taskList = taskRepository.findAll();

        return taskList;
    }

    public void updateState(Long task_no, String task_state) {
        Task task = taskRepository.findById(task_no).get();
        task.updateState(task_state);
    }

    public void updateRate(Long task_no, int task_rate) {
        Task task = taskRepository.findById(task_no).get();
        task.updateRate(task_rate);
    }

    public void fixChkTask(Long task_no, String task_fix_chk) {
        Task task = getTaskDetail(task_no);
        task.fixChkTask(task_fix_chk);
    }


}
