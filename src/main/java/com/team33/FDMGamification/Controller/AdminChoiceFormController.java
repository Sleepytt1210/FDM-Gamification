package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.*;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/admin/choices")
@SessionAttributes("choice")
public class AdminChoiceFormController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    @ModelAttribute("allQuestions")
    public List<Question> questions() {
        return questionService.getAll();
    }

    @ModelAttribute("invalidQuestion")
    public Question invalidQuestion() {
        Question question = new Question();
        question.setQuestionId(-1);
        return question;
    }

    @GetMapping("/{id}")
    public ModelAndView getChoiceView(@PathVariable("id") Integer id, ModelMap model) {
        ModelAndView mav = new ModelAndView("admin/choiceForm");
        try {
            if(!model.containsAttribute("choice")) {
                Choice choice = choiceService.findById(id);
                mav.addObject("choice", choice);
            }
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
        return mav;
    }

    @GetMapping("/new")
    public String createChoice(ModelMap model) {
        if(!model.containsAttribute("choice")) model.addAttribute("choice", new Choice());
        return "admin/choiceForm";
    }

    @PostMapping(value = {"/new", "/{id}"}, params = "save")
    public String saveChoice(@Valid @ModelAttribute("choice") Choice choice,
                               final BindingResult bindingResult, ModelMap model,
                               @PathVariable(value = "id", required = false) Integer id,
                               @RequestParam("question.questionId") Integer questionId,
                               SessionStatus status) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/choiceForm";
            }
            // Create a new entity if the path is not id
            System.out.println("Choice is saved!");
            if(id == null) {
                choiceService.create(questionId, choice);
            } else {
                choiceService.update(id, choice.getChoiceText(), choice.getChoiceWeight(), choice.getChoiceReason(), questionId);
            }
            status.setComplete();
            model.clear();
            return "redirect:/admin/choices";
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (NullPointerException ne) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, ne.getMessage(), ne);
        }
    }
}
