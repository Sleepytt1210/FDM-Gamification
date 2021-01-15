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

    @PostMapping(value = "/{id}", headers=("content-type=multipart/*"), params = {"save"})
    public String saveChallenge(@ModelAttribute("challenge") Challenge challenge, @PathVariable("id") Integer id,
                                @RequestPart("pic") MultipartFile picData,
                                final BindingResult bindingResult, ModelMap model,
                                SessionStatus status) {
        try {
            if (bindingResult.hasErrors()) {
                return "admin/challengeForm";
            }
            Thumbnail thumbnail = challenge.getThumbnail();
            processImage(picData, thumbnail);
            challenge.setThumbnail(thumbnail);
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

    @PostMapping(value = "/{id}", params = {"addQuestion"})
    public ModelAndView addQuestion(@ModelAttribute("challenge") Challenge challenge) {
        ModelAndView mav = new ModelAndView("admin/challengeForm");
        try {
            Question tempQ = new Question();
            questionService.create(challenge, tempQ);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return mav;
    }

    @PostMapping(value = "/{id}", params = {"removeQuestion"})
    public String removeQuestion(@ModelAttribute("challenge") Challenge challenge, @RequestParam("removeQuestion") Integer rmId) {
        try {
            challenge.getQuestions().remove(rmId);
            questionService.delete(rmId);
            return "admin/challengeForm";
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping(value = "/{id}", params = {"addChoice"})
    public ModelAndView addChoice(@ModelAttribute("challenge") Challenge challenge, @RequestParam("addChoice") Integer qid) {
        ModelAndView mav = new ModelAndView("admin/challengeForm");
        try {
            Question question = challenge.getQuestions().get(qid);
            Choice tempC = new Choice();
            choiceService.create(question, tempC);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return mav;
    }

    @PostMapping(value = "/{id}", params = {"removeChoice"})
    public String removeChoice(@ModelAttribute("challenge") Challenge challenge, @RequestParam("removeChoice") List<Integer> ids) {
        try {
            Integer qid = ids.get(0);
            Integer choiceId = ids.get(1);
            challenge.getQuestions().get(qid).getChoices().remove(choiceId);
            choiceService.delete(choiceId);
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
