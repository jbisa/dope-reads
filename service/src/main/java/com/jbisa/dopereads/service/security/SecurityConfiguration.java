package com.jbisa.dopereads.service.security;

import com.jbisa.dopereads.service.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("jbisa")
                .password("foo")
                .roles(Role.USER.toString())
                .and()
                .withUser("nbisa")
                .password("bar")
                .roles(Role.ADMIN.toString());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/admin/*")
                .hasRole(Role.ADMIN.toString())
                .antMatchers("/api/users")
                .hasAnyRole(Role.ADMIN.toString(), Role.USER.toString())
                .antMatchers("/")
                .permitAll()
                .and()
                .formLogin();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
