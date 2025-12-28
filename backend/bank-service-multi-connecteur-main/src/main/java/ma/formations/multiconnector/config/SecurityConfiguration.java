package ma.formations.multiconnector.config;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.jwt.AuthEntryPointJwt;
import ma.formations.multiconnector.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private AuthTokenFilter authTokenFilter;
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Requestor-Type", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> {
            // Routes publiques (SEULEMENT signin et signup)
            auth.requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/auth/signin"),
                    new AntPathRequestMatcher("/auth/signup"),
                    new AntPathRequestMatcher("/h2/**"),
                    new AntPathRequestMatcher("/h2/login.do**")
            ).permitAll();

            // Change password nécessite authentification
            auth.requestMatchers(new AntPathRequestMatcher("/auth/change-password"))
                    .authenticated();

            // 🆕 UC-4 : Dashboard accessible uniquement aux CLIENTS
            auth.requestMatchers(new AntPathRequestMatcher("/api/rest/dashboard/**"))
                    .hasRole("CLIENT");

            auth.anyRequest().authenticated();
        });
        http.authenticationProvider(authenticationProvider());
        http.exceptionHandling((exception) -> exception.authenticationEntryPoint(unauthorizedHandler).
                accessDeniedPage("/access-denied"));
        http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                new AntPathRequestMatcher("/h2/**"),
                new AntPathRequestMatcher("/static/**"),
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/js/**",
                        "/images/**"));
    }
}