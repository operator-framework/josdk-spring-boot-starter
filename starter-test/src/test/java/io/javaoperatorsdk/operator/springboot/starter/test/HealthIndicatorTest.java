package io.javaoperatorsdk.operator.springboot.starter.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "management.endpoint.health.enabled=true", "management.endpoint.health.show-details=always"})
@EnableMockOperator(crdPaths = "classpath:crd.yml")
class HealthIndicatorTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testOperatorHealthIndicator() {
    ResponseEntity<String> entity =
        this.restTemplate.getForEntity("/actuator/health", String.class);
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(entity.getBody()).contains("\"status\":\"UP\"");
    assertThat(entity.getBody())
        .contains(
            "\"operator\":{\"status\":\"UP\",\"details\":{\"customservicereconciler\":\"OK\"}}");
  }

}
