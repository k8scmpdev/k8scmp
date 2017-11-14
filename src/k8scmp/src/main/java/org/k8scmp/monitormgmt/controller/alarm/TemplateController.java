package org.k8scmp.monitormgmt.controller.alarm;

import java.util.ArrayList;
import java.util.List;

import org.k8scmp.appmgmt.domain.Instance;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.common.ApiController;
import org.k8scmp.engine.k8s.util.NodeWrapper;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.service.UserService;
import org.k8scmp.monitormgmt.domain.alarm.DeploymentInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateBack;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupDetail;
import org.k8scmp.monitormgmt.domain.alarm.UserGroupInfo;
import org.k8scmp.monitormgmt.service.alarm.HostGroupService;
import org.k8scmp.monitormgmt.service.alarm.TemplateService;
import org.k8scmp.monitormgmt.service.alarm.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    @Autowired
    HostGroupService hostGropService;
    @Autowired
    UserGroupService userGroupService;
    @Autowired
    GlobalBiz globalBiz;
    
    @RequestMapping(value = "")
    public String getTemplate() throws Exception {
        return "alarm/alarm";
    }
    
    @ResponseBody
    @RequestMapping(value = "/templatelist", method = RequestMethod.GET)
    public ModelAndView listTemplateInfo() {
    	List<TemplateInfoBasic> templateList = templateService.listTemplateInfo();
        return new ModelAndView("alarm/alarm-template", "templateList", templateList);
    }
    
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public HttpResponseTemp<?> listTemplateInfo(Model model,@RequestParam("templateName") String templateName) {
        return templateService.searchTemplateInfo("%"+templateName+"%");
    }
    
    @RequestMapping(value = "/templatenew")
    public String createTemplate(Model model) throws Exception {
    	TemplateBack templateBack = new TemplateBack();
    	//获取hostGroup
    	List<HostGroupInfo> hostGroupInfos = hostGropService.listHostGroupInfo();
    	List<HostGroupInfoBasic> hostGroupInfoList = new ArrayList<HostGroupInfoBasic>();
    	for (HostGroupInfo hostGroupInfo : hostGroupInfos) {
    		HostGroupInfoBasic hostGroupInfoBasic = new HostGroupInfoBasic();
    		hostGroupInfoBasic.setId(hostGroupInfo.getId());
    		hostGroupInfoBasic.setHostGroupName(hostGroupInfo.getHostGroupName());
    		hostGroupInfoList.add(hostGroupInfoBasic);
		}
    	//获取user
    	List<UserGroupDetail> userList = (List<UserGroupDetail>)userGroupService.listUserGroupInfo().getResult();
    	List<UserGroupInfo> userGroupList = new ArrayList<>();
    	for (UserGroupDetail userGroupDetail : userList) {
    		UserGroupInfo userGroupInfo = new UserGroupInfo();
    		userGroupInfo.setId(userGroupDetail.getId());
    		userGroupInfo.setUserGroupName(userGroupDetail.getUserGroupName());
    		userGroupList.add(userGroupInfo);
		}
    	//获取deployment
    	NodeWrapper nodeWrapper = new NodeWrapper().init("default");
    	List<DeploymentInfo> deploymentList = new ArrayList<>();
    	for(Instance instance:nodeWrapper.getInstance()){
    		DeploymentInfo deploymentInfo = new DeploymentInfo();
    		deploymentInfo.setDeploymentName(instance.getInstanceName());
    		deploymentInfo.setId(instance.getServiceId());
    		deploymentList.add(deploymentInfo);
    	}
    	//callback url
    	String callback = globalBiz.getGlobalInfoByType(GlobalType.MONITOR_API_MAIL).getValue();
    	templateBack.setCallback(callback);
    	templateBack.setHostGroupList(hostGroupInfoList);
    	templateBack.setUserGroupList(userGroupList);
    	templateBack.setDeploymentInfos(deploymentList);
    	model.addAttribute("templateBack", templateBack);
        return "alarm/template-new";
    }
    
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public HttpResponseTemp<?> createTemplate(@RequestBody TemplateInfo templateInfo) {
    	List<StrategyInfo> strategyList = templateInfo.getStrategyList();
    	for (StrategyInfo strategyInfo : strategyList) {
    		if(strategyInfo.getOperator().equals("le")){
    			strategyInfo.setOperator("<=");
    		}else if(strategyInfo.getOperator().equals("ge")){
    			strategyInfo.setOperator(">=");
    		}else{
    			strategyInfo.setOperator("==");
    		}
		}
    	templateInfo.setStrategyList(strategyList);
        return templateService.createTemplate(templateInfo);
    }
    
    @RequestMapping(value = "/edit", method=RequestMethod.GET)
    public String editTemplate(Model model, @RequestParam(value="templateId") int templateId, @RequestParam("templateType") String templateType) {
    	TemplateInfo templateInfo = (TemplateInfo)templateService.getTemplateInfo(templateId).getResult();
    	List<HostGroupInfo> hostGroupInfoList = hostGropService.listHostGroupInfo();
    	List<String> hostGroupSelList = new ArrayList<>();
//    	List<User> userList = userService.listAllUserInfo().getResult();
    	List<UserGroupDetail> userList = (List<UserGroupDetail>)userGroupService.listUserGroupInfo().getResult();
    	List<UserGroupInfo> userGroupList = new ArrayList<>();
    	for (UserGroupDetail userGroupDetail : userList) {
    		UserGroupInfo userGroupInfo = new UserGroupInfo();
    		userGroupInfo.setId(userGroupDetail.getId());
    		userGroupInfo.setUserGroupName(userGroupDetail.getUserGroupName());
    		userGroupList.add(userGroupInfo);
		}
    	List<String> userSelectList = new ArrayList<>();
    	if(templateInfo.getHostGroupList() != null){
    		for(HostGroupInfoBasic hostGroupInfo:templateInfo.getHostGroupList()){
        		hostGroupSelList.add(hostGroupInfo.getHostGroupName());
        	}
    	}
    	if(templateInfo.getUserGroupList() != null){
    		for(UserGroupInfo userGroup:templateInfo.getUserGroupList()){
        		userSelectList.add(userGroup.getUserGroupName());
        	}
    	}
    	model.addAttribute("hostGroupSelList", hostGroupSelList);
    	model.addAttribute("hostGroupInfoList", hostGroupInfoList);
    	model.addAttribute("userSelectList", userSelectList);
    	model.addAttribute("userGroupList", userGroupList);
    	model.addAttribute("templateInfo", templateInfo);
    	if(templateType.equals("host")){
    		return "alarm/template-hostEdit";
    	}else{
    		 return "alarm/template-deployEdit";
    	}
    }
    
    @ResponseBody
    @RequestMapping(value = "/getTemp", method=RequestMethod.GET)
    public HttpResponseTemp<?> getTemplate(@RequestParam(value="templateId") int templateId){
    	TemplateInfo templateInfo =(TemplateInfo) templateService.getTemplateInfo(templateId).getResult();
    	List<StrategyInfo> strategyList = templateInfo.getStrategyList();
    	for (StrategyInfo strategyInfo : strategyList) {
    		if(strategyInfo.getOperator().equals("<=")){
    			strategyInfo.setOperator("le");
    		}else if(strategyInfo.getOperator().equals(">=")){
    			strategyInfo.setOperator("ge");
    		}else{
    			strategyInfo.setOperator("eq");
    		}
		}
    	templateInfo.setStrategyList(strategyList);
    	return ResultStat.OK.wrap(templateInfo);
    }
    
    @ResponseBody
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public HttpResponseTemp<?> modifyTemplate(@RequestBody TemplateInfo templateInfo) {
    	List<StrategyInfo> strategyList = templateInfo.getStrategyList();
    	for (StrategyInfo strategyInfo : strategyList) {
    		if(strategyInfo.getOperator().equals("le")){
    			strategyInfo.setOperator("<=");
    		}else if(strategyInfo.getOperator().equals("ge")){
    			strategyInfo.setOperator(">=");
    		}else{
    			strategyInfo.setOperator("==");
    		}
		}
    	templateInfo.setStrategyList(strategyList);
        return templateService.modifyTemplate(templateInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/template/{id}", method = RequestMethod.GET)
    public HttpResponseTemp<?> getTemplateInfo(@PathVariable int id) {
        return templateService.getTemplateInfo(id);
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteTemplate(@PathVariable int id) {
        return templateService.deleteTemplate(id);
    }
}
