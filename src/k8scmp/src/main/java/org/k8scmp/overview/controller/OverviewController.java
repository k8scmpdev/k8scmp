package org.k8scmp.overview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.k8scmp.login.domain.User;

/**
 * Created by jason on 2017/8/29.
 */
@Controller
public class OverviewController {
    
    	
    @RequestMapping(value="/overview", method=RequestMethod.GET)
    public String overview(Model model) {
    	String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
    	User user = new User();
    	user.setUsername(currentUser);
    	model.addAttribute("user", user);
        return "overview/index";
    }
    

   
   
}
