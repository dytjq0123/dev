package kr.or.dev.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpDto {

    @Size(min = 1, max = 30, message = "이름은 1글자 이상, 30글자 이하로 작성해주세요.")
    @NotBlank(message = "이름을 입력해 주세요.")
    private String mem_name;

    @Size(min = 2, max = 100, message = "이메일은 2글자 이상, 100글자 이하로 작성해주세요.")
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String mem_id;

    @Size(min = 8, message = "비밀번호는 8자리 이상이어야 합니다.")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,}$",
            message = "비밀번호는 영문, 특수문자, 숫자가 적어도 1개 이상씩 포함된 8자 이상의 비밀번호여야 합니다.")
    private String mem_pw;

    @Size(min = 2, max = 10, message = "닉네임은 2글자 이상, 10글자 이하로 작성해 주세요.")
    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String mem_nick;

    @NotBlank(message = "전화번호를 입력해 주세요.")
    @Pattern(regexp = "^[0-9]+$", message = "전화번호는 숫자로만 입력해 주세요.")
    private String mem_phone;

    private String mem_howjoin;
}
