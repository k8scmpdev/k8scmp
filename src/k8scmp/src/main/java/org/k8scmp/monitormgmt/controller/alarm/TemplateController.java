package org.k8scmp.monitormgmt.controller.alarm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.common.ApiController;
import org.k8scmp.engine.k8s.util.NodeWrapper;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.service.UserService;
import org.k8scmp.monitormgmt.domain.alarm.DeploymentInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostEnv;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfo;
import org.k8scmp.monitormgmt.domain.alarm.HostGroupInfoBasic;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateBack;
import org.k8scmp.monitormgmt.domain.alarm.TemplateIn;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;
import org.k8scmp.monitormgmt.domain.monitor.PodInfo;
import org.k8scmp.monitormgmt.service.alarm.HostGroupService;
import org.k8scmp.monitormgmt.service.alarm.TemplateService;
import org.k8scmp.util.AuthUtil;
import org.k8scmp.util.DateUtil;
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

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;


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
    UserService userService;
    
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
    	List<User> userList = userService.listAllUserInfo().getResult();
    	//获取deployment
    	NodeWrapper nodeWrapper = new NodeWrapper().init("default");
    	PodList podList = nodeWrapper.getAllPods();
    	List<Pod> items = podList.getItems();
    	List<DeploymentInfo> deploymentList = new ArrayList<>();
    	for (Pod pod : items) {
    		DeploymentInfo deploymentInfo = new DeploymentInfo();
    		deploymentInfo.setDeploymentName(pod.getMetadata().getName());
    		deploymentList.add(deploymentInfo);
		}
    	templateBack.setHostGroupList(hostGroupInfoList);
    	templateBack.setUserList(userList);
    	templateBack.setDeploymentInfos(deploymentList);
    	model.addAttribute("templateBack", templateBack);
    	model.addAttribute("TemplateBack", new TemplateInfo());
        return "alarm/template-new";
    }
    
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public HttpResponseTemp<?> createTemplate(@RequestBody TemplateIn templateIn) {
    	TemplateInfo templateInfo = new TemplateInfo();
    	templateInfo.setTemplateName(templateIn.getTemplateName());
    	templateInfo.setTemplateType(templateIn.getTemplateType());
    	templateInfo.setCreateTime(DateUtil.dateFormat(new Date()));
    	templateInfo.setCreatorId(AuthUtil.getUserId());
    	templateInfo.setCreatorName(AuthUtil.getCurrentLoginName());
    	templateInfo.setDeploymentInfo(templateIn.getDeploymentInfo());
    	templateInfo.setHostGroupList(templateIn.getHostGroupList());
    	templateInfo.setUserList(templateIn.getUserList());
    	templateInfo.setCallback(templateIn.getCallback());
    	templateInfo.setUpdateTime(DateUtil.dateFormat(new Date()));
    	List<StrategyInfo> strategyInfolist = new ArrayList<StrategyInfo>();
    	for (int i=0;i<templateIn.getMetricList().length;i++) {
    		StrategyInfo strategyInfo = new StrategyInfo();
    		strategyInfo.setAggregateType(templateIn.getAggregateTypeList()[i]);
    		strategyInfo.setMaxStep(templateIn.getMaxStepList()[i]);
    		strategyInfo.setMetric(templateIn.getMetricList()[i]);
    		strategyInfo.setNote(templateIn.getNoteValueList()[i]);
    		strategyInfo.setOperator(templateIn.getOperatorList()[i]);
    		strategyInfo.setPointNum(templateIn.getPointNumList()[i]);
    		strategyInfo.setRightValue(templateIn.getRightValueList()[i]);
    		strategyInfolist.add(strategyInfo);
		}
    	templateInfo.setStrategyList(strategyInfolist);
        return templateService.createTemplate(templateInfo);
    }
    
    @RequestMapping(value = "/edit", method=RequestMethod.GET)
    public String editTemplate(Model model, @RequestParam("templateId") int templateId, @RequestParam("templateType") String templateType) {
    	//int id = Integer.parseInt(templateId);
    	TemplateInfo templateInfo = (TemplateInfo)templateService.getTemplateInfo(templateId).getResult();
    	model.addAttribute("templateInfo", templateInfo);
    	if(templateType.equals("host")){
    		return "alarm/template-hostEdit";
    	}else{
    		 return "alarm/template-deployEdit";
    	}
       
    }
    
    @ResponseBody
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public HttpResponseTemp<?> modifyTemplate(@RequestBody TemplateInfo templateInfo) {
        return templateService.modifyTemplate(templateInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/template/{id}", method = RequestMethod.GET)
    public HttpResponseTemp<?> getTemplateInfo(@PathVariable String id) {
    	int ID = Integer.parseInt(id);
        return templateService.getTemplateInfo(ID);
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteTemplate(@PathVariable String id) {
    	int ID = Integer.parseInt(id);
        return templateService.deleteTemplate(ID);
    }
}
