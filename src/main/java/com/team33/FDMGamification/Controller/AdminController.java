package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.AdminDetails;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    @RequestMapping({"/", "", "{page:challenges|questions|choices}"})
    public String renderAdminHome(@AuthenticationPrincipal AdminDetails activeUser, Model model, @PathVariable("page") Optional<String> tab) {
        String tabRes = "";
        if(tab.isPresent()){
            tabRes = tab.get();
        }else{
            return "redirect:/admin/challenges";
        }
        switch (tabRes) {
            case "challenges":
                model.addAttribute("challenges", challengeService.getAll());
                break;
            case "questions":
                model.addAttribute("questions", questionService.getAll());
                break;
            case "choices":
                model.addAttribute("choices", choiceService.getAll());
                break;
        }
        model.addAttribute("admin", activeUser);
        return "admin/adminHome";
    }

    @PostMapping(value = "{page:challenges|questions|choices}", params = {"create"})
    public String createItem(ModelMap model, @PathVariable("page") String page) {
        switch (page) {
            case "questions":
                model.addAttribute("question", new Question());
                return "admin/questionForm";
            case "choices":
                model.addAttribute("choice", new Choice());
                return "admin/choiceForm";
            default:
                model.addAttribute("challenge", new Challenge());
                return "admin/challengeForm";
        }
    }

    @PostMapping(value = "{page:challenges|questions|choices}", params = {"delete", "ids"})
    public String deleteItem(ModelMap model, @RequestParam("ids") Integer[] ids, @PathVariable("page") String page) {
        model.remove(page.substring(0, page.length()-1));
        switch (page) {
            case "questions":
                for (Integer id : ids) {
                    questionService.delete(id);
                }
                return "redirect:/admin/questions";
            case "choices":
                for (Integer id : ids) {
                    choiceService.delete(id);
                }
                return  "redirect:/admin/choices";
            default:
                for (Integer id : ids) {
                    challengeService.delete(id);
                }
                return  "redirect:/admin/challenges";
        }
    }
}
