package kr.or.dev.service;

import kr.or.dev.entity.Format;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.collection.Collection;
import kr.or.dev.repository.CollectionRepository;
import kr.or.dev.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;

    private final MemberRepository memberRepository;

    public long insertColl(Member member, TimeLine timeLine) {

        Collection collection = collectionRepository.findById(member.getCollection().getColl_no()).get();
        if(collection == null){
            Collection saveColl = collectionRepository.save(Collection.builder()
                    .member(member)
                    .build());

            saveColl.addTimeLine(timeLine);

            return saveColl.getColl_no();

        }else {
            collection.addTimeLine(timeLine);

            return collection.getColl_no();
        }
    }

    public void removeColl(Member member, TimeLine timeLine) {
        Collection collection = collectionRepository.findById(member.getCollection().getColl_no()).get();
        collection.removeTimeLine(timeLine);
    }

    // 사용자가 담아둔 글 리스트
    public List<Collection> getMyProCollList(Long mem_no, Long pro_no) {
        List<Collection> collectionList = collectionRepository.findByProMemNo(mem_no, pro_no);

        return collectionList;
    }

    public List<TimeLine> getMyCollList(Long mem_no) {
        List<TimeLine> resultList = new ArrayList<>();

        Member member = memberRepository.findById(mem_no).get();

        member.getCollection().getBasicList().forEach(basic -> {
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
                newTl.setProject(basic.getProject());
                newTl.setColl_chk(1);
                resultList.add(newTl);
            }
        });

        member.getCollection().getTaskList().forEach(task -> {
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
                newTl.setProject(task.getProject());
                newTl.setColl_chk(1);
                resultList.add(newTl);
            }
        });

        member.getCollection().getScheduleList().forEach(schedule -> {
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
                newTl.setProject(schedule.getProject());
                newTl.setColl_chk(1);
                resultList.add(newTl);
            }
        });

        member.getCollection().getTodoList().forEach(todo -> {
            if(todo.getTodo_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setTodo(todo);
                newTl.setMember(todo.getMember());
                newTl.setFix_chk(todo.getTodo_fix_chk());
                todo.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(todo.getEmoticonList());
                todo.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(todo.getCreateDate());
                newTl.setProject(todo.getProject());
                newTl.setColl_chk(1);
                resultList.add(newTl);
            }
        });

        member.getCollection().getVoteList().forEach(vote -> {
            if(vote.getVote_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setVote(vote);
                newTl.setMember(vote.getMember());
                newTl.setFix_chk(vote.getVote_fix_chk());
                vote.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(vote.getEmoticonList());
                vote.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(vote.getCreateDate());
                newTl.setProject(vote.getProject());
                newTl.setColl_chk(1);
                resultList.add(newTl);
            }
        });

        Collections.sort(resultList);

        return resultList;
    }

    public List<TimeLine> getMyTimeLineList(Long mem_no) {
        List<TimeLine> resultList = new ArrayList<>();

        Member member = memberRepository.findById(mem_no).get();

        member.getBasicList().forEach(basic -> {
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
                newTl.setProject(basic.getProject());
                if(member.getCollection().getBasicList().contains(basic)){
                    newTl.setColl_chk(1);
                }
                resultList.add(newTl);
            }
        });

        member.getTasks().forEach(task -> {
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
                newTl.setProject(task.getProject());
                if(member.getCollection().getTaskList().contains(task)){
                    newTl.setColl_chk(1);
                }
                resultList.add(newTl);
            }
        });

        member.getScheduleList().forEach(schedule -> {
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
                newTl.setProject(schedule.getProject());
                if(member.getCollection().getScheduleList().contains(schedule)){
                    newTl.setColl_chk(1);
                }
                resultList.add(newTl);
            }
        });

        member.getTodoList().forEach(todo -> {
            if(todo.getTodo_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setTodo(todo);
                newTl.setMember(todo.getMember());
                newTl.setFix_chk(todo.getTodo_fix_chk());
                todo.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(todo.getEmoticonList());
                todo.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(todo.getCreateDate());
                newTl.setProject(todo.getProject());
                if(member.getCollection().getTodoList().contains(todo)){
                    newTl.setColl_chk(1);
                }
                resultList.add(newTl);
            }
        });

        member.getVoteList().forEach(vote -> {
            if(vote.getVote_del_chk().equals("n")){
                TimeLine newTl = new TimeLine();
                newTl.setVote(vote);
                newTl.setMember(vote.getMember());
                newTl.setFix_chk(vote.getVote_fix_chk());
                vote.getEmoticonList().forEach(emoticon -> {
                    if(emoticon.getMemberList().contains(member)){
                        newTl.setEmo_user_chk(true);
                        newTl.setEmo_no(emoticon.getEmo_no());
                        newTl.setEmo_name(emoticon.getEmo_name());
                    }
                });
                newTl.setEmoticonList(vote.getEmoticonList());
                vote.getReplyList().forEach(reply -> {
                    if(reply.getRep_del_chk().equals("n")){
                        newTl.addReplyList(reply);
                    }
                });
                newTl.setTime(vote.getCreateDate());
                newTl.setProject(vote.getProject());
                if(member.getCollection().getVoteList().contains(vote)){
                    newTl.setColl_chk(1);
                }
                resultList.add(newTl);
            }
        });

        Collections.sort(resultList);

        return resultList;
    }
}
