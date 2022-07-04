package kr.or.dev.config.auth;

import kr.or.dev.entity.member.Member;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final String ROLE_PREFIX = "ROLE_";
    private Member member;

    public void updateMember(Member member) {
        this.member = member;
    }


    /**
     * 사용자에게 부여된 권한 반환
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 사용자를 인증하는데 사용된 암호를 반환
     * @return
     */
    @Override
    public String getPassword() {
        return member.getMem_pw();
    }

    /**
     * 사용자를 인증하는 사용된 사용자 이름을 반환
     * @return
     */
    @Override
    public String getUsername() {
        return member.getMem_id();
    }

    /**
     * 사용자의 계정이 만료되었는지 여부
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 사용자가 잠겨있는지 여부
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자의 자격 증명(암호)이 만료되었는지 여부
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자가 활성화되었는지 여부
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }


}
