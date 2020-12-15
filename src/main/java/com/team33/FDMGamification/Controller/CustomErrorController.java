package com.team33.FDMGamification.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

        ModelAndView errorPage = new ModelAndView("error");
        String errorMsg = RequestDispatcher.ERROR_MESSAGE;
        Object status = httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Integer statusCode = Integer.valueOf(status.toString());

        if(errorMsg == null || errorMsg.equals("")) {
            switch (statusCode) {
                case 400: {
                    errorMsg = "Http Error Code: 400. Bad Request";
                    break;
                }
                case 401: {
                    errorMsg = "Http Error Code: 401. Unauthorized";
                    break;
                }
                case 404: {
                    errorMsg = "Http Error Code: 404. Resource not found";
                    break;
                }
                case 500: {
                    errorMsg = "Http Error Code: 500. Internal Server Error";
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
