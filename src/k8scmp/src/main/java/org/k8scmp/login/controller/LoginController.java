package org.k8scmp.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by bysocket on 07/02/2017.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login() {
        return "demo/login";
    }
    
    @RequestMapping(value = "/index")
    public String index() {
        return "demo/index";
    }
    
    @RequestMapping(value = "/index")
    public String index() {
        return "demo/index";
    }
    
    
    
   
}
