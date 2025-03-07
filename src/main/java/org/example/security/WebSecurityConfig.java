package org.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
  @Autowired
  private JwtTokenFilter jwtTokenFilter;

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests((requests) -> requests
            .anyRequest().permitAll()
        )
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }
}
