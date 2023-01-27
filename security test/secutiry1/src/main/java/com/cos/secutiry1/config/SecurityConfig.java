package com.cos.secutiry1.config;

import com.cos.secutiry1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // controller에서 @secured 활성화, @preAuthorize 활성화
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
       return http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN')or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                //낚아챘을 때 권한이 없으면 login 페이지로 이동시킴
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // login 주소가 호출되면 시큐리티가 낚아채서 로그인을 진행해줌 그래서 controller에 login 메서드를 안만들어도됨
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                //구글 로그인이 완료된 뒤의 후처리가 필요함
                //1. 코드받기 (인증)
                //2. 엑세스 토큰 받기(권한)
                //3. 사용자 프로필 정보를 가져오기
                //4. 정보를 토대로 회원가입을 자동으로 진행
                // 구글 로그인이 완료가 되면 코드를 받는게 아닌 엑세스토큰 + 사용자 프로필 정보를 받게 됨
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
               .and().and().build();


    }
}
