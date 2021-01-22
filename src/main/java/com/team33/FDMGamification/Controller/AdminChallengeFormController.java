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
@SessionAttributes({"challenge"})
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
            if(!model.containsAttribute("challenge")) {
                Challenge challenge = challengeService.findById(id);
                model.addAttribute("challenge", challenge);
            }
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
                                final BindingResult bindingResult,
                                Model model,
                                @PathVariable(value = "id", required = false) Integer id,
                                @RequestPart("pic") MultipartFile picData,
                                SessionStatus status) {
        try {
            Thumbnail thumbnail = challenge.getThumbnail();
            if(thumbnail.getBase64String().isBlank() && thumbnail.getBase64String().isEmpty() && picData.isEmpty()){
                bindingResult.rejectValue("thumbnail", "EMPTY_THUMBNAIL", "Please add a thumbnail!");
            }
            if (bindingResult.hasErrors()) {
                return "admin/challengeForm";
            }
            processImage(picData, model, challenge);
            challenge.setThumbnail(thumbnail);
            if(id == null){
                challengeService.create(challenge);
            }else {
                challengeService.update(challenge);
            }
            status.setComplete();
            model.asMap().clear();
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
                              @RequestPart("pic") MultipartFile picData,
                              Model model,
                              HttpServletRequest request) {
        try {
            // Thumbnail update
            if(!picData.isEmpty()) {
                processImage(picData, model, challenge);
            }
            // For page scrolling to question section
            String uri = request.getRequestURI()+"?addQuestion";
            Question tempQ = new Question();
            challenge.addQuestion(tempQ);
            return "redirect:"+uri;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping(value = {"/new","/{id}"}, params = {"removeQuestion"})
    public String removeQuestion(@ModelAttribute("challenge") Challenge challenge,
                                 @PathVariable(value = "id", required = false) Integer id,
                                 @RequestParam("removeQuestion") Integer rmIdx,
                                 @RequestPart("pic") MultipartFile picData,
                                 Model model,
                                 HttpServletRequest request) {
        try {
            // Thumbnail update
            if(!picData.isEmpty()) {
                processImage(picData, model, challenge);
            }
            Question question = challenge.getQuestions().get(rmIdx);
            challenge.removeQuestion(rmIdx.intValue());
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
                            @RequestParam("addChoice") Integer qIdx,
                            @RequestPart("pic") MultipartFile picData,
                            Model model,
                            HttpServletRequest request) {
        try {
            // Thumbnail update
            if(!picData.isEmpty()) {
                processImage(picData, model, challenge);
            }
            // For page scrolling to question section
            String uri = request.getRequestURI()+"?addChoice="+ qIdx;
            Question question = challenge.getQuestionByIndex(qIdx);
            Choice tempC = new Choice();
            question.addChoice(tempC);
            return "redirect:"+uri;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping(value = "/{id}", params = {"removeChoice"})
    public String removeChoice(@SessionAttribute("challenge") Challenge challenge,
                               @PathVariable(value = "id", required = false) Integer id,
                               @RequestParam("removeChoice") List<Integer> ids,
                               @RequestPart("pic") MultipartFile picData,
                               Model model,
                               HttpServletRequest request) {
        try {
            // Thumbnail update
            if(!picData.isEmpty()) {
                processImage(picData, model, challenge);
            }
            Integer qIdx = ids.get(0);
            Integer choiceIdx = ids.get(1);
            Question question = challenge.getQuestionByIndex(qIdx);
            Choice choice = question.getChoiceByIndex(choiceIdx);
            question.removeChoice(choiceIdx.intValue());
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

    private void processImage(MultipartFile picture, Model model, Challenge challenge){
        try {
            Thumbnail thumbnail = challenge.getThumbnail();
            byte[] bytes = picture.getBytes();
            String base64 = Base64Utils.encodeToString(bytes);
            thumbnail.setBase64String(base64);
            thumbnail.setFileName(picture.getOriginalFilename());
            thumbnail.setFileType(picture.getContentType());
            model.addAttribute("challenge", challenge);
        } catch (IOException ioException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ioException.toString(), ioException);
        }
    }
}
