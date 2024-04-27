# spring-webflux-security-example

### spring security (form + google OAuth2.0) example in spring 3.x webflux  

## Clone Project
```shell
$ git clone https://github.com/van1164/spring-webflux-security-example.git

# java user
$ cd java/webflux-security-example
# kotlin user
$ cd kotlin/webflux-security-example

$ ./gradlew build
```
> ### When the build is complete, go to localhost:8080
> ### you will see the screen below.

![image](https://github.com/van1164/spring-webflux-security-example/assets/52437971/fc15ee9f-e929-4030-84f8-3e44fad4f6f8)


# Configuration
## build.gradle (dependencies)
### [build.gradle.kts](https://github.com/van1164/spring-webflux-security-example/blob/main/kotlin/webflux-security-example/build.gradle.kts)
### [build.gradle](https://github.com/van1164/spring-webflux-security-example/blob/main/java/webflux-security-example/build.gradle)
---
## application.properties
### [your-client-id], [your-google-secret] is required.
```properties
spring.application.name=webflux-security-example
jwt.secret = abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZExampleSecret

spring.main.web-application-type=reactive

spring.security.oauth2.client.registration.google.client-id = [your-client-id]
spring.security.oauth2.client.registration.google.client-secret= [your-google-secret]
spring.security.oauth2.client.registration.google.scope = profile, email
```

---
# Security Configuration Code
## Java
```java
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
}
```

## Kotlin
```kotlin
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
```


![image](https://github.com/van1164/spring-webflux-security-example/assets/52437971/7b8e5cb8-9f50-4e6b-b7b6-91b292e61bc9)

# Full Code

### [Java](https://github.com/van1164/spring-webflux-security-example/tree/main/java/webflux-security-example/src/main/java/com/van1164/webfluxsecurityexample)
### [Kotlin](https://github.com/van1164/spring-webflux-security-example/tree/main/kotlin/webflux-security-example/src/main/kotlin/com/van1164/webfluxsecurityexample)


