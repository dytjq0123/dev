package kr.or.dev.service;

import kr.or.dev.dto.box.BoxUpdateDto;
import kr.or.dev.entity.group.box.Box;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.repository.BoxRepository;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoxService {

    private final BoxRepository boxRepository;

    private final ProjectRepository projectRepository;

    private final MemberRepository memberRepository;

    public List<Box> getBoxMyList(Long mem_no) {
        List<Box> boxList = boxRepository.findByMemNo(mem_no);

        return boxList;
    }

    public long insertBox(Box box) {
        Box saveBox = boxRepository.save(box);

        return saveBox.getBox_no();
    }

    public void updateBox(BoxUpdateDto boxUpdateDto) {
        Box findBox = boxRepository.findById(boxUpdateDto.getBox_no()).get();
        findBox.updateBox(boxUpdateDto.getBox_name());

    }

    public void deleteBox(Long box_no) {
        Box box = boxRepository.findById(box_no).get();
        box.deleteBox();
        boxRepository.delete(box);
    }

    public Box getBoxDetail(Long box_no) {
        Box findBox = boxRepository.findById(box_no).get();

        return findBox;
    }

    public void addProject(Long box_no, Long pro_no) {
        Box box = boxRepository.findById(box_no).get();
        Project project = projectRepository.findById(pro_no).get();
        box.addBoxProject(project);
    }

    public void delProject(Long box_no, Long pro_no) {
        Box box = boxRepository.findById(box_no).get();
        Project project = projectRepository.findById(pro_no).get();
        box.remBoxProject(project);
    }

}
