package eatda.exception;


import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eatda.config.CorsConfig;
import eatda.config.CorsProperties;
import eatda.config.WebConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@WebMvcTest(
        controllers = GlobalExceptionHandlerTest.TestExceptionController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                WebConfig.class, CorsConfig.class
        })
)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("local")
class GlobalExceptionHandlerTest {

    @TestConfiguration
    static class TestCorsConfig {
        @Bean
        public CorsProperties corsProperties() {
            return new CorsProperties();
        }
    }

    @RestController
    @RequestMapping("/test")
    public static class TestExceptionController {

        @GetMapping("/business")
        public void throwBusinessException() {
            throw new BusinessException(BusinessErrorCode.STORY_NOT_FOUND);
        }

        @GetMapping("/mismatch")
        public void typeMismatch(@RequestParam Long id) {
        }

        @GetMapping("/header")
        public void missingHeader(@RequestHeader("X-Request-Id") String header) {
        }

        @PostMapping(value = "/mediatype", consumes = MediaType.APPLICATION_JSON_VALUE)
        public void mediaTypeTest() {
        }

        @GetMapping("/method")
        public void allowedMethod() {
        }

        @GetMapping("/unexpected")
        public void throwUnexpected() {
            throw new RuntimeException("예상치 못한 예외");
        }

        @GetMapping("/missing-param")
        public void missingParam(@RequestParam String value) {
        }

        @GetMapping("/missing-cookie")
        public void missingCookie(@CookieValue("userId") String cookie) {
        }

        @GetMapping("/binding-exception")
        public void throwBindingException(@Valid TestDto dto) {
        }

        @GetMapping("/no-resource")
        public void throwNoResourceFound() throws NoResourceFoundException {
            throw new NoResourceFoundException(HttpMethod.GET, "/test/no-resource");
        }

        @GetMapping("/constraint")
        public void throwConstraintViolation(@Validated @RequestParam @NotBlank String name) {
        }

        @GetMapping("/handler-validation")
        public void throwHandlerMethodValidation(@Valid TestDto dto) {
        }
    }

    public static class TestDto {
        @NotBlank
        private String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class handleExceptions {

        @Test
        void 비즈니스_예외는_정의된_코드로_응답된다() throws Exception {
            mockMvc.perform(get("/test/business"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode", equalTo(BusinessErrorCode.STORY_NOT_FOUND.getCode())));
        }

        @Test
        void 지원하지_않는_HTTP_METHOD는_405() throws Exception {
            mockMvc.perform(post("/test/method"))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.METHOD_NOT_SUPPORTED.getCode())));
        }

        @Test
        void 쿼리파라미터_형변환_실패는_400() throws Exception {
            mockMvc.perform(get("/test/mismatch?id=abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH.getCode())));
        }

        @Test
        void 누락된_헤더는_400() throws Exception {
            mockMvc.perform(get("/test/header"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.NO_HEADER_FOUND.getCode())));
        }

        @Test
        void 미지원_MediaType은_415() throws Exception {
            mockMvc.perform(post("/test/mediatype")
                            .contentType(MediaType.APPLICATION_XML))
                    .andExpect(status().isUnsupportedMediaType())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getCode())));
        }

        @Test
        void 처리되지_않은_예외는_500() throws Exception {
            mockMvc.perform(get("/test/unexpected"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.INTERNAL_SERVER_ERROR.getCode())));
        }

        @Test
        void 필수_파라미터_누락은_400() throws Exception {
            mockMvc.perform(get("/test/missing-param"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.NO_PARAMETER_FOUND.getCode())));
        }

        @Test
        void 필수_쿠키_누락은_400() throws Exception {
            mockMvc.perform(get("/test/missing-cookie"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.NO_COOKIE_FOUND.getCode())));
        }

        @Test
        void DTO_바인딩_에러는_400() throws Exception {
            mockMvc.perform(get("/test/binding-exception"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.CLIENT_REQUEST_ERROR.getCode())));
        }

        @Test
        void NoResourceFoundException_은_404() throws Exception {
            mockMvc.perform(get("/test/no-resource"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.NO_RESOURCE_FOUND.getCode())));
        }

        @Test
        void 제약조건_위반은_400() throws Exception {
            mockMvc.perform(get("/test/constraint?name="))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.VALIDATION_ERROR.getCode())));
        }

        @Test
        void HandlerMethodValidationException은_400() throws Exception {
            mockMvc.perform(get("/test/handler-validation"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode", equalTo(EtcErrorCode.CLIENT_REQUEST_ERROR.getCode())));
        }
    }
}
