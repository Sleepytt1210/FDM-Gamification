package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/scenario/{sid}")
public class ScenarioPageController {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChoiceService choiceService;

    @GetMapping({"/", ""})
    public String scenarioPage(Model model, @ModelAttribute("scenario") Challenge scenario, @PathVariable("sid") Integer sid){
        try {
            if (!sid.equals(scenario.getId())) {
                model.addAttribute("scenario", challengeService.findById(sid));
                model.addAttribute("questions", questionService.getQuestions(sid));
            }
        } catch (EntityNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.toString(), ex);
        }
        return "scenario";
    }

    @GetMapping("/{qid}")
    public String questionPage(@PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid,
                               Model model){
        populateChallengeQuestionAndChoices(model, sid, qid);
        Question question = (Question) model.getAttribute("question");
        switch (question.getQuestionType()){
            case TEXTBOX: return "textboxQuestion";
            case MULTIPLE_CHOICE: return "radioQuestion";
            case DRAG_DROP: return "dragAndDropQuestion";
        }
        return "dragAndDropQuestion";
    }

    @PostMapping(value = "/{qid}", params = {"dragAndDrop"})
    public String submitQuestion(Model model,
                                 @PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid,
                                 @RequestParam(value = "score0") Integer[] cids0,
                                 @RequestParam(value = "score1") Integer[] cids1,
                                 @RequestParam(value = "score2") Integer[] cids2,
                                 @RequestParam(value = "compInc", required = false) Integer compInc
    ) {
        int score = 0;

        score += scoreCheck(cids0, 0);
        score += scoreCheck(cids1, 1);
        score += scoreCheck(cids2, 2);

        if(compInc != null) {
            System.out.println("Question completion increase by 1");
            Question question = questionService.findById(qid);
            questionService.completionIncrement(question);
        }

        populateChallengeQuestionAndChoices(model, sid, qid);

        model.addAttribute("score", score);
        return "dragAndDropQuestion";
    }

    @PostMapping(value = "/{qid}", params = {"radioQuestion"})
    public String submitQuestion(Model model,
                                 @PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid,
                                 @RequestParam(value = "choices") Integer cid,
                                 @RequestParam(value = "compInc", required = false) Integer compInc
    )
    {

        int score = 0;

        if(compInc != null) {
            System.out.println("Question completion increase by 1");
            Question question = questionService.findById(qid);
            questionService.completionIncrement(question);
        }

        Choice correctChoice = choiceService.findById(cid);
        if (correctChoice.getChoiceWeight() == 1){
            score++;
        }

        populateChallengeQuestionAndChoices(model,sid,qid);
        model.addAttribute("score", score);


        return "radioQuestion";
    }

    @PostMapping(value = "/{qid}", params = {"textboxQuestion"})
    public String submitQuestion(Model model,
                                 @PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid,
                                 @RequestParam(value = "answer") String answer,
                                 @RequestParam(value = "compInc", required = false) Integer compInc
    )
    {
        int score = 0;

        if(compInc != null) {
            System.out.println("Question completion increase by 1");
            Question question = questionService.findById(qid);
            questionService.completionIncrement(question);
        }

        List<Choice> correctChoice = choiceService.getChoices(qid);
        if (correctChoice.get(0).getChoiceText().contains(answer)){
            score+=2;
        }
        populateChallengeQuestionAndChoices(model,sid,qid);
        model.addAttribute("score", score);


        return "textboxQuestion";
    }




    private int scoreCheck(Integer[] cids, int weight){
        int score = 0;
        if(cids != null) {
            for (Integer cid : cids) {
                Choice choice = choiceService.findById(cid);
                if (choice.getChoiceWeight() == weight) score++;
            }
        }
        return score;
    }



    private void populateChallengeQuestionAndChoices(Model model, Integer sid, Integer qid) {
        try {
            model.addAttribute("scenario", challengeService.findById(sid));
            Question question = questionService.findById(qid);
            model.addAttribute("questions", questionService.getQuestions(sid));
            if(question == null) {
                throw new EntityNotFoundException("Question id " + qid + " does not exist in scenario id " + sid + " !!");
            }
            List<Choice> choices = choiceService.getChoices(qid);
            model.addAttribute("question", question);
            model.addAttribute("choices", choices);
        } catch (EntityNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.toString(), ex);
        }
    }
}
