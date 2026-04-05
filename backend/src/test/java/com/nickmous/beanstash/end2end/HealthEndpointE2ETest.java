package com.nickmous.beanstash.end2end;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.nickmous.beanstash.configuration.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
@ContextConfiguration(initializers = TestcontainersConfig.Initializer.class)
@ActiveProfiles("test")
class HealthEndpointE2ETest {

    @LocalServerPort
    private int port;

    @Test
    void healthEndpointReturnsUp() {
        given()
            .port(port)
        .when()
            .get("/actuator/health")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
}
