package com.team33.FDMGamification.Controller;

import com.team33.FDMGamification.Model.AdminDetails;
import com.team33.FDMGamification.Service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChallengeService challengeService;

    @RequestMapping({"/", ""})
    public ModelAndView renderAdminHome(@AuthenticationPrincipal AdminDetails activeUser){
        ModelAndView mav = new ModelAndView("admin/adminHome");
        mav.addObject("challenges", challengeService.getAll());
        mav.addObject("admin", activeUser);
        return mav;
    }

    @RequestMapping("/{id}")
    public ModelAndView renderAdminHome(@PathVariable("id") Integer id){
        ModelAndView mav = new ModelAndView("admin/adminPanel");
        try {
            mav.addObject("challenge", challengeService.findById(id));
        } catch (EntityNotFoundException ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
        return mav;
    }
}
