package org.k8scmp.login.controller;

import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.login.domain.ChangeUserPassword;
import org.k8scmp.login.domain.LoginType;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.UserPassword;
import org.k8scmp.login.service.UserService;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.operation.OperationType;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jason on 2017/9/4.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    protected static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;
    @Autowired
    OperationLog operationLog;

    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listAllUsers(Model model,@RequestParam(defaultValue="") String info) {
        long userId = AuthUtil.getUserId();
        if (userId <= 0) {
            model.addAttribute("info","请先登录");
        }
        
        HttpResponseTemp<?> resp = userService.listAllUserInfo();
        List<User> users = (List<User>) resp.getResult();
        //获取当前登录用户
        User user = AuthUtil.getUser();
    	model.addAttribute("user", user);
    	if(!model.containsAttribute("info")) model.addAttribute("info","");
        model.addAttribute("users",users);
        model.addAttribute("User", new User());
        model.addAttribute("UserPassword", new UserPassword());
        return "login/user-list";
    }
    

    @RequestMapping(value="/create", method = RequestMethod.GET)
    public String create(Model model) {
    	//获取当前登录用户
        User user = AuthUtil.getUser();
    	model.addAttribute("user", user);
    	model.addAttribute("User", new User());
        return "login/user-new";
    }
    
    @RequestMapping(value="/create", method = RequestMethod.POST)
    public String createUser(Model model,@ModelAttribute User addUserInfo) {
        User user = new User(addUserInfo.getLoginname(),addUserInfo.getPassword());
        user.setUsername(addUserInfo.getUsername());
        user.setEmail(addUserInfo.getEmail());
        user.setPhone(addUserInfo.getPhone());
        HttpResponseTemp<?> resp = userService.createUser(user);
        String info = "用户新增成功";
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info",info);
    		
    	}else{
    		info ="用户新增失败";
    		state="fail";
    		model.addAttribute("info",info);
    	}
    	operationLog.insertRecord(new OperationRecord(
    			addUserInfo.getLoginname(), 
				ResourceType.CONFIGURATION,
				OperationType.DELETEUSER, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));
        return "redirect:/user/list?info="+info;
    }

    @RequestMapping(value="/delete/{loginname}", method = RequestMethod.GET)
    public String deleteUser(Model model,@PathVariable String loginname) {
    	HttpResponseTemp<?> resp =  userService.deleteUser(loginname);
    	String info = "用户删除成功";
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info",info);
    		
    	}else{
    		info ="用户删除失败";
    		state="fail";
    		model.addAttribute("info",info);
    	}
    	operationLog.insertRecord(new OperationRecord(
    			loginname, 
				ResourceType.CONFIGURATION,
				OperationType.DELETEUSER, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));
    	return "redirect:/user/list";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public HttpResponseTemp<?> modifyPassword(Model model,@ModelAttribute ChangeUserPassword changeUserPassword) {
        return userService.changePassword(changeUserPassword);
    }

    @RequestMapping(value = "/adminChangePassword", method = RequestMethod.POST)
    public String modifyPassword(Model model,@ModelAttribute UserPassword userPassword) {
        HttpResponseTemp<?> resp =  userService.changePasswordByAdmin(userPassword);
        String info = "用户密码修改成功";
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info",info);
    		
    	}else{
    		info ="用户密码修改失败";
    		state="fail";
    		model.addAttribute("info",info);
    	}
    	operationLog.insertRecord(new OperationRecord(
    			userPassword.getLoginname(), 
				ResourceType.CONFIGURATION,
				OperationType.MODIFYUSER, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));
    	return "redirect:/user/list";
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public String modifyUser(Model model,@ModelAttribute User user) {
    	HttpResponseTemp<?> resp =   userService.modifyUser(user);
    	String info = "用户信息修改成功";
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info",info);
    		
    	}else{
    		info ="用户信息修改失败";
    		state="fail";
    		model.addAttribute("info",info);
    	}
    	operationLog.insertRecord(new OperationRecord(
    			user.getLoginname(), 
				ResourceType.CONFIGURATION,
				OperationType.MODIFYUSER, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));
    	return "redirect:/user/list";
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public String getUser(Model model,@RequestParam(defaultValue="") String keyword) {
    	HttpResponseTemp<?> resp =  userService.listUsersByKW("%"+keyword+"%");
    	List<User> users = (List<User>) resp.getResult();
    	//获取当前登录用户
        User user = AuthUtil.getUser();
     	model.addAttribute("user", user);
     	if(!model.containsAttribute("info")) model.addAttribute("info","");
        model.addAttribute("users",users);
        model.addAttribute("User", new User());
        model.addAttribute("keyword", keyword);
        model.addAttribute("UserPassword", new UserPassword());
        return "login/user-list";
    }

    @RequestMapping(value = "/get/{loginname}", method = RequestMethod.GET)
    public String getCurrentLoginUser(Model model,@PathVariable String loginname) {
    	//获取当前登录用户
        User user = AuthUtil.getUser();
    	model.addAttribute("user", user);
    	
        HttpResponseTemp<?> resp = userService.getUserInfo(loginname);
        User getuser = (User) resp.getResult();
        model.addAttribute("User", getuser);
        return "login/user-edit";
    }

    
    
}
