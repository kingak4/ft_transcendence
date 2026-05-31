package code.shared;

import code.bootstrap.DotEnvInitializer;
import code.shared.config.RedisTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = DotEnvInitializer.class)
class RateLimitIntegrationTest extends RedisTestSupport {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser
  void shouldReturn429WhenRateLimitExceeded() throws Exception {

    for (int i = 0; i < 100; i++) {
      mockMvc.perform(get("/users/me"))
          .andExpect(status().isOk());
    }

    mockMvc.perform(get("/users/me"))
        .andExpect(status().isTooManyRequests());
  }
}