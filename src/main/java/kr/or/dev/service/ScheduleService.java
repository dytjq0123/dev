package kr.or.dev.service;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.mapper.ScheduleMapperDto;
import kr.or.dev.dto.timeline.schedule.ScheduleInsertDto;
import kr.or.dev.dto.timeline.schedule.ScheduleUpdateDto;
import kr.or.dev.entity.Format;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.schedule.Schedule;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import kr.or.dev.repository.ScheduleRepository;
import kr.or.dev.repository.mapper.ScheduleMapperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapperRepository scheduleMapperRepository;

    private final MemberRepository memberRepository;

    private final ProjectRepository projectRepository;

    public long insertSchd(ScheduleInsertDto scheduleInsertDto,
                          String alert_time,
                          String datetime,
                          UserDetailsImpl userDetails) throws ParseException {

        Member member = userDetails.getMember();

        Project project = projectRepository.findById(scheduleInsertDto.getPro_no()).get();

        // 날짜 datetime
        String[] time = datetime.split("   -   ");

        // 알람 울릴시간(Format class의 메서드를 이용, 시작날짜로부터 계산해서 알림날짜를 추출)
        String notificationTimer = "";
        if(!alert_time.equals("0")){
            notificationTimer = Format.notificationFormatter(time[0],-Integer.parseInt(alert_time));
        } else if(alert_time.equals("0")){
            notificationTimer = time[0];
        }

        Schedule saveSchd = scheduleRepository.save(Schedule.builder()
                .schd_start_time(time[0])
                .schd_end_time(time[1])
                .schd_alarm(notificationTimer)
                .schd_lat(scheduleInsertDto.getSchd_lat())
                .schd_lon(scheduleInsertDto.getSchd_lon())
                .schd_loc(scheduleInsertDto.getSchd_loc())
                .schd_memo(scheduleInsertDto.getSchd_memo())
                .schd_title(scheduleInsertDto.getSchd_title())
                .project(project)
                .member(member)
                .build());

        return saveSchd.getSchd_no();
    }

    public void updateSchd(ScheduleUpdateDto scheduleUpdateDto,
                           String alert_time,
                           String datetime,
                           String defaultDate,
                           UserDetailsImpl userDetails) throws ParseException {

        if(datetime == null || datetime.equals("")){
            datetime = defaultDate;
        }
        // 날짜 datetime
        String[] time = datetime.split("   -   ");  //2018-01-22 10:22   - 2018-01-22 12:22
        // 알람 울릴시간(Format class의 메서드를 이용, 시작날짜로부터 계산해서 알림날짜를 추출)
        String notificationTimer = Format.notificationFormatter(time[0],-Integer.parseInt(alert_time));

        Schedule findSchedule = scheduleRepository.findById(scheduleUpdateDto.getSchd_no()).get();

        findSchedule.updateSchedule(scheduleUpdateDto.getSchd_title()
                ,scheduleUpdateDto.getSchd_loc()
                ,scheduleUpdateDto.getSchd_memo()
                ,notificationTimer
                ,time[0]
                ,time[1]
                ,scheduleUpdateDto.getSchd_lat()
                ,scheduleUpdateDto.getSchd_lon());

    }

    public void deleteSchd(Long schd_no) {
        Schedule findSchedule = getSchdDetail(schd_no);

        findSchedule.deleteSchedule();
    }

    public Schedule getSchdDetail(Long schd_no) {
        Schedule findSchedule = scheduleRepository.findById(schd_no).get();

        return findSchedule;
    }

    public void fixChkSchd(Long schd_no, String schd_fix_chk) {
        Schedule findSchedule = scheduleRepository.findById(schd_no).get();
        findSchedule.fixChkSchd(schd_fix_chk);
    }

    public List<ScheduleMapperDto> getSchdSearchList(Map paramMap) {
        List<ScheduleMapperDto> schdSearchList = scheduleMapperRepository.getSchdSearchList(paramMap);

        return schdSearchList;
    }

    public List<Schedule> getMySchdList(Long mem_no) {
        Member member = memberRepository.findById(mem_no).get();
        return member.getScheduleList();
    }

    public List<Schedule> getAlarmList(Map paramMap) {
        List<Schedule> scheduleList = scheduleRepository.findByCurrentTime((Long) paramMap.get("mem_no"), (String) paramMap.get("schd_alarm"));

        return scheduleList;
    }

//    public List<Schedule> getInvited_Schd() {}

    public List<Schedule> getMyProSchdList(Map paramMap) {
        List<Schedule> scheduleList = scheduleRepository.findByMemNoAndProNo((Long) paramMap.get("mem_no"), (Long) paramMap.get("pro_no"));

        return scheduleList;
    }

//    public List<TimeLine> getSchdCollList(Long mem_no) {
//        Member member = memberRepository.findById(mem_no).get();
//
//        List<TimeLine> resultList = new ArrayList<>();
//
//        member.getCollectionList().forEach(collection -> {
//            TimeLine newTl = new TimeLine();
//            newTl.setSchedule(collection.getSchedule());
//            newTl.setMember(collection.getSchedule().getMember());
//            newTl.setFix_chk(collection.getSchedule().getSchd_fix_chk());
//            newTl.setEmoticonList(collection.getSchedule().getEmoticonList());
//            newTl.setReplyList(collection.getSchedule().getReplyList());
//            resultList.add(newTl);
//        });
//
//        return resultList;
//
//    }

    public List<TimeLine> getSchdProList(Long pro_no, Member member) {
        List<TimeLine> resultList = new ArrayList<>();

        Project project = projectRepository.findById(pro_no).get();

        project.getScheduleList().forEach(schedule -> {
            if(schedule.getSchd_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setSchedule(schedule);
                newTl.setMember(schedule.getMember());
                newTl.setFix_chk(schedule.getSchd_fix_chk());
                String minute = "";
                try {
                    minute = Format.dateCal(schedule.getSchd_start_time(), schedule.getSchd_alarm());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                schedule.setMinute(minute);
                schedule.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(schedule.getEmoticonList());
                schedule.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(schedule.getCreateDate());
                if(member.getCollection() != null){
                    if(member.getCollection().getScheduleList().contains(schedule)){
                        newTl.setColl_chk(1);
                    }
                }
                resultList.add(newTl);
            }
        });

        return resultList;
    }






}
