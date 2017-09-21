package org.k8scmp.monitormgmt.controller.alarm;

import java.util.ArrayList;
import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.common.ApiController;
import org.k8scmp.login.domain.User;
import org.k8scmp.monitormgmt.domain.alarm.DeploymentInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostEnv;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.k8scmp.monitormgmt.service.alarm.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * Created by baokangwang on 2016/4/13.
 */
@Controller
@RequestMapping("/alarm")
public class TemplateController extends ApiController {

    @Autowired
    TemplateService templateService;
    
    @RequestMapping(value = "")
    public ModelAndView getTemplate() throws Exception {
        return listTemplateInfo();
    }
    
    @ResponseBody
    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public ModelAndView listTemplateInfo() {
    	TemplateInfoBasic basic = new TemplateInfoBasic();
    	List<TemplateInfoBasic> templateList = new ArrayList<TemplateInfoBasic>();
    	basic.setId(3);
    	basic.setTemplateName("tpl1");
    	basic.setCreatorName("jbyb");
    	basic.setTemplateType("host");
    	basic.setCreateTime(System.currentTimeMillis());
    	templateList.add(basic);
        return new ModelAndView("alarm/alarm","templateList",templateList);
    }
    
    @RequestMapping(value = "/templatenew")
    public String createTemplate(Model model) throws Exception {
    	
    	TemplateInfo templateInfo = new TemplateInfo();
    	
    	
    	
//    	List<StrategyInfo> strategyList = new ArrayList<StrategyInfo>();
    	//后台获取host Group
    	List<HostGroupInfoBasic> hostGroupList = new ArrayList<HostGroupInfoBasic>();
    	HostGroupInfoBasic hostGroup = new HostGroupInfoBasic();
    	HostGroupInfoBasic hostGroup1 = new HostGroupInfoBasic();
    	hostGroup.setHostGroupName("aaa");
    	hostGroup.setId(3);
    	hostGroupList.add(hostGroup);
    	hostGroup1.setHostGroupName("ccc");
    	hostGroup1.setId(4);
    	hostGroupList.add(hostGroup1);
    	
    	//后台获取user
    	List<User> userList = new ArrayList<User>();
    	User user = new User();
    	user.setUsername("jbyb");
    	user.setId(6);
    	userList.add(user);
    	
    	//后台获取deployment
    	DeploymentInfo deploymentInfo = new DeploymentInfo();
    	deploymentInfo.setClusterName("centos-k8s");
    	deploymentInfo.setHostEnv(HostEnv.TEST);
    	deploymentInfo.setDeploymentName("qazsw");
    	deploymentInfo.setId(1);
    	
    	templateInfo.setHostGroupList(hostGroupList);
//    	templateInfo.setStrategyList(strategyList);
    	templateInfo.setUserList(userList);
    	templateInfo.setDeploymentInfo(deploymentInfo);
    	model.addAttribute("hostGroupBasic", new HostGroupInfoBasic());
    	model.addAttribute("templateInfo", templateInfo);
    	model.addAttribute("TemplateInfo", new TemplateInfo());
    	
        return "alarm/template-new";
    }
    
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public HttpResponseTemp<?> createTemplate(@ModelAttribute TemplateInfo templateInfo, @ModelAttribute List<HostGroupInfoBasic> hostGroupList) {
    	System.out.println(templateInfo);
    	System.out.println(hostGroupList);
//        return templateService.createTemplate(templateInfo);
    	return null;
    }
    
    @RequestMapping(value = "/edit")
    public String editTemplate(Model model, @RequestParam("templateId") long templateId, @RequestParam("templateType") String templateType) throws Exception {
    	HttpResponseTemp<?> templateInfo = templateService.getTemplateInfo(templateId);
    	
    	model.addAttribute("templateInfo", templateInfo);
    	if(templateType.equals("host")){
    		return "alarm/template-hostEdit";
    	}else{
    		 return "alarm/template-deployEdit";
    	}
       
    }
    
    @ResponseBody
    @RequestMapping(value = "/template", method = RequestMethod.PUT)
    public HttpResponseTemp<?> modifyTemplate(@RequestBody TemplateInfo templateInfo) {
        return templateService.modifyTemplate(templateInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/template/{id}", method = RequestMethod.GET)
    public HttpResponseTemp<?> getTemplateInfo(@PathVariable long id) {
        return templateService.getTemplateInfo(id);
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteTemplate(@PathVariable long id) {
        return templateService.deleteTemplate(id);
    }

}
