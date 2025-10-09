package springboot.librarysystem.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonAuthHandlers {
    public static class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            springboot.librarysystem.dto.ApiResponseDto<Void> body =
                new springboot.librarysystem.dto.ApiResponseDto<>(401, "Unauthorized: Please login", null);
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        }
    }

    public static class JsonAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            springboot.librarysystem.dto.ApiResponseDto<Void> body =
                new springboot.librarysystem.dto.ApiResponseDto<>(403, "Forbidden: You do not have permission", null);
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        }
    }
}
