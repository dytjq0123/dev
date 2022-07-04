package kr.or.dev.service;

import kr.or.dev.entity.group.important.Important;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import kr.or.dev.repository.ImportantRepository;
import kr.or.dev.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImportantService {

    private final ImportantRepository importantRepository;

    private final MemberRepository memberRepository;

    public Important getImpProList(Long mem_no) {
        Important impProList = importantRepository.getImpProList(mem_no);

        return impProList;
    }

    public Important insertImp(Important important) {
        Important saveImp = importantRepository.save(important);

        Member member = memberRepository.findById(important.getMember().getMem_no()).get();

        member.setImportant(saveImp);

        return saveImp;
    }

    public void addImpProject(Long mem_no, Project project) {
        Important impProList = importantRepository.getImpProList(mem_no);
        impProList.addImpProject(project);
    }

    public void removeImpProject(Long mem_no, Project project) {
        Important impProList = importantRepository.getImpProList(mem_no);
        impProList.removeImpProject(project);
    }

}
