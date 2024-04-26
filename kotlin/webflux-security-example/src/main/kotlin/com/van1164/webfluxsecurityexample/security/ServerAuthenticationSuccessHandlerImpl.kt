package com.van1164.webfluxsecurityexample.security

import com.van1164.webfluxsecurityexample.logger
import com.van1164.webfluxsecurityexample.user_info.JwtTokenProvider
import com.van1164.webfluxsecurityexample.user_info.PrincipalDetailsReactive
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono


@Component(value = "authenticationSuccessHandler")
class ServerAuthenticationSuccessHandlerImpl(
    val jwtTokenProvider: JwtTokenProvider,
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val principal = authentication.principal as PrincipalDetailsReactive
        val userName = principal.username
        val jwt = jwtTokenProvider.createToken(userName)
       return Mono.just(jwt)
           .doOnNext {
               logger.info{"Enter SuccessHandler"}
           }
            .flatMap {
                // Set the address to be redirected if the login is successful.
                //로그인 성공시 리다이렉트할 주소 설정.
                val uri = UriComponentsBuilder.newInstance().path("/").queryParam("token",jwt.token).build().toUri()
                val exchange = webFilterExchange.exchange
                DefaultServerRedirectStrategy().sendRedirect(exchange,uri)
            }
    }
}