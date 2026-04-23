package code.users.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtTokenService jwtTokenService;

  @Mock private UserDetailsService userDetailsService;

  @InjectMocks private JwtAuthenticationFilter filter;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doesNotAuthenticateIfNoAuthorizationHeader() throws ServletException, IOException {
    // given
    when(request.getHeader("Authorization")).thenReturn(null);

    // when
    filter.doFilterInternal(request, response, filterChain);

    // then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doesNotAuthenticateIfHeaderDoesNotStartWithBearer() throws ServletException, IOException {
    // given
    when(request.getHeader("Authorization")).thenReturn("Basic token");

    // when
    filter.doFilterInternal(request, response, filterChain);

    // then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void authenticatesIfTokenIsValid() throws ServletException, IOException {
    // given
    String token = "valid-token";
    String username = "user@example.com";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtTokenService.extractUsername(token)).thenReturn(username);
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtTokenService.isTokenValid(token, userDetails)).thenReturn(true);

    // when
    filter.doFilterInternal(request, response, filterChain);

    // then
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doesNotAuthenticateIfTokenIsInvalid() throws ServletException, IOException {
    // given
    String token = "invalid-token";
    String username = "user@example.com";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtTokenService.extractUsername(token)).thenReturn(username);
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    when(jwtTokenService.isTokenValid(token, userDetails)).thenReturn(false);

    // when
    filter.doFilterInternal(request, response, filterChain);

    // then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
