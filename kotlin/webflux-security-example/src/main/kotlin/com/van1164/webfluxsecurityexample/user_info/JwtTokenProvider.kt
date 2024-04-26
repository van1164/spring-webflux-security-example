package com.van1164.webfluxsecurityexample.user_info

import com.nimbusds.oauth2.sdk.Role
import com.van1164.webfluxsecurityexample.logger
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.LinkedHashMap

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private var secretKey: String,
) {

    val EXPIRATION_MILLISECONDS: Long = 10000 * 60 * 30
    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }


    fun createToken(email: String): TokenInfo {
        val claims = Jwts.claims().apply {
            subject = email
            put("roles", Role.entries)
        }
        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        val accessToken = Jwts
            .builder()
            .claim("auth", claims)
            .setIssuedAt(now)
            .setExpiration(accessExpiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return TokenInfo("Bearer", accessToken)
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val auth = claims["auth"] ?: throw RuntimeException("Invalid Token")
        val email = (auth as LinkedHashMap<*, *>)["sub"] as String
        val authorities: Collection<GrantedAuthority> = (auth.toString())
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = User(email, "", authorities)


        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return false
    }

    private fun getClaims(token: String): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}