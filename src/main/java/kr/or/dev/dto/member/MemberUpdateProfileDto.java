package kr.or.dev.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateProfileDto {

    private String mem_id;
    private String mem_name;
    private String mem_nick;
}
