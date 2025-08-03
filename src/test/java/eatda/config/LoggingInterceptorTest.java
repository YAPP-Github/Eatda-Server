package eatda.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class LoggingInterceptorTest {

    private final LoggingInterceptor interceptor = new LoggingInterceptor();

    @Nested
    class afterCompletion {

        @Test
        void preHandle_없이_afterCompletion만_호출되면_duration_unknown_로그가_남는다() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            interceptor.afterCompletion(request, response, new Object(), null);
        }
    }
}
