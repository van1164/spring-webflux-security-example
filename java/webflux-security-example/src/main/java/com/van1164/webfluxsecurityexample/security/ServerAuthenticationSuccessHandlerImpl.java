package com.van1164.webfluxsecurityexample.security;

import com.van1164.webfluxsecurityexample.user_info.JwtTokenProvider;
import com.van1164.webfluxsecurityexample.user_info.PrincipalDetails;
import com.van1164.webfluxsecurityexample.user_info.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component(value = "authenticationSuccessHandler")
@Slf4j
@AllArgsConstructor
public class ServerAuthenticationSuccessHandlerImpl implements ServerAuthenticationSuccessHandler {
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String userName = principal.getUsername();
        TokenInfo jwt = jwtTokenProvider.createToken(userName);
        return Mono.just(jwt)
                .doOnNext(token -> log.info("Enter SuccessHandler"))
                .flatMap(token -> {
                    // Set the address to be redirected if the login is successful.
                    //로그인 성공시 리다이렉트할 주소 설정.
                    URI uri = UriComponentsBuilder.newInstance().path("/").queryParam("token", token.getToken()).build().toUri();
                    ServerWebExchange exchange = webFilterExchange.getExchange();
                    return new DefaultServerRedirectStrategy().sendRedirect(exchange, uri);
                });
    }

}