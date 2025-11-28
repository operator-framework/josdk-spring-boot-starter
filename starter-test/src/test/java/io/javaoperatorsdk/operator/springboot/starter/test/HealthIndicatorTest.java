package io.javaoperatorsdk.operator.springboot.starter.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "management.endpoint.health.enabled=true", "management.endpoint.health.show-details=always"})
@EnableMockOperator(crdPaths = "classpath:crd.yml")
@AutoConfigureMockMvc
class HealthIndicatorTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testOperatorHealthIndicator() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"))
        .andExpect(jsonPath("$.components.operator.status").value("UP"))
        .andExpect(jsonPath("$.components.operator.details.customservicereconciler").value("OK"));
  }

}
