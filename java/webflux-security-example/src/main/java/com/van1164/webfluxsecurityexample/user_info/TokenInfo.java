package com.van1164.webfluxsecurityexample.user_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class TokenInfo {
    private final String grantType;
    private final String token;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenInfo tokenInfo = (TokenInfo) o;
        return Objects.equals(grantType, tokenInfo.grantType) && Objects.equals(token, tokenInfo.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grantType, token);
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "grantType='" + grantType + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}