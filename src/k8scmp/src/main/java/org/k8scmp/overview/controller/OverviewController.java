package org.k8scmp.overview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.k8scmp.login.domain.User;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.util.AuthUtil;

/**
 * Created by jason on 2017/8/29.
 */
@Controller
public class OverviewController {
	@Autowired
    OperationLog operationLog;
    	
    @RequestMapping(value="/overview", method=RequestMethod.GET)
    public String overview(Model model) {
    	//获取当前登录用户
        User user = AuthUtil.getUser();
     	model.addAttribute("user", user);
     	
     	//获取操作日志
     	List<OperationRecord> operecords = operationLog.listOperationRecord4Overview();
     	model.addAttribute("operecords", operecords);
        return "overview/index";
    }
    
    @RequestMapping(value="/overview/opelog", method=RequestMethod.GET)
    public String showLog(Model model) {
    	//获取当前登录用户
        User user = AuthUtil.getUser();
     	model.addAttribute("user", user);
        return "overview/index";
    }
   
   
}
