package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    @Autowired
    private ChallengeService challengeService;

    @RequestMapping({"/", "/home"})
    public String index(Model model){
        return "index";
    }

    @RequestMapping("/scenario")
    public String scenariosPage(Model model){
        return "redirect:/explore";
    }

    @RequestMapping("/explore")
    public String explorePage(Model model){
        model.addAttribute("ST", challengeService.findByStream("ST"));
        model.addAttribute("BI", challengeService.findByStream("BI"));
        model.addAttribute("TO", challengeService.findByStream("TO"));
        return "explore";
    }

    @RequestMapping("/leaderboard")
    public String leaderboard(Model model){
        return "leaderboard";
    }

    @RequestMapping("/radio")
    public String testRadio(Model model){return "radioQuestion";}
}
