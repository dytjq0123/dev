package kr.or.dev.service;

import kr.or.dev.dto.mapper.ProjectMapperDto;
import kr.or.dev.dto.project.ProjectUpdateDto;
import kr.or.dev.entity.group.kind.Kind;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.repository.KindRepository;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import kr.or.dev.repository.mapper.ProjectMapperRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapperRepository projectMapperRepository;

    private final MemberRepository memberRepository;

    private final KindRepository kindRepository;

    public long insertPro(Project project) {
        Project savePro = projectRepository.save(project);

        savePro.addProjectMember(project.getMember());

        return savePro.getPro_no();
    }

    public void updatePro(ProjectUpdateDto projectUpdateDto) {
        Project findPro = projectRepository.findById(projectUpdateDto.getPro_no()).get();

        Kind kind = kindRepository.findById(projectUpdateDto.getKind_no()).get();

        findPro.updateProject(projectUpdateDto.getPro_name(), projectUpdateDto.getPro_cont(), kind);
    }

    public void deletePro(Long pro_no) throws NotFoundException {
        Project findPro = projectRepository.findById(pro_no).orElseThrow(() -> new NotFoundException("Not Found Project"));
        findPro.deleteProject();
    }

    public Project getProDetail(Long pro_no) {
        Project findPro = projectRepository.findById(pro_no).get();

        return findPro;
    }

    public Page<Project> getProPageAllList(Pageable pageable) {
        Page<Project> projectList = projectRepository.findAll(pageable);

        return projectList;
    }

    public int getProkindCnt(Long kind_no) {
        int proKindCnt = projectRepository.findByKindNo(kind_no);

        return proKindCnt;
    }

    public List<ProjectMapperDto> getProSearchList(Map paramMap) {
        List<ProjectMapperDto> proSearchList = projectMapperRepository.getProSearchList(paramMap);

        return proSearchList;
    }

    public List<Project> getProAllList() {
        List<Project> projectList = projectRepository.findAll();

        return projectList;
    }

    public int getProMonthCnt(String create_date) {
        int proMonthCnt = projectMapperRepository.getProMonthCnt(create_date);

        return proMonthCnt;
    }

    public List<Project> getMyProList(Long mem_no) {
        Member member = memberRepository.findById(mem_no).get();
        member.getProjectList().forEach(project -> {
            member.setProject(project);
        });

        return member.getProjectList();
    }

    public void updateColor(Long pro_no, String pro_color) {
        Project project = projectRepository.getById(pro_no);
        project.updateColor(pro_color);
    }

    public void addProjectMember(Long pro_no, List<String> mem_idList) {
        Project project = projectRepository.findById(pro_no).get();
        for (String mem_id : mem_idList) {
            Member member = memberRepository.findByMemId(mem_id).get();
            project.addProjectMember(member);
        }
    }

    public void sendMail(Long pro_no, List<String> emailList, String email_con, String mem_id) {
        Member member = memberRepository.findByMemId(mem_id).get();

        Project project = projectRepository.getById(pro_no);

        // 메일 인코딩
        final String bodyEncoding = "UTF-8"; //콘텐츠 인코딩

        String subject = "메일 발송 테스트";
        String fromEmail = "dytjq0123@gmail.com";
        String fromUsername = "SYSTEM MANAGER";
        String toEmail = ""; // 콤마(,)로 여러개 나열
        for (String email : emailList) {
            toEmail += (toEmail.isEmpty() ? "" : ",") + email;
        }

        final String username = "dytjq0123@gmail.com";
        final String password = "cslkmpehlslvwieb";

        // 메일에 출력할 텍스트
        String htmlContent = "";
        htmlContent += "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:500px;margin:0;padding:60px;text-align:left;font-family:'MalgunGothic','맑은 고딕','Malgun Gothic','돋움',Dotum,'굴림',Gulim,Arial,sans-serif;\">";
        htmlContent += 		"<tbody>";
        htmlContent += 			"<tr>";
        htmlContent += 				"<td style=\"margin:0;\">";
        htmlContent += 					"<h1 style=\"margin:0;padding:0;\"><img src=\"http://zchu.xyz/img/flowolf_logo_s.png\"></h1>";
        //htmlContent += 					"<h2 style=\"margin:15px 0 0 0;font-weight:bold;font-size:24px;\">프로젝트 초대</h2>";
        htmlContent += 				"</td>";
        htmlContent += 			"</tr>";
        htmlContent += 			"<tr>";
        htmlContent += 				"<td style=\"margin:0;padding:30px 0 0 0;\">";
        htmlContent += 					"<p style=\"font-size:15px;color:#333;line-height:30px;\">";
        htmlContent +=						"<strong>" + member.getMem_nick() + "(" + member.getMem_id() + ")</strong> 님이<br><strong style=\"color:#746fcc;\">프로젝트(&quot;" + project.getPro_name() + "&quot;)</strong>에 초대합니다.";
        htmlContent +=					"</p>";
        htmlContent +=					"<pre style=\"width:466px;margin:20px 0 0 0;padding:15px;font-size:15px;color:#333;line-height:22px;border:2px dotted #ccc;border-radius:10px;\">";
        htmlContent +=						email_con;
        htmlContent +=					"</pre>";
        htmlContent +=					"<p style=\"margin:20px 0 0 0;font-size:13px;color:#999;\">";
        htmlContent +=						"아래의 버튼을 통해 프로젝트에 참여할 수 있습니다.";
        htmlContent +=					"</p>";

        htmlContent +=					"<a href=\"http://localhost:8080/login/view?pro_no="+project.getPro_no()+"&apply="+member.getMem_id()+"&mem_nick="+member.getMem_nick()+"\""
                +" style=\"text-decoration:none;display:block;width:100%;margin:20px 0 0 0;padding:15px 0;font-weight:bold;font-size:20px;color:#fff;text-align:center;background-color:#2b3991;border-radius:10px;\">";
        htmlContent +=						"프로젝트 참여하기";
        htmlContent +=					"</a>";

        htmlContent += 				"</td>";
        htmlContent += 			"</tr>";
        htmlContent += 			"<tr>";
        htmlContent += 				"<td style=\"margin:0;padding:30px 0 0 0;\">";
        htmlContent +=					"<p style=\"margin:0;font-size:13px;color:#aaa;letter-spacing:1;\">";
        htmlContent +=						"Copyright &copy; 2022 #DEV. All rights reserved.<br>";
        htmlContent +=						"본 메일은 발신 전용입니다.";
        htmlContent +=					"</p>";
        htmlContent += 				"</td>";
        htmlContent += 			"<tr>";
        htmlContent += 			"</tr>";
        htmlContent += 		"</tbody>";
        htmlContent += "</table>";
        String html = htmlContent.toString();

        // 메일 옵션 설정
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        try {
            // 메일 서버  인증 계정 설정
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };

            // 메일 세션 생성
            Session session = Session.getInstance(props, auth);

            // 메일 송/수신 옵션 설정
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, fromUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject(subject);
            message.setSentDate(new Date());

            // 메일 콘텐츠 설정
            Multipart mParts = new MimeMultipart();
            MimeBodyPart mTextPart = new MimeBodyPart();
            MimeBodyPart mFilePart = null;

            // 메일 콘텐츠 - 내용
            mTextPart.setText(html, bodyEncoding, "html");
            mParts.addBodyPart(mTextPart);

            // 메일 콘텐츠 설정
            message.setContent(mParts);

            // MIME 타입 설정
            MailcapCommandMap MailcapCmdMap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            MailcapCmdMap.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            MailcapCmdMap.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            MailcapCmdMap.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            MailcapCmdMap.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            MailcapCmdMap.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(MailcapCmdMap);

            // 메일 발송
            Transport.send(message);

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
