package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.AdminDetails;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@SessionAttributes("challenge")
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

    @GetMapping("/challenges/{id}")
    public ModelAndView getChallengeView(@PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView("admin/form");
        try {
            Challenge challenge = challengeService.findById(id);
            mav.addObject("challenge", challenge);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
        return mav;
    }


    @PostMapping(value = "/challenges", params = {"create"})
    public String createChallenge(ModelMap model, @RequestParam("create") String create) {
        Challenge challenge = new Challenge();
        model.addAttribute("challenge", challenge);
        return "admin/form";
    }

    @PostMapping(value = "/challenges", params = {"delete", "chlids"})
    public String deleteChallenge(ModelMap model, @RequestParam("chlids") Integer[] chlids) {
        model.remove("challenge");
        System.out.println(Arrays.toString(chlids));
        for (Integer id : chlids) {
            challengeService.delete(id);
        }
        return "redirect:/admin/challenges";
    }

    @PostMapping(value = "/challenges/{id}", params = {"save"})
    public String updateChallenge(@ModelAttribute("challenge") Challenge challenge, @PathVariable("id") Integer id, final BindingResult bindingResult, ModelMap model, SessionStatus status) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/form";
            }
            challengeService.update(id, challenge);
            status.setComplete();
            model.clear();
            return "redirect:/admin";
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (NullPointerException ne) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, ne.getMessage(), ne);
        }
    }

    @PostMapping(value = "/challenges/{id}", params = {"addQuestion"})
    public ModelAndView addQuestion(@ModelAttribute("challenge") Challenge challenge) {
        ModelAndView mav = new ModelAndView("admin/form");
        try {
            Question tempQ = new Question();
            questionService.create(challenge, tempQ);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return mav;
    }

    @PostMapping(value = "/challenges/{id}", params = {"removeQuestion"})
    public String removeQuestion(@ModelAttribute("challenge") Challenge challenge, @RequestParam("removeQuestion") Integer rmId) {
        try {
            challenge.getQuestion().remove(rmId);
            questionService.delete(rmId);
            return "admin/form";
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping(value = "/challenges/{id}", params = {"addChoice"})
    public ModelAndView addChoice(@ModelAttribute("challenge") Challenge challenge, @RequestParam("addChoice") Integer qid) {
        ModelAndView mav = new ModelAndView("admin/form");
        try {
            Question question = challenge.getQuestion().get(qid);
            Choice tempC = new Choice();
            choiceService.create(question, tempC);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return mav;
    }

    @PostMapping(value = "/challenges/{id}", params = {"removeChoice"})
    public String removeChoice(@ModelAttribute("challenge") Challenge challenge, @RequestParam("removeChoice") List<Integer> ids) {
        try {
            Integer qid = ids.get(0);
            Integer choiceId = ids.get(1);
            challenge.getQuestion().get(qid).getChoices().remove(choiceId);
            choiceService.delete(choiceId);
            return "admin/form";
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
