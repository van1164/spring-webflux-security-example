package com.van1164.webfluxsecurityexample.security;

import com.van1164.webfluxsecurityexample.user_info.PrincipalDetails;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    /*
    [ENGLISH]
    Security session => Authentication => UserDetails
    The value returned here enters the session with Authentication.
    At the end of the function, a @AuthenticationPrincipal annotation is created.
    */

    /*
    [KOREAN]
    시큐리티 session => Authentication => UserDetails
    여기서 리턴 된 값이 session 안에 Authentication 이 들어감.
    함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어짐.
     */
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        log.info("Enter UserDetailsService");

        // [ENGLISH] Requires the process of fetching user information and verifying it.
        // [KOREAN] 사용자 정보 불러와서 검증하는 과정 필요함.
        /*
            Example.
                val user = userRepository.findByUserName(username)
                val password = user.password
         */
        String password = passwordEncoder.encode("password" /*user.password*/);
        // [ENGLISH] Security automatically checks for the same password entered by the user.
        // [KOREAN] security에서 자동으로 사용자가 입력한 비밀번호와 같은지 체크함.
        return Mono.just(new PrincipalDetails(new User(username, password, Collections.emptyList())));
    }
}

