package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.AdminDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @RequestMapping({"/", "/home"})
    public String index(){
        return "index";
    }
}
