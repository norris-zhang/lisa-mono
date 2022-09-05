package com.guoba.lisa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           @Autowired AuthenticationProvider authProvider,
                                           @Autowired UserDetailsService userDetailsService) throws Exception {
        TokenBasedRememberMeServices rmService = new TokenBasedRememberMeServices("cynzrm", userDetailsService);
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
//                .authorizeHttpRequests((authz) -> authz
//                        .anyRequest().authenticated()
//                )
                .addFilterBefore(
                        new InstitutionalUsernamePasswordAuthenticationFilter(new ProviderManager(authProvider), rmService),
                        UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .rememberMe().rememberMeServices(rmService)
                .and()
                .httpBasic(withDefaults()).authenticationProvider(authProvider)
                .csrf().disable();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/user/", "/ignore2");
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(PasswordEncoder encoder,
                                                            UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
