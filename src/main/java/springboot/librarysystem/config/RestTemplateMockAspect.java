package springboot.librarysystem.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Aspect
@Configuration
public class RestTemplateMockAspect {
    @Around("execution(* org.springframework.web.client.RestTemplate.exchange(..))")
    public Object mockExchange(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        if (args.length >= 1 && "https://todo.com.tw".equals(args[0])) {
            // when String.class return mock
            if (args.length >= 4 && args[3] == String.class) {
                return new ResponseEntity<>("MOCK", HttpStatus.OK);
            }
        }
        return pjp.proceed();
    }
}
