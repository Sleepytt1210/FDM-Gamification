package com.team33.FDMGamification.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView renderErrorPage(HttpServletRequest request) {

        ModelAndView errorPage = new ModelAndView("error");
        String errorMsg = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Integer statusCode = Integer.valueOf(status.toString());

        if(errorMsg == null || errorMsg.isBlank() || errorMsg.isEmpty()) {
            switch (statusCode) {
                case 400: {
                    errorMsg = "Bad Request";
                    break;
                }
                case 401: {
                    errorMsg = "Unauthorized";
                    break;
                }
                case 404: {
                    errorMsg = "Resource not found";
                    break;
                }
                case 500: {
                    errorMsg = "Internal Server Error";
                    break;
                }
            }
        }
        errorPage.addObject("errorCode", statusCode);
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
