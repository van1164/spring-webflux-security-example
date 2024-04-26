package com.van1164.webfluxsecurityexample.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.reactive.config.EnableWebFlux


@EnableWebFlux
@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class SecurityConfig(
    val oAuthSuccessHandler: ServerAuthenticationSuccessHandlerImpl,
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
    val clientRegistrationRepository: ReactiveClientRegistrationRepository
) {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.httpBasic {
            it.disable()
            }
            .csrf {
                it.disable()
            }
            .authorizeExchange{
                it.pathMatchers("/permit/url/**").permitAll()
                it.pathMatchers("/**").authenticated()
            }
            .formLogin {
                it.authenticationSuccessHandler(oAuthSuccessHandler)
            }
//            .exceptionHandling{
//                it.authenticationEntryPoint(RedirectServerAuthenticationEntryPoint("/login"))
//            }
            .oauth2Login {
                it.authenticationSuccessHandler(oAuthSuccessHandler)
                it.authorizationRequestResolver(authorizationRequestResolver())
            }
            .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }

    private fun authorizationRequestResolver(): ServerOAuth2AuthorizationRequestResolver {
        val authorizationRequestMatcher: ServerWebExchangeMatcher = PathPatternParserServerWebExchangeMatcher(
            "/oauth2/authorization/{registrationId}"
        )

        return DefaultServerOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, authorizationRequestMatcher
        )
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


}
