package com.example.housingsociety.config;

import com.example.housingsociety.security.JwtAuthenticationFilter;
import com.example.housingsociety.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public (auth + H2)
                        .requestMatchers("/auth/**", "/api/auth/**", "/h2-console/**").permitAll()

                        // ==== Visitors ====
                        // My visitors (derived from JWT)
                        .requestMatchers(HttpMethod.GET, "/api/visitors/my")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN","RESIDENT","ROLE_RESIDENT")
                        // Visitors by user (admin support tool)
                        .requestMatchers(HttpMethod.GET, "/api/visitors/user/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        // Visitors by flat (admin/security/resident)
                        .requestMatchers(HttpMethod.GET, "/api/visitors/flat/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN","SECURITY","ROLE_SECURITY","RESIDENT","ROLE_RESIDENT")
                        // Check-in/out (security/admin)
                        .requestMatchers(HttpMethod.POST, "/api/visitors/checkin", "/api/visitors/checkout/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN","SECURITY","ROLE_SECURITY")
                        // List all visitors (admin)
                        .requestMatchers(HttpMethod.GET, "/api/visitors")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN","SECURITY","ROLE_SECURITY")

                        // ==== Documents ====
                        // Upload after login (resident/admin). If you only upload during register-with-doc, you can tighten/remove.
                        .requestMatchers(HttpMethod.POST, "/api/documents")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN","RESIDENT","ROLE_RESIDENT")
                        // View file (admin)
                        .requestMatchers(HttpMethod.GET, "/api/documents/*/file")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        // Verify/unverify (admin)
                        .requestMatchers(HttpMethod.PATCH, "/api/documents/*/verify")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        // List documents (admin)
                        .requestMatchers(HttpMethod.GET, "/api/documents/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")

                        // ==== Admin area ====
                        .requestMatchers("/api/admin/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        .requestMatchers("/api/users/security/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")

                        // ==== Maintenance ====
                        .requestMatchers("/api/maintenance/generate", "/api/maintenance/send-emails")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        .requestMatchers("/api/maintenance/due")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        .requestMatchers("/api/maintenance")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        .requestMatchers("/api/maintenance/flat/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN","RESIDENT","ROLE_RESIDENT")

                        // ==== Flats & residents ====
                        .requestMatchers("/api/flats/**")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/flat-users")
                        .hasAnyAuthority("ADMIN","ROLE_ADMIN")

                        // Everything else requires auth
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
