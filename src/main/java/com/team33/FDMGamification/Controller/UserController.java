package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private ChallengeService challengeService;

    @RequestMapping({"/", "/home"})
    public String index(Model model){
        return "index";
    }

    @RequestMapping("/scenarios")
    public String scenariosPage(Model model){
        model.addAttribute("ST", challengeService.findByStream("ST"));
        model.addAttribute("BI", challengeService.findByStream("BI"));
        model.addAttribute("TO", challengeService.findByStream("TO"));
        return "scenarios";
    }

    @RequestMapping("/leaderboard")
    public String leaderboard(Model model){
        return "leaderboard";
    }

    @GetMapping("/scenario/{id}")
    public String scenarioPage(Model model, @PathVariable("id") Integer id){
        model.addAttribute("scenario", challengeService.findById(id));
        return "questions";
    }
}
