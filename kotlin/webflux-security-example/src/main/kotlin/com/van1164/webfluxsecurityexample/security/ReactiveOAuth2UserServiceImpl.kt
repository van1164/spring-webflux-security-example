package com.van1164.webfluxsecurityexample.security

import com.van1164.webfluxsecurityexample.logger
import com.van1164.webfluxsecurityexample.user_info.PrincipalDetailsReactive
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class ReactiveOAuth2UserServiceImpl : ReactiveOAuth2UserService<OAuth2UserRequest,OAuth2User> {
    //구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
        val delegate = DefaultReactiveOAuth2UserService()

        return delegate.loadUser(userRequest)
            .doOnNext {
                logger.info { "Enter OAuth2UserService" }
            }
            .flatMap { oAuth2User ->
                when (userRequest.clientRegistration.registrationId) {
                    "google" -> {
                        val attributes = oAuth2User.attributes
                        val email = attributes["email"].toString()
                        val user = User(
                            email,
                            "",
                            listOf()
                        )
                        Mono.just(PrincipalDetailsReactive(user,attributes))
                    }
                    else -> {
                        logger.info{"Unsupported OAuth"}
                        Mono.empty<PrincipalDetailsReactive>()
                    }
                }
            }

    }


}