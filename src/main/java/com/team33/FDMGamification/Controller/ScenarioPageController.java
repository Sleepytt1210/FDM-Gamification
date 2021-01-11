package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;

@Controller
@RequestMapping("/scenario/{sid}")
@SessionAttributes({"scenario", "question"})
public class ScenarioPageController {

    @Autowired
    private ChallengeService challengeService;

    @ModelAttribute
    private Challenge challenge(@PathVariable("sid") Integer sid){
        try{
            return challengeService.findById(sid);
        } catch (EntityNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.toString(), ex);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString(), e);
        }
    }

    @GetMapping({"/", ""})
    public String scenarioPage(){
        return "questions";
    }

    @GetMapping("/{qid}")
    public String questionPage(Model model, @PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid){
        model.addAttribute("question", challengeService.findById(sid).getQuestion().get(qid));
        return "question";
    }

    @PostMapping("/{qid}")
    public String submitQuestion(Model model, @PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid
            , @RequestParam(value = "score0", required = false) Integer[] cids0,
                                 @RequestParam(value = "score1", required = false) Integer[] cids1,
                                 @RequestParam(value = "score2", required = false) Integer[] cids2) {
        System.out.println(Arrays.toString(cids0));
        System.out.println(Arrays.toString(cids1));
        System.out.println(Arrays.toString(cids2));
        return "question";
    }
}
