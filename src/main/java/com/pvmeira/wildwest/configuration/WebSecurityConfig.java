package com.pvmeira.wildwest.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/h2-console/**")


                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(URI.LOGIN)
                .defaultSuccessUrl(URI.INDEX)
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl(URI.LOGIN)
                .permitAll();

        http.csrf()
                .ignoringAntMatchers("/h2-console/**");
        http.headers()
                .frameOptions()
                .sameOrigin();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                DataSource dataSource,
                                PasswordEncoder passwordEncoder) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource);
    }


}