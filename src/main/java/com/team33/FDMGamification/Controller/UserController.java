package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.AdminDetails;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private ChallengeService challengeService;

    @RequestMapping({"/", "/home"})
    public String index(Model model){
        return "index";
    }

    @RequestMapping("/scenario")
    public String scenarioPage(Model model){
        model.addAttribute("scenarios", challengeService.getAll());
        return "scenarios";
    }
}
