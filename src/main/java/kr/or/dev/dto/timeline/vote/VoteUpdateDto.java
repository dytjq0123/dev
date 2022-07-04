package kr.or.dev.dto.timeline.vote;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VoteUpdateDto {

    private Long pro_no;
    private Long vote_no;
    private String vote_title;
    private List<String> vi_contList;
    private String vote_end_time;
}
