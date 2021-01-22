package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.*;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/questions")
@SessionAttributes("question")
public class AdminQuestionFormController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    @ModelAttribute("allChallenges")
    public List<Challenge> challenges() {
        return challengeService.getAll();
    }

    @ModelAttribute("allQuestionType")
    public QuestionType[] questionTypes() {
        return QuestionType.values();
    }

    @ModelAttribute("invalidChallenge")
    public Challenge invalidChallenge() {
        Challenge challenge = new Challenge();
        challenge.setId(-1);
        return challenge;
    }

    @GetMapping("/{id}")
    public ModelAndView getQuestionView(@PathVariable("id") Integer id, Model model) {
        ModelAndView mav = new ModelAndView("admin/questionForm");
        try {
            if(!model.containsAttribute("question")) {
                Question question = questionService.findById(id);
                mav.addObject("question", question);
            }
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
        return mav;
    }

    @GetMapping("/new")
    public String createQuestion(Model model) {
        if(!model.containsAttribute("question")) model.addAttribute("question", new Question());
        return "admin/questionForm";
    }

    @PostMapping(value = {"/new", "/{id}"}, params = "save")
    public String saveQuestion(@Valid @ModelAttribute("question") Question question,
                               final BindingResult bindingResult, ModelMap model,
                               @PathVariable(value = "id", required = false) Integer id,
                               @RequestParam("challenge.id") Integer challengeId,
                               SessionStatus status) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/questionForm";
            }
            // Create a new entity if the path is not id
            if(id == null) {
                questionService.create(challengeId, question);
            } else {
                questionService.update(id, question);
            }
            status.setComplete();
            model.clear();
            return "redirect:/admin/questions";
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (NullPointerException ne) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, ne.getMessage(), ne);
        }
    }

    @PostMapping(value = {"/new","/{id}"}, params = {"addChoice"})
    public String addChoice(@ModelAttribute("question") Question question,
                            @PathVariable(value = "id", required = false) Integer id,
                            HttpServletRequest request) {
        String uri = request.getRequestURI()+"?addChoice";
        try {
            Choice tempC = new Choice();
            question.getChoices().add(tempC);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return "redirect:"+uri;
    }

    @PostMapping(value = {"/new","/{id}"}, params = {"removeChoice"})
    public String removeChoice(@ModelAttribute("question") Question question,
                               @PathVariable(value = "id", required = false) Integer id,
                               @RequestParam("removeChoice") Integer idx,
                               HttpServletRequest request) {
        try {
            String uri = request.getRequestURI()+"?addChoice";
            Choice choice = question.getChoices().get(idx);
            question.getChoices().remove(idx.intValue());
            // If it's form edit, delete the persisted choice from database as well.
            if(choice.getChoiceId() != null) choiceService.delete(choice.getChoiceId());
            return "redirect:"+uri;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

}
