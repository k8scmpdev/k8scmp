package org.k8scmp.login.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.login.domain.UserPassword;
import org.k8scmp.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by bysocket on 07/02/2017.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login() {
        return "login/login";
    }
    
    @RequestMapping(value = "/index")
    public String index() {
        return "demo/index";
    }
    
    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public HttpResponseTemp<?> normalLogin(@ModelAttribute UserPassword userPassword) {
        return userService.normalLogin(userPassword);
    }

    @RequestMapping("/logout")
    public ModelAndView logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new ModelAndView("redirect:/login/login.html");
    }
   
}
