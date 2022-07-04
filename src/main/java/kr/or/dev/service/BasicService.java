package kr.or.dev.service;

import kr.or.dev.dto.mapper.BasicMapperDto;
import kr.or.dev.dto.timeline.basic.BasicUpdateDto;
import kr.or.dev.entity.files.Files;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.basic.Basic;
import kr.or.dev.repository.BasicRepository;
import kr.or.dev.repository.FilesRepository;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import kr.or.dev.repository.mapper.BasicMapperRepository;
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
public class BasicService {

    private final BasicRepository basicRepository;

    public final BasicMapperRepository basicMapperRepository;

    private final ProjectRepository projectRepository;

    private final MemberRepository memberRepository;

    private final FilesRepository filesRepository;

    @Value("${uploadFile.path}")
    private String uploadFolder;

    public long insertBasic(Basic basic, List<MultipartFile> fileList, List<MultipartFile> imgList) throws IOException {
        Basic saveBasic = basicRepository.save(basic);

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
                    paramMap.put("timeline_col", "basic_no");
                    paramMap.put("timeline_col_val", saveBasic);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("file")
                            .paramMap(paramMap)
                            .project(saveBasic.getProject())
                            .member(basic.getMember())
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
                    paramMap.put("timeline_col", "basic_no");
                    paramMap.put("timeline_col_val", saveBasic);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("img")
                            .paramMap(paramMap)
                            .project(saveBasic.getProject())
                            .member(basic.getMember())
                            .build());


                    if (files.getFiles_no() > 0) {
                        // 경로에 파일 저장
                        File uploadFile = new File(files_path, files_upload);
                        imgFile.transferTo(uploadFile);
                    }
                }
            }
        }

        return saveBasic.getBasic_no();
    }

    public void updateBasic(BasicUpdateDto basicUpdateDto, List<MultipartFile> fileList, List<MultipartFile> imgList, List<Long> delFileList) throws IOException {
        Basic findBasic = basicRepository.findById(basicUpdateDto.getBasic_no()).get();
        findBasic.updateBasic(basicUpdateDto.getBasic_cont());

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
                    paramMap.put("timeline_col", "basic_no");
                    paramMap.put("timeline_col_val", findBasic);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("file")
                            .paramMap(paramMap)
                            .project(findBasic.getProject())
                            .member(findBasic.getMember())
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
                    paramMap.put("timeline_col", "basic_no");
                    paramMap.put("timeline_col_val", findBasic);

                    Files files = filesRepository.save(Files.builder()
                            .files_name(files_name)
                            .files_path(files_path)
                            .files_upload(files_upload)
                            .files_size(files_size)
                            .files_kind("img")
                            .paramMap(paramMap)
                            .project(findBasic.getProject())
                            .member(findBasic.getMember())
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

    public void deleteBasic(Long basic_no) {
        Basic findBasic = basicRepository.findById(basic_no).get();
        findBasic.deleteBasic();
    }

    public Basic getBasicDetail(Long basic_no) {
        Basic findBasic = basicRepository.findById(basic_no).get();

        return findBasic;
    }

    public void fixChkBasic(Long basic_no, String basic_fix_chk) {
        Basic basic = getBasicDetail(basic_no);
        basic.fixChkBasic(basic_fix_chk);
    }

    public List<TimeLine> getBasicProList(Long pro_no, Member member) {
        List<TimeLine> resultList = new ArrayList<>();

        Project project = projectRepository.findById(pro_no).get();

        project.getBasicList().forEach(basic -> {
            if(basic.getBasic_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setBasic(basic);
                newTl.setMember(basic.getMember());
                newTl.setFilesList(basic.getFilesList());
                newTl.setFix_chk(basic.getBasic_fix_chk());
                basic.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(basic.getEmoticonList());
                basic.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(basic.getCreateDate());
                if(member.getCollection() != null){
                    if(member.getCollection().getBasicList().contains(basic)){
                        newTl.setColl_chk(1);
                    }
                }
                resultList.add(newTl);
            }
        });

        return resultList;
    }

    public List<BasicMapperDto> getBasicSearchList(Map paramMap) {
        List<BasicMapperDto> basicSearchList = basicMapperRepository.getBasicSearchList(paramMap);

        return basicSearchList;
    }

//    public List<TimeLine> getBasicCollList(Long mem_no) {
//        Member member = memberRepository.findById(mem_no).get();
//
//        List<TimeLine> resultList = new ArrayList<>();
//
//        member.getCollectionList().forEach(collection -> {
//            TimeLine newTl = new TimeLine();
//            newTl.setBasic(collection.getBasic());
//            newTl.setMember(collection.getBasic().getMember());
//            newTl.setFilesList(collection.getBasic().getFilesList());
//            newTl.setFix_chk(collection.getBasic().getBasic_fix_chk());
//            newTl.setEmoticonList(collection.getBasic().getEmoticonList());
//            newTl.setReplyList(collection.getBasic().getReplyList());
//            resultList.add(newTl);
//        });
//
//        return resultList;
//    }

    public List<TimeLine> getMyBasicList(Long mem_no) {
        Member member = memberRepository.findById(mem_no).get();

        List<TimeLine> resultList = new ArrayList<>();

        member.getBasicList().forEach(basic -> {
            TimeLine newTl = new TimeLine();
            newTl.setBasic(basic);
            newTl.setMember(basic.getMember());
            newTl.setFilesList(basic.getFilesList());
            newTl.setFix_chk(basic.getBasic_fix_chk());
            newTl.setEmoticonList(basic.getEmoticonList());
            newTl.setReplyList(basic.getReplyList());
            resultList.add(newTl);
        });

        return resultList;
    }


}
