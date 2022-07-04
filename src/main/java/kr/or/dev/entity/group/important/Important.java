package kr.or.dev.entity.group.important;

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
public class Important extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long important_no; // 중요 프로젝트 번호

    @ManyToMany
    @JoinTable(name = "imp_project",
            joinColumns = @JoinColumn(name = "important_no"),
            inverseJoinColumns = @JoinColumn(name = "pro_no"))
    private List<Project> projectList = new ArrayList<>();		// 프로젝트

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;	// 회원ID

    @Builder
    public Important(Member member) {
        this.member = member;
    }

    public void addImpProject(Project project) {
        projectList.add(project);
    }

    public void removeImpProject(Project project) {
        projectList.remove(project);
    }

}
