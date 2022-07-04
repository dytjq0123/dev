package kr.or.dev.dto.project;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectUpdateDto {

    private Long pro_no;
    private String pro_name;
    private String pro_cont;
    private Long kind_no;
}
