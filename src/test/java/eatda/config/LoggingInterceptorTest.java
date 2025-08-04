package eatda.config;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class LoggingInterceptorTest {

    private final LoggingInterceptor interceptor = new LoggingInterceptor();

    @Nested
    class afterCompletion {

        @Test
        void preHandle_없이_afterCompletion만_호출되면_duration_unknown_로그가_남는다() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            Logger logger = (Logger) LoggerFactory.getLogger(LoggingInterceptor.class);
            ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
            listAppender.start();
            logger.addAppender(listAppender);

            interceptor.afterCompletion(request, response, new Object(), null);

            assertThat(listAppender.list).anySatisfy(event -> {
                assertThat(event.getLevel()).isEqualTo(Level.WARN);
                assertThat(event.getFormattedMessage()).contains("duration unknown - preHandle not called");
            });

            listAppender.stop();
            logger.detachAppender(listAppender);
        }
    }
}
