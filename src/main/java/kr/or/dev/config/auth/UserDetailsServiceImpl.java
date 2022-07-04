package kr.or.dev.config.auth;

import kr.or.dev.entity.member.Member;
import kr.or.dev.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String mem_id) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemId(mem_id)
                .orElseThrow(() -> new IllegalStateException("등록되지 않은 사용자 입니다."));

        if(member.getMem_chk().equalsIgnoreCase("y")){
            return new UserDetailsImpl(member);
        }else {
            throw new IllegalStateException("비활성화된 사용자 입니다.");
        }
    }
}
