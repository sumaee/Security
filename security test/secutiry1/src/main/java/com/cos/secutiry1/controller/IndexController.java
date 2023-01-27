package com.cos.secutiry1.controller;

import com.cos.secutiry1.config.auth.PrincipalDetails;
import com.cos.secutiry1.model.User;
import com.cos.secutiry1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; // 패스워드 암호화

    //소셜 로그인에서는 진행이 안됨 -> 소셜로그인으로는 PrincipalDetails가 안먹음
    @GetMapping("/test/login")
    public @ResponseBody String loginTest(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){
        System.out.println("/test/login ====================================");
        // Principal()의 반환 타입이 Object이기 때문에 다운 캐스팅 후 찍으면 user 정보가 나옴
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        //둘다 같은 정보가 넘어옴 -> PrincipalDetails 가 UserDetails를 extends 하고 있기 때문에
        System.out.println("authentication: " + principalDetails.getUser()); // Authentication authentication 사용
        System.out.println("UserDetails: " + userDetails.getUser()); //@AuthenticationPrincipal PrincipalDetails userDetails 사용
        return "세션 정보 확인";
    }
    
    @GetMapping("/test/oauth/login")
    public @ResponseBody String loginOAuthTest(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("/test/oauth/login ====================================");
        // Principal()의 반환 타입이 Object이기 때문에 다운 캐스팅 후 찍으면 user 정보가 나옴
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        //둘다 같은 정보가 넘어옴
        System.out.println("authentication: " + oAuth2User.getAuthorities()); //Authentication authentication 사용
        System.out.println("oauth2User: " + oauth.getAttributes()); //@AuthenticationPrincipal OAuth2User oauth 사용
        return "OAuth 세션 정보 확인";
    }

    @GetMapping({"", "/"})
    public String index(){
        return "index";
    }

    //일반 로그인과 OAuth 로그인으로 해도 PrincipalDetails 로 받을 수 있음
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails: "+ principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "maneger";
    }

    //스프링 시큐리티가 해당 주소를 낚아챔
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        user.setRole("ROLE_USER");
        String encPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);
        repository.save(user);
        return "redirect:/loginForm";
    }
//    @Secured("ROLE_ADMIN") //OR 조건만 사용 가능
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // OR 조건, AND 조건 둘다 사용 가능
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

}
