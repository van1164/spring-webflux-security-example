package com.van1164.webfluxsecurityexample.security;

import com.van1164.webfluxsecurityexample.user_info.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReactiveOAuth2UserServiceImpl implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultReactiveOAuth2UserService delegate = new DefaultReactiveOAuth2UserService();

        return delegate.loadUser(userRequest)
                .doOnNext(oAuth2User -> {
                    log.info("Enter OAuth2UserService");
                })
                .flatMap(oAuth2User -> {
                    if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
                        String email = oAuth2User.getAttributes().get("email").toString();
                        User user = new User(email, "", java.util.Collections.emptyList());
                        return Mono.just(new PrincipalDetails(user, oAuth2User.getAttributes()));
                    } else {
                        log.info("Unsupported OAuth");
                        return Mono.empty();
                    }
                });
    }
}

