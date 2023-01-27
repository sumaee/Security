package com.cos.secutiry1.config.oauth;

import com.cos.secutiry1.config.auth.PrincipalDetails;
import com.cos.secutiry1.model.User;
import com.cos.secutiry1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록 해줌
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    //구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
    // 함수 종료 시 @AuthenticationPrincipal 이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        System.out.println(userRequest.getClientRegistration()); :
            //registrationId, clientId, clientSecret, clientAuthenticationMethod, redirectUri, scopes, providerDetails, clientName 이 주어짐
//        System.out.println(userRequest.getAccessToken().getTokenValue()); :
            //token이 주어짐
//        System.out.println(super.loadUser(userRequest).getAttributes());
            //사용자의 정보가 주어짐
        OAuth2User oauth2User = super.loadUser(userRequest);

        //회원가입을 강제로 진행해볼 예정
        String provider = userRequest.getClientRegistration().getClientId(); // google
        String providerId = oauth2User.getAttribute("sub");
        String username = provider+"-"+providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oauth2User.getAttribute("email");
        String role = "ROLE_USER";


         User userEntity = userRepository.findByUsername(username);
         // null이라면 회원가입 진행
         if(userEntity == null){
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
             userRepository.save(userEntity);
         }

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
