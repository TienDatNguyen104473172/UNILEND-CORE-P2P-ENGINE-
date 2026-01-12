package com.unilend.common.config;

import com.unilend.security.CustomUserDetailsService;
import com.unilend.security.JwtAuthenticationEntryPoint;
import com.unilend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    // Inject the necessary beans
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Use Constructor Injection instead of @Autowired (a better standard).
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    // 1. Configure Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt One-Way Encryption (Very Secure)
    }
    // 2. Configure AuthenticationManager (Login Manager)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    // 3. Provider Configuration (Assigning users to the Manager)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);// User data source (DB)
        authProvider.setPasswordEncoder(passwordEncoder()); // Password comparison tool
        return authProvider;
    }
    // 4. Configure the Security Filter Chain (MOST IMPORTANT)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disabled CSRF because using a Token (Stateless).
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Xử lý lỗi 401
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng Session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Open access to login/registration APIs.
                        .requestMatchers("/api/test/**").permitAll() // Allow API testing (if necessary).
                        .anyRequest().authenticated() // All other APIs REQUIRE login.
                );
        // Add Provider
        http.authenticationProvider(authenticationProvider());
        // Add your filter BEFORE Spring's default filter.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
