package org.k8scmp.login.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.login.domain.LoginType;
import org.k8scmp.login.domain.UserPassword;
import org.k8scmp.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jason on 25/08/2017.
 */
@Controller
public class LoginController {

    @Autowired
    UserService userService;
    
    	
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login(Model model) {
    	model.addAttribute("UserPassword", new UserPassword());
        return "login/login";
    }
    

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public String normalLogin(Model model,@ModelAttribute UserPassword userPassword) {
    	userPassword.setLoginType(LoginType.USER);
    	HttpResponseTemp<?> resp = userService.normalLogin(userPassword);
    	
    	if(resp.getResultCode()==200){
    		model.addAttribute("userPassword",userPassword);
    		return "redirect:/overview";
    	}else{
    		if(resp.getResultMsg().split(":").length>0){
    			model.addAttribute("msg", resp.getResultMsg().split(":")[1]);
    		}else{
    			model.addAttribute("msg", resp.getResultMsg());
    		}
	    	return login(model);
    	}
    }

    @RequestMapping("/logout")
    public ModelAndView logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new ModelAndView("redirect:/login");
    }
   
}
