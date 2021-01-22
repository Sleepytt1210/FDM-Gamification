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
                System.out.println("sid changed");
                model.addAttribute("scenario", challengeService.findById(sid));
                model.addAttribute("questions", challengeService.getQuestions(sid));
            }
        } catch (EntityNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.toString(), ex);
        }
        return "scenario";
    }

    @GetMapping("/{qid}")
    public String questionPage(@PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid,
                               Model model){
        populateQuestionAndChoices(model, sid, qid);
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
                                 @RequestParam(value = "score0", required = false) Integer[] cids0,
                                 @RequestParam(value = "score1", required = false) Integer[] cids1,
                                 @RequestParam(value = "score2", required = false) Integer[] cids2
    ) {
        int score = 0;

        score += scoreCheck(cids0, 0);
        score += scoreCheck(cids1, 1);
        score += scoreCheck(cids2, 2);

        populateQuestionAndChoices(model, sid, qid);

        model.addAttribute("score", score);
        return "dragAndDropQuestion";
    }

    @PostMapping(value = "/{qid}", params = {"radioQuestion"})
    public String submitQuestion(Model model,
                                 @PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid,
                                 @RequestParam(value = "choices") Integer cid)
    {

        int score = 0;


        Choice correctChoice = choiceService.findById(cid);
        score = correctChoice.getChoiceWeight();

        populateQuestionAndChoices(model,sid,qid);
        model.addAttribute("score", score);


        return "radioQuestion";
    }

    @PostMapping(value = "/{qid}", params = {"textboxQuestion"})
    public String submitQuestion(Model model,
                                 @PathVariable("sid") Integer sid, @PathVariable("qid") Integer qid,
                                 @RequestParam(value = "answer") String answer)
    {
        int score = 0;

        List<Choice> choices = questionService.getChoices(qid);


        //The weight makes no difference on the textbox variations
        if (choices.get(0).getChoiceText().equalsIgnoreCase(answer)){
            score+=2;
        }
        populateQuestionAndChoices(model,sid,qid);
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



    private void populateQuestionAndChoices(Model model, Integer sid, Integer qid) {
        try {
            Question question = questionService.findById(qid);
            model.addAttribute("questions", challengeService.getQuestions(sid));
            if(question == null) {
                throw new EntityNotFoundException("Question id " + qid + " does not exist in scenario id " + sid + " !!");
            }
            List<Choice> choices = questionService.getChoices(qid);
            model.addAttribute("question", question);
            model.addAttribute("choices", choices);
        } catch (EntityNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.toString(), ex);
        }
    }
}
