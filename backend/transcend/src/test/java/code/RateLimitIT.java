package code;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import code.bootstrap.DotEnvInitializer;
import code.bootstrap.ValidProfileInitializer;
import code.shared.config.WebSocketTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"bucket4j.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = DotEnvInitializer.class)
@Import(ValidProfileInitializer.class)
class RateLimitIT extends WebSocketTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockUser
  void shouldReturn429WhenRateLimitExceeded() throws Exception {
    String randomEndpoint = "/users/me";
    boolean rateLimitReached = false;

    for (int i = 0; i < 1000; i++) {
      int status = mockMvc.perform(get(randomEndpoint)).andReturn().getResponse().getStatus();

      if (status == 429) {
        rateLimitReached = true;
        break;
      }
    }
    if (!rateLimitReached) {
      fail("Rate limit was not reached after 1000 requests.");
    }
  }
}
