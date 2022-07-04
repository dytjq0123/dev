package kr.or.dev.dto.timeline.todo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoItemUpdateDto {

    private Long ti_no;
    private String ti_chk;
    private String ti_cont;
    private Long todo_no;
    private int todo_rate;
}
