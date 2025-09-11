package springboot.librarysystem.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateMockAspectTest {
    @Test
    void testMockExchange_returnsMockResponse() throws Throwable {
        RestTemplateMockAspect aspect = new RestTemplateMockAspect();
        Object[] args = new Object[] {
            "https://todo.com.tw",
            HttpMethod.GET,
            new HttpEntity<>(""),
            String.class
        };
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(pjp.getArgs()).thenReturn(args);

        Object result = aspect.mockExchange(pjp);
        assertTrue(result instanceof ResponseEntity);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("MOCK", response.getBody());
    }

    @Test
    void testMockExchange_proceedCalled() throws Throwable {
        RestTemplateMockAspect aspect = new RestTemplateMockAspect();
        Object[] args = new Object[] {
            "https://other.com.tw",
            HttpMethod.GET,
            new HttpEntity<>(""),
            String.class
        };
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(pjp.getArgs()).thenReturn(args);
        Mockito.when(pjp.proceed()).thenReturn("PROCEED_RESULT");

        Object result = aspect.mockExchange(pjp);
        assertEquals("PROCEED_RESULT", result);
    }
}
