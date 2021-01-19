package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.*;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/challenges")
@SessionAttributes("challenge")
public class AdminChallengeFormController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    @ModelAttribute("allStreams")
    public Stream[] streams() {
        return Stream.values();
    }

    @ModelAttribute("allQuestionType")
    public QuestionType[] questionTypes() {
        return QuestionType.values();
    }

    @GetMapping("/{id}")
    public ModelAndView getChallengeView(@PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView("admin/challengeForm");
        try {
            Challenge challenge = challengeService.findById(id);
            mav.addObject("challenge", challenge);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
        return mav;
    }

    @GetMapping(value = {"/new"})
    public String createChallenge(ModelMap model) {
        model.addAttribute("challenge", new Challenge());
        return "admin/challengeForm";
    }

    @PostMapping(value = {"/new", "/{id}"}, headers=("content-type=multipart/*"), params = {"save"})
    public String saveChallenge(@Valid @ModelAttribute("challenge") Challenge challenge,
                                final BindingResult bindingResult, ModelMap model,
                                @PathVariable(value = "id", required = false) Integer id,
                                @RequestPart("pic") MultipartFile picData,
                                SessionStatus status) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/challengeForm";
            }
            Thumbnail thumbnail = challenge.getThumbnail();
            processImage(picData, thumbnail);
            challenge.setThumbnail(thumbnail);
            if(id == null){
                challengeService.create(challenge);
            }else {
                challengeService.update(id, challenge);
            }
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

    @PostMapping(value = {"/new","/{id}"}, params = {"addQuestion"})
    public String addQuestion(@ModelAttribute("challenge") Challenge challenge,
                                    @PathVariable(value = "id", required = false) Integer id) {
        try {
            Question tempQ = new Question();
            challenge.getQuestions().add(tempQ);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return "admin/challengeForm";
    }

    @PostMapping(value = {"/new","/{id}"}, params = {"removeQuestion"})
    public String removeQuestion(@ModelAttribute("challenge") Challenge challenge,
                                 @PathVariable(value = "id", required = false) Integer id,
                                 @RequestParam("removeQuestion") Integer rmIdx) {
        try {
            Question question = challenge.getQuestions().get(rmIdx);
            challenge.getQuestions().remove(rmIdx.intValue());
            // If it's form edit, delete the persisted question from database as well.
            if(question.getQuestionId() != null) questionService.delete(question.getQuestionId());
            return "admin/challengeForm";
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping(value = {"/new","/{id}"}, params = {"addChoice"})
    public ModelAndView addChoice(@ModelAttribute("challenge") Challenge challenge,
                                  @PathVariable(value = "id", required = false) Integer id,
                                  @RequestParam("addChoice") Integer qIdx) {
        ModelAndView mav = new ModelAndView("admin/challengeForm");
        try {
            Question question = challenge.getQuestions().get(qIdx);
            Choice tempC = new Choice();
            question.getChoices().add(tempC);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return mav;
    }

    @PostMapping(value = "/{id}", params = {"removeChoice"})
    public String removeChoice(@ModelAttribute("challenge") Challenge challenge,
                               @PathVariable(value = "id", required = false) Integer id,
                               @RequestParam("removeChoice") List<Integer> ids) {
        try {
            Integer qIdx = ids.get(0);
            Integer choiceIdx = ids.get(1);
            Question question = challenge.getQuestions().get(qIdx);
            Choice choice = question.getChoices().get(choiceIdx);
            question.getChoices().remove(choiceIdx.intValue());
            // If it's form edit, delete the persisted choice from database as well.
            if(choice.getChoiceId() != null) choiceService.delete(choice.getChoiceId());
            return "admin/challengeForm";
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    private void processImage(MultipartFile picture, Thumbnail thumbnail){
        try {
            byte[] bytes = picture.getBytes();
            String base64 = Base64Utils.encodeToString(bytes);
            thumbnail.setBase64String(base64);
            thumbnail.setFileName(picture.getOriginalFilename());
            thumbnail.setFileType(picture.getContentType());
        } catch (IOException ioException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ioException.toString(), ioException);
        }
    }
}
