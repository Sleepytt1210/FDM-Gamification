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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    @RequestMapping({"/", ""})
    public ModelAndView renderAdminHome(@AuthenticationPrincipal AdminDetails activeUser) {
        ModelAndView mav = new ModelAndView("admin/adminHome");
        mav.addObject("challenges", challengeService.getAll());
        mav.addObject("admin", activeUser);
        return mav;
    }

    @GetMapping("/challenge/{id}")
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


    @PostMapping(value = "/challenge/", params = {"create"})
    public String updateChallenge(ModelMap model) {
        Challenge challenge = new Challenge();
        model.addAttribute("challenge", challenge);
        return "admin/form";
    }

    @PostMapping(value = "/challenge/{id}", params = {"save"})
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

    @PostMapping(value = "/challenge/{id}", params = {"addQuestion"})
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

    @PostMapping(value = "/challenge/{id}", params = {"removeQuestion"})
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

    @PostMapping(value = "/challenge/{id}", params = {"addChoice"})
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

    @PostMapping(value = "/challenge/{id}", params = {"removeChoice"})
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
