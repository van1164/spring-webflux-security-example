package com.van1164.webfluxsecurityexample.user_info

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User


class PrincipalDetailsReactive : UserDetails, OAuth2User{
    private var user  : User
    private lateinit var attributes :MutableMap<String,Any>
    public constructor(user: User) {
        this.user = user
    }

    constructor(user: User, attributes: Map<String, Any>){
        this.user = user
        this.attributes = attributes as MutableMap<String, Any>
    }

    override fun getName(): String {
        return user.username
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collection: ArrayList<GrantedAuthority> = ArrayList()
        return collection
    }
    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}