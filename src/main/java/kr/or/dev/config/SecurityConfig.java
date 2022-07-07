package kr.or.dev.config;

import kr.or.dev.config.auth.UserDetailsServiceImpl;
import kr.or.dev.config.oauth.Oauth2DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    private final Oauth2DetailsService oauth2DetailsService;

    /**
     * 회원 가입시 비밀번호 암호화에 사용할 Encoder 빈 등록
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**",
                        "/image/**",
                        "/js/**",
                        "/SE2/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // URL별 권한 관리 설정 시작점(authorizeRequests 가 선언되어야 antMatchers 옵션 사용 가능)
                .authorizeRequests()
                // login으로 시작되는 url 접근 허용
                .antMatchers("/login/**", "/chat/**").permitAll()
                // adm으로 시작되는 url은 ADMIN 권한이 있는 사용자만 접근 허용
                .antMatchers("/adm/**").hasRole("ADMIN")
                // 그외 모든 요청 과정은 인증과정 필요
                .anyRequest().authenticated()
                .and()
                .formLogin()
                // 로그인 페이지로 사용할 페이지 지정
                .loginPage("/login/view")
                // 로그인을 요청할 url 지정 (로그인 form의 action과 이름이 같아야 함)
                .loginProcessingUrl("/pro/main")
                // 로그인 성공 후 반환할 페이지 지정
//                .defaultSuccessUrl("/pro/main")
                .successHandler(new LoginSuccessHandler())
                .and()
                .logout()
                .logoutSuccessUrl("/login/view")
                // 세션 전체 삭제
                .invalidateHttpSession(true)
                .and()
                .oauth2Login()
                .loginPage("/login/view")
                .defaultSuccessUrl("/pro/main")
                .userInfoEndpoint()
                .userService(oauth2DetailsService);

        http.sessionManagement() // 중복 로그인
                .maximumSessions(1) //세션 최대 허용 수
                .maxSessionsPreventsLogin(false); // false인 경우 중복 로그인 시 이전 로그인 풀림

    }
}
