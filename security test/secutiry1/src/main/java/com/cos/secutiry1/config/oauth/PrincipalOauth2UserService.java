package com.cos.secutiry1.config.oauth;

import com.cos.secutiry1.config.auth.PrincipalDetails;
import com.cos.secutiry1.config.auth.provider.GoogleUserInfo;
import com.cos.secutiry1.config.auth.provider.NaverUserInfo;
import com.cos.secutiry1.config.auth.provider.OAuth2UserInfo;
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

import java.util.Map;
import java.util.Objects;

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
        System.out.println(super.loadUser(userRequest).getAttributes());
            //사용자의 정보가 주어짐
        OAuth2User oauth2User = super.loadUser(userRequest);

        //회원가입을 강제로 진행해볼 예정
        OAuth2UserInfo oAuth2UserInfo = null;
        if(Objects.equals(userRequest.getClientRegistration().getRegistrationId(), "google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        }else if(Objects.equals(userRequest.getClientRegistration().getRegistrationId(), "naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        }else{
            System.out.println("구글과 네이버만 로그인 가능");
        }



        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"-"+providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
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
         }else{
             System.out.println("로그인을 한 적이 있음. 자동 회원가입이 되어있다.");
         }

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
