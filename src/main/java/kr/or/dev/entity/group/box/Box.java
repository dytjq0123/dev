package kr.or.dev.entity.group.box;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.group.project.Project;
import kr.or.dev.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Box extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long box_no;		// 보관함번호
    private String box_name;	// 보관함명
//    private int box_index;		// 조회순번
    private String box_del_chk;	// 삭제여부

    @JoinColumn(name = "mem_no")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;		// 회원

    @Transient
    private int pro_box_chk;	// 해당 프로젝트의 보관함 유무(entity에서만 사용)

    @ManyToMany
    @JoinTable(name = "box_project",
            joinColumns = @JoinColumn(name = "box_no"),
            inverseJoinColumns = @JoinColumn(name = "project_no"))
    private List<Project> projectList = new ArrayList<>();

    @Builder
    public Box(String box_name, Member member) {
        this.box_name = box_name;
        this.box_del_chk = "n";
        this.member = member;
    }

    public void updateBox(String box_name) {
        this.box_name = box_name;
    }

    public void addBoxProject(Project project) {
        projectList.add(project);
    }

    public void remBoxProject(Project project) {
        projectList.remove(project);
    }

    public void setPro_box_chk() {
        this.pro_box_chk = 1;
    }

    public void deleteBox() {
        projectList.clear();
    }
}
