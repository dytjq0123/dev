package kr.or.dev.entity.group.kind;

import kr.or.dev.entity.BaseTimeEntity;
import kr.or.dev.entity.group.project.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Kind extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kind_no;			// 분류코드
    private String kind_name;		// 분류명
    private String kind_del_chk;	// 삭제여부

    @OneToMany(mappedBy = "kind")
    private List<Project> projectList = new ArrayList<>();

    @Builder
    public Kind(String kind_name) {
        this.kind_name = kind_name;
        this.kind_del_chk = "n";
    }

    public void updateKind(String kind_name) {
        this.kind_name = kind_name;
    }

    public void deleteKind() {
        this.kind_del_chk = "y";
    }
}
