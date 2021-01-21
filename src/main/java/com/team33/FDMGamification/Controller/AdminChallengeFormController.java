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
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
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
    public String getChallengeView(@PathVariable("id") Integer id, Model model) {
        try {
            Challenge challenge = challengeService.findById(id);
            model.addAttribute("challenge", challenge);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
        return "admin/challengeForm";
    }

    @GetMapping(value = {"/new"})
    public String createChallenge(Model model) {
        if(model.getAttribute("challenge") == null) model.addAttribute("challenge", new Challenge());
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
                              @PathVariable(value = "id", required = false) Integer id,
                              HttpServletRequest request) {
        // For page scrolling to question section
        String uri = request.getRequestURI()+"?addQuestion";
        try {
            Question tempQ = new Question();
            challenge.getQuestions().add(tempQ);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return "redirect:"+uri;
    }

    @PostMapping(value = {"/new","/{id}"}, params = {"removeQuestion"})
    public String removeQuestion(@ModelAttribute("challenge") Challenge challenge,
                                 @PathVariable(value = "id", required = false) Integer id,
                                 @RequestParam("removeQuestion") Integer rmIdx,
                                 HttpServletRequest request) {
        try {
            Question question = challenge.getQuestions().get(rmIdx);
            challenge.getQuestions().remove(rmIdx.intValue());
            // For page scrolling to question section
            String uri = request.getRequestURI()+"?removeQuestion";
            // If it's form edit, delete the persisted question from database as well.
            if(question.getQuestionId() != null) questionService.delete(question.getQuestionId());
            return "redirect:"+uri;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping(value = {"/new","/{id}"}, params = {"addChoice"})
    public String addChoice(@ModelAttribute("challenge") Challenge challenge,
                                  @PathVariable(value = "id", required = false) Integer id,
                                  HttpServletRequest request,
                                  @RequestParam("addChoice") Integer qIdx) {
        // For page scrolling to question section
        String uri = request.getRequestURI()+"?addChoice="+ qIdx;
        try {
            Question question = challenge.getQuestions().get(qIdx);
            Choice tempC = new Choice();
            question.getChoices().add(tempC);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
        return "redirect:"+uri;
    }

    @PostMapping(value = "/{id}", params = {"removeChoice"})
    public String removeChoice(@ModelAttribute("challenge") Challenge challenge,
                               @PathVariable(value = "id", required = false) Integer id,
                               @RequestParam("removeChoice") List<Integer> ids,
                               HttpServletRequest request) {
        try {
            Integer qIdx = ids.get(0);
            Integer choiceIdx = ids.get(1);
            Question question = challenge.getQuestions().get(qIdx);
            Choice choice = question.getChoices().get(choiceIdx);
            question.getChoices().remove(choiceIdx.intValue());
            // For page scrolling to question section
            String uri = request.getRequestURI()+"?removeChoice="+ qIdx;
            // If it's form edit, delete the persisted choice from database as well.
            if(choice.getChoiceId() != null) choiceService.delete(choice.getChoiceId());
            return "redirect:"+uri;
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
