package code.bootstrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityEnabled(
      HttpSecurity http,
      DaoAuthenticationProvider authProvider
  ) {
    try {
      return http
          .csrf(csrf -> csrf.disable())
          .authenticationProvider(authProvider)
          .authorizeHttpRequests(authorize -> authorize
              .requestMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
              .anyRequest().authenticated()
          )
          .httpBasic(httpBasic -> {
          })
          .build();
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to build security filter chain", exception);
    }
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authProvider(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
  ) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder);
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }
}