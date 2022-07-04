package kr.or.dev.service;

import kr.or.dev.config.auth.UserDetailsImpl;
import kr.or.dev.dto.timeline.vote.VoteUpdateDto;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.vote.Vote;
import kr.or.dev.entity.timeline.vote_item.VoteItem;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import kr.or.dev.repository.VoteItemRepository;
import kr.or.dev.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    private final ProjectRepository projectRepository;

    private final MemberRepository memberRepository;

    private final VoteItemRepository voteItemRepository;

    public long insertVote(Long pro_no,
                          String vote_title,
                          List<String> vi_contList,
                          String vote_end_time,
                          UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();

        Project project = projectRepository.findById(pro_no).get();

        Vote saveVote = voteRepository.save(Vote.builder()
                .vote_title(vote_title)
                .member(member)
                .project(project)
                .vote_end_time(vote_end_time)
                .build());

        if(vi_contList != null){
            for(int i = 0; i < vi_contList.size(); i++){
                VoteItem saveVoteItem = voteItemRepository.save(VoteItem.builder()
                        .vi_cont(vi_contList.get(i))
                        .vote(saveVote)
                        .build());
                saveVote.addVoteItem(saveVoteItem);
            }
        }

        return saveVote.getVote_no();
    }

    public void updateVote(VoteUpdateDto voteUpdateDto) {
        Vote findVote = voteRepository.findById(voteUpdateDto.getVote_no()).get();

        for (VoteItem voteItem : findVote.getVoteItemList()) {
            findVote.remVoteItem(voteItem);
            voteItemRepository.delete(voteItem);
        }

        if(voteUpdateDto.getVi_contList().size() > 0){
            for(int i = 0; i < voteUpdateDto.getVi_contList().size(); i++){
                if(findVote.getVoteItemList().size() < 6){
                    VoteItem saveVoteItem = voteItemRepository.save(VoteItem.builder()
                            .vi_cont(voteUpdateDto.getVi_contList().get(i))
                            .vote(findVote)
                            .build());
                    if(findVote.getVoteItemList().size() < 6){
                        findVote.addVoteItem(saveVoteItem);
                    }
                }
            }
        }
    }

    public void deleteVote(Long vote_no) {
        Vote vote = voteRepository.findById(vote_no).get();
        vote.deleteVote();
    }

    public void fixChkVote(Long vote_no, String vote_fix_chk) {
        Vote vote = getVoteDetail(vote_no);
        vote.fixChkVote(vote_fix_chk);
    }

    public Vote getVoteDetail(Long vote_no) {
        Vote vote = voteRepository.findById(vote_no).get();

        return vote;
    }

    public List<TimeLine> getVoteProList(Long pro_no, Member member) {
        List<TimeLine> resultList = new ArrayList<>();

        Project project = projectRepository.findById(pro_no).get();

        project.getVoteList().forEach(vote -> {
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
                if(member.getCollection() != null){
                    if(member.getCollection().getVoteList().contains(vote)){
                        newTl.setColl_chk(1);
                    }
                }
                resultList.add(newTl);
            }
        });

        return resultList;
    }

//    public List<TimeLine> getVoteCollList(Long mem_no) {
//        Member member = memberRepository.findById(mem_no).get();
//
//        List<TimeLine> resultList = new ArrayList<>();
//
//        member.getCollectionList().forEach(collection -> {
//            TimeLine newTl = new TimeLine();
//            newTl.setVote(collection.getVote());
//            newTl.setMember(collection.getVote().getMember());
//            newTl.setFix_chk(collection.getVote().getVote_fix_chk());
//            newTl.setEmoticonList(collection.getVote().getEmoticonList());
//            newTl.setReplyList(collection.getVote().getReplyList());
//            resultList.add(newTl);
//        });
//
//        return resultList;
//
//    }

    public List<TimeLine> getMyVoteList(Long mem_no) {
        List<TimeLine> resultList = new ArrayList<>();

        Member member = memberRepository.findById(mem_no).get();

        member.getVoteList().forEach(vote -> {
            TimeLine newTl = new TimeLine();
            newTl.setVote(vote);
            newTl.setMember(vote.getMember());
            newTl.setFix_chk(vote.getVote_fix_chk());
            newTl.setEmoticonList(vote.getEmoticonList());
            newTl.setReplyList(vote.getReplyList());
            resultList.add(newTl);
        });

        return resultList;
    }

    public VoteItem getVoteItem(Long vi_no) {
        VoteItem voteItem = voteItemRepository.findById(vi_no).get();

        return voteItem;
    }

    public void voting(Long vote_no,
                       Long vi_no,
                       UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();

        Vote vote = voteRepository.findById(vote_no).get();
        vote.addVoteMember(member);

        VoteItem voteItem = voteItemRepository.findById(vi_no).get();
        voteItem.addVoteItemMember(member);

    }

    public long resetVote(Long vote_no, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Vote vote = voteRepository.findById(vote_no).get();

        vote.remVoteMember(member);

        vote.getVoteItemList().forEach(voteItem -> {
            if(voteItem.getMemberList().contains(member)){
                voteItem.getMemberList().remove(member);
            }
        });

        return vote.getVote_no();
    }



}
