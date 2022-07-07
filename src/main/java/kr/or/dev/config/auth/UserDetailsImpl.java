package kr.or.dev.config.auth;

import kr.or.dev.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails, OAuth2User {
    private static final String ROLE_PREFIX = "ROLE_";
    private Member member;
    private Map<String, Object> attributes = new HashMap<>();

    public void updateMember(Member member) {
        this.member = member;
    }

    public UserDetailsImpl(Member member) {
        this.member = member;
    }

    public UserDetailsImpl(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 사용자에게 부여된 권한 반환
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return ROLE_PREFIX + member.getRole().toString();
            }
        });

        return collect;
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


    @Override
    public String getName() {
        String sub = attributes.get("sub").toString();
        return sub;
    }


}
