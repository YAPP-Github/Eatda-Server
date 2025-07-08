package eatda.repository;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

class DatabaseSchemaTest {

    @Nested
    @ActiveProfiles({"test", "flyway"})
    @SpringBootTest(webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = {
            "spring.datasource.url=jdbc:h2:mem:flyway:local;MODE=MySQL",
            "spring.flyway.locations=classpath:db/migration,classpath:db/seed/local"})
    class LocalDatabaseSchemaTest {

        @Autowired
        private Flyway flyway;

        @Test
        void 로컬_데이터베이스_스키마가_정상적으로_동작한다() {
            assertThatCode(() -> flyway.migrate()).doesNotThrowAnyException();
        }
    }

    @Nested
    @ActiveProfiles({"test", "flyway"})
    @SpringBootTest(webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = {
            "spring.datasource.url=jdbc:h2:mem:flyway:dev;MODE=MySQL",
            "spring.flyway.locations=classpath:db/migration,classpath:db/seed/dev"})
    class DevelopDatabaseSchemaTest {


        @Autowired
        private Flyway flyway;

        @Test
        void 개발_데이터베이스_스키마가_정상적으로_동작한다() {
            assertThatCode(() -> flyway.migrate()).doesNotThrowAnyException();
        }
    }

    @Nested
    @ActiveProfiles({"test", "flyway"})
    @SpringBootTest(webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = {
            "spring.datasource.url=jdbc:h2:mem:flyway:prod;MODE=MySQL",
            "spring.flyway.locations=classpath:db/migration,classpath:db/seed/prod"})
    class ProductionDatabaseSchemaTest {

        @Autowired
        private Flyway flyway;

        @Test
        void 운영_데이터베이스_스키마가_정상적으로_동작한다() {
            assertThatCode(() -> flyway.migrate()).doesNotThrowAnyException();
        }
    }
}
