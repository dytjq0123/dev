package kr.or.dev.service;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.member.MemberSignUpDto;
import kr.or.dev.dto.member.MemberUpdateProfileDto;
import kr.or.dev.entity.Role;
import kr.or.dev.entity.group.important.Important;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.collection.Collection;
import kr.or.dev.repository.CollectionRepository;
import kr.or.dev.repository.ImportantRepository;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final ProjectRepository projectRepository;

    private final ImportantRepository importantRepository;

    private final CollectionRepository collectionRepository;

    @Value("${profileImg.path}")
    private String profileUploadFolder;

    public int save(MemberSignUpDto memberSignUpDto, MultipartFile file) throws IOException {

        String pic_path = "/Users/owner/Desktop/study/dev/src/main/resources/static/image/";
        String pic_name = "user-pic-sample.png";
        String pic_upload = "user-pic-sample.png";

        if(!file.isEmpty()){

            pic_path = profileUploadFolder;

            File newFile = new File(pic_path);
            if(!newFile.exists()){
                newFile.mkdirs();
            }

            pic_name = file.getOriginalFilename();

            pic_upload = UUID.randomUUID().toString();

            File saveFile = new File(pic_path, pic_upload);

            file.transferTo(saveFile);
        }

        Member member = memberRepository.save(Member.builder()
                .mem_id(memberSignUpDto.getMem_id())
                .mem_nick(memberSignUpDto.getMem_nick())
                .mem_pw(passwordEncoder.encode(memberSignUpDto.getMem_pw()))
                .mem_name(memberSignUpDto.getMem_name())
                .mem_phone(memberSignUpDto.getMem_phone())
                .mem_alim_kind(null)
                .mem_chk("y")
                .mem_howjoin(memberSignUpDto.getMem_howjoin())
                .mem_pic_name(pic_name)
                .mem_pic_path(pic_path)
                .mem_pic_upload(pic_upload)
                .role(Role.USER)
                .build());

        Important important = importantRepository.save(Important.builder()
                .member(member)
                .build());

        member.setImportant(important);

        Collection collection = collectionRepository.save(Collection.builder()
                .member(member)
                .build());

        member.setCollection(collection);


        if(member != null){
            return 1;
        }else {
            return 0;
        }

    }

    public void changeProfile(MemberUpdateProfileDto memberUpdateProfileDto,
                              MultipartFile file,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        Member member = memberRepository.findByMemId(memberUpdateProfileDto.getMem_id()).get();

        String pic_path = userDetails.getMember().getMem_pic_path();
        String pic_name = userDetails.getMember().getMem_pic_name();
        String pic_upload = userDetails.getMember().getMem_pic_upload();

        if(!file.isEmpty()){

            pic_path = profileUploadFolder;

            File newFile = new File(pic_path);
            if(!newFile.exists()){
                newFile.mkdirs();
            }

            pic_name = file.getOriginalFilename();

            pic_upload = UUID.randomUUID().toString();

            File saveFile = new File(pic_path, pic_upload);

            file.transferTo(saveFile);
        }

        member.updateProfile(memberUpdateProfileDto.getMem_name(),
                            memberUpdateProfileDto.getMem_nick(),
                            pic_name,
                            pic_path,
                            pic_upload);

        // 인증 객체에 변경된 Member 객체 set
        userDetails.setMember(member);


    }

    public void changePw(String mem_id, String mem_pw) {
        Member member = memberRepository.findByMemId(mem_id).get();
        member.updatePassword(mem_pw);
    }

    public void changePwLogIn(String mem_pw, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = memberRepository.findByMemId(userDetails.getMember().getMem_id()).get();
        member.updatePassword(mem_pw);

        userDetails.setMember(member);
    }

    public Member getMemberDetail(String mem_id) {
        Member member = memberRepository.findByMemId(mem_id).get();

        return member;
    }

    public int chMemId(String mem_id) {
        int idChk = memberRepository.idChk(mem_id);
        return idChk;
    }

    public int chkMemNick(String mem_nick) {
        int nickChk = memberRepository.nickChk(mem_nick);
        return nickChk;
    }

    public List<Member> getMemAllList() {
        List<Member> memberList = memberRepository.findAll();
        return memberList;
    }

    public String findId(String mem_name, String mem_phone) {
        String result = memberRepository.findId(mem_name, mem_phone);
        return result;
    }

    public Member findById(Long mem_no) {
        Member member = memberRepository.findById(mem_no).get();
        return member;
    }

    public String getMemChk(String mem_name, String mem_id, String mem_phone) {
        String result = memberRepository.getMemChk(mem_name, mem_id, mem_phone);
        return result;
    }

    public int getMemAllCnt() {
        int memAllCnt = memberRepository.getMemAllCnt();
        return memAllCnt;
    }

    public int getMemHowjoinCnt(String mem_howjoin) {
        int memHowjoinCnt = memberRepository.getMemHowjoinCnt(mem_howjoin);
        return memHowjoinCnt;
    }

    public Page<Member> findAll(Pageable pageable) {
        Page<Member> all = memberRepository.findAll(pageable);
        return all;
    }

    // 회원이 참여중인 모든 프로젝트의 참여자 리스트
    public List<Member> getMemProMemList(Long mem_no) {
        List<Member> resultList = new ArrayList<>();

        Member member = memberRepository.findById(mem_no).get();

        member.getProjectList().forEach(project -> {
            resultList.addAll(project.getMemberList());
        });

        // 중복 제거
        List<Member> newResult = resultList.stream().distinct().collect(Collectors.toList());

        return newResult;
    }

    public void removeMember(String mem_id) {
        Member member = memberRepository.findByMemId(mem_id).get();

        member.removeMember();
    }

}
