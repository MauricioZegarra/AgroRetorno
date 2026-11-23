package pe.edu.agroretorno.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad de AgroRetorno (RNF02, control de riesgo 1: autorización por roles).
 * <ul>
 *   <li>API /api/** y métricas /actuator/**: HTTP Basic sin estado (solo laboratorio local y JMeter).</li>
 *   <li>Web: inicio de sesión con formulario, sesión HTTP y protección CSRF.</li>
 * </ul>
 * Las restricciones se aplican por URL y además con @PreAuthorize en los controladores.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http, RespuestaJsonSeguridad respuestaJson) throws Exception {
        http.securityMatcher("/api/**", "/actuator/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ENCARGADO")
                        .requestMatchers("/api/productor/**").hasRole("PRODUCTOR")
                        .requestMatchers("/api/encargado/**").hasRole("ENCARGADO")
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(respuestaJson))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // API sin cookies de sesión: cada petición envía sus credenciales, por lo que no aplica CSRF.
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(respuestaJson).accessDeniedHandler(respuestaJson));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/favicon.ico").permitAll()
                        .requestMatchers("/productor/**").hasRole("PRODUCTOR")
                        .requestMatchers("/encargado/**").hasRole("ENCARGADO")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/panel", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement(s -> s.sessionFixation(f -> f.migrateSession()));
        // CSRF permanece habilitado (valor por defecto) para todos los formularios web.
        return http.build();
    }
}
