package com.van1164.webfluxsecurityexample.security

import com.van1164.webfluxsecurityexample.logger
import com.van1164.webfluxsecurityexample.user_info.JwtTokenProvider
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        logger.info {"Enter jwt filter"}
        val token = resolveToken(exchange.request)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
            logger.info("doFilterChain:$authentication")
        }
        return chain.filter(exchange)
    }

    private fun resolveToken(request : ServerHttpRequest) : String? {
        val bearerToken = request.headers["Authorization"]?.get(0)
        return if (StringUtils.hasText(bearerToken) && bearerToken!!.startsWith("Bearer")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

}