package com.seuunng.todolist.login;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping
    public String home(Model model, OidcUser oidcUser, OAuth2User oauth2User) {
        if (oidcUser != null) {
            model.addAttribute("userName", oidcUser.getFullName());
        } else if (oauth2User != null) {
            model.addAttribute("userName", oauth2User.getAttribute("name"));
        } else {
            model.addAttribute("userName", "Anonymous");
        }
        return "home"; // home.html 또는 home.jsp 파일이 필요합니다.
    }
    
    @GetMapping("/")
    public String index() {
        return "index";
    }

//    @GetMapping("/home")
//    public String home(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
//        model.addAttribute("name", oidcUser.getFullName());
//        return "home";
//    }
//
//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }
}
