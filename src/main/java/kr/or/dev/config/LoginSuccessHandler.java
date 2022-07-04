package kr.or.dev.config;

import kr.or.dev.config.auth.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String url = "";
        switch (((UserDetailsImpl)authentication.getPrincipal()).getMember().getRole().toString()) {
            case "ADMIN" :
                url = "/adm/memList";
                break;
            case "USER" :
                url = "/pro/main";
        }

        response.sendRedirect(url);
    }
}
