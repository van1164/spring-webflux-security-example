package com.van1164.webfluxsecurityexample.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.reactive.config.EnableWebFlux;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFlux
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ServerAuthenticationSuccessHandlerImpl successHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange ->
                        exchange.pathMatchers("/permit/url/**").permitAll()
                                .pathMatchers("/**").authenticated()
                )
                .formLogin(formLogin ->
                        formLogin.authenticationSuccessHandler(successHandler)
                )
                .oauth2Login(oAuth ->
                        oAuth.authenticationSuccessHandler(successHandler)
                                .authorizationRequestResolver(authorizationRequestResolver())
                )
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        ServerWebExchangeMatcher authorizationRequestMatcher = new PathPatternParserServerWebExchangeMatcher("/oauth2/authorization/{registrationId}");
        return new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestMatcher);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}