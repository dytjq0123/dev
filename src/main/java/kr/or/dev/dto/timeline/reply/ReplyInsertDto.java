package kr.or.dev.dto.timeline.reply;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyInsertDto {

    private Long pro_no;
    private String rep_cont;
    private String timeline_col;
    private Long timeline_no;
}
