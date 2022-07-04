package kr.or.dev.dto.timeline.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDto {

    private Long pro_no;
    private Long task_no;
    private String task_title;
    private String task_state;
    private String task_cont;
    private String task_start_date;
    private String task_end_date;
    private int task_rate;
    private String task_priority;

}
