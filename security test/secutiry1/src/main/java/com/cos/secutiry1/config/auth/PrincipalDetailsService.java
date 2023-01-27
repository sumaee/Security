package com.cos.secutiry1.config.auth;

import com.cos.secutiry1.model.User;
import com.cos.secutiry1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login")
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행이됨
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    //시큐리티 session(Authentication(내부 UserDetails))
    // 함수 종료 시 @AuthenticationPrincipal 이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = repository.findByUsername(username);

        if(userEntity !=null)
            return new PrincipalDetails(userEntity);

        return null;
    }
}
