package kr.or.dev.dto.timeline.schedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleUpdateDto {

    private Long schd_no;
    private Long pro_no;
    private String schd_title;
    private String schd_memo;
    private String schd_loc;
    private String schd_lon;
    private String schd_lat;
}
