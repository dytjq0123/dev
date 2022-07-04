package kr.or.dev.dto.timeline.todo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TodoUpdateDto {

    private Long pro_no;
    private Long todo_no;
    private String todo_title;
    private List<Long> tiNoDelList = new ArrayList<>();
    private List<String> ti_contList = new ArrayList<>();
    private List<String> ti_dateList = new ArrayList<>();
    private List<String> ti_mem_idList = new ArrayList<>();
}
