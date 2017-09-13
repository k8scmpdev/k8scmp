package org.k8scmp.globalmgmt.controller;

import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.globalmgmt.domain.ClusterInfo;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.globalmgmt.domain.MonitorInfo;
import org.k8scmp.globalmgmt.domain.RegisterInfo;
import org.k8scmp.globalmgmt.service.GlobalService;
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
 * Created by jason on 2017/9/9.
 */
@Controller
public class GlobalController {
    protected static Logger logger = LoggerFactory.getLogger(GlobalController.class);
    @Autowired
    GlobalService globalService;
    @Autowired
    OperationLog operationLog;
    
    

    @RequestMapping(value = "/cluster/edit", method = RequestMethod.GET)
    public String showCluster(Model model) {
    	GlobalInfo clustername =  globalService.getGlobalInfoByType(GlobalType.CI_CLUSTER_NAME);
    	GlobalInfo clusterapi =  globalService.getGlobalInfoByType(GlobalType.CI_CLUSTER_HOST);
    	GlobalInfo clusterdesc =  globalService.getGlobalInfoByType(GlobalType.CI_CLUSTER_DESC);
    	
    	String clname = clustername==null?"":clustername.getValue();
    	String clapi = clusterapi==null?"":clusterapi.getValue();
    	String cldesc = clusterdesc==null?"":clusterdesc.getValue();
    	
    	ClusterInfo clinfo = new ClusterInfo(); 
    	clinfo.setName(clname);
    	clinfo.setApiserver(clapi);
    	clinfo.setDescription(cldesc);
    	
    	model.addAttribute("clusterinfo", clinfo);
    	
    	String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
    	User user = new User();
    	user.setUsername(currentUser);
    	model.addAttribute("user", user);
    	
    	return "/global/cluster-edit";
    }
    
    
    @RequestMapping(value="/cluster/edit", method = RequestMethod.POST)
    public String editCluster(Model model,@ModelAttribute ClusterInfo clusterInfo) {
    	HttpResponseTemp<?> resp = globalService.editClusterInfo(clusterInfo);
    	if(resp.getResultCode()==200){
    		model.addAttribute("info","修改成功");
    	}else{
    		model.addAttribute("info","修改失败");
    	}
    	return showCluster(model);
    }

    
    @RequestMapping(value = "/registry/edit", method = RequestMethod.GET)
    public String showRegistry(Model model) {
    	GlobalInfo regname =  globalService.getGlobalInfoByType(GlobalType.REGISTRY_NAME);
    	GlobalInfo regurl =  globalService.getGlobalInfoByType(GlobalType.REGISTRY_URL);
    	GlobalInfo regdesc =  globalService.getGlobalInfoByType(GlobalType.REGISTRY_DESCRIPTION);
    	
    	String rname = regname==null?"":regname.getValue();
    	String rurl = regurl==null?"":regurl.getValue();
    	String rdesc = regdesc==null?"":regdesc.getValue();
    	
    	RegisterInfo rinfo = new RegisterInfo(); 
    	rinfo.setName(rname);
    	rinfo.setUrl(rurl);
    	rinfo.setDescription(rdesc);
    	
    	model.addAttribute("reginfo", rinfo);
    	
    	String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
    	User user = new User();
    	user.setUsername(currentUser);
    	model.addAttribute("user", user);
    	
    	return "/global/registry-edit";
    }
    
    @RequestMapping(value="/registry/edit", method = RequestMethod.POST)
    public String editRegistry(Model model,@ModelAttribute RegisterInfo registryinfo) {
    	HttpResponseTemp<?> resp = globalService.editRegistryInfo(registryinfo);
    	if(resp.getResultCode()==200){
    		model.addAttribute("info","修改成功");
    	}else{
    		model.addAttribute("info","修改失败");
    	}
    	return showRegistry(model);
    }
    
    
    @RequestMapping(value = "/monitor/edit", method = RequestMethod.GET)
    public String showMonitor(Model model) {
    	GlobalInfo montran =  globalService.getGlobalInfoByType(GlobalType.MONITOR_TRANSFER);
    	GlobalInfo mongra =  globalService.getGlobalInfoByType(GlobalType.MONITOR_GRAPH);
    	GlobalInfo monque =  globalService.getGlobalInfoByType(GlobalType.MONITOR_QUERY);
    	GlobalInfo monhbs =  globalService.getGlobalInfoByType(GlobalType.MONITOR_HBS);
    	GlobalInfo monjud =  globalService.getGlobalInfoByType(GlobalType.MONITOR_JUDGE);
    	GlobalInfo monala =  globalService.getGlobalInfoByType(GlobalType.MONITOR_ALARM);
    	GlobalInfo monsen =  globalService.getGlobalInfoByType(GlobalType.MONITOR_SENDER);
    	GlobalInfo monred =  globalService.getGlobalInfoByType(GlobalType.MONITOR_REDIS);
    	
    	String mtran = montran==null?"":montran.getValue();
    	String mgra = mongra==null?"":mongra.getValue();
    	String mque = monque==null?"":monque.getValue();
    	String mhbs = monhbs==null?"":monhbs.getValue();
    	String mjud = monjud==null?"":monjud.getValue();
    	String mala = monala==null?"":monala.getValue();
    	String msen = monsen==null?"":monsen.getValue();
    	String mred = monred==null?"":monred.getValue();
    	
    	MonitorInfo minfo = new MonitorInfo(); 
    	minfo.setTransfer(mtran);
    	minfo.setGraphy(mgra);
    	minfo.setQuery(mque);
    	minfo.setHbs(mhbs);
    	minfo.setJudge(mjud);
    	minfo.setAlarm(mala);
    	minfo.setSender(msen);
    	minfo.setRedis(mred);
    	
    	model.addAttribute("moninfo", minfo);
    	
    	String currentUser = (String) SecurityUtils.getSubject().getPrincipal();
    	User user = new User();
    	user.setUsername(currentUser);
    	model.addAttribute("user", user);
    	
    	return "/global/monitor-edit";
    }
    
    @RequestMapping(value="/monitor/edit", method = RequestMethod.POST)
    public String editMonitor(Model model,@ModelAttribute MonitorInfo monitorinfo) {
    	HttpResponseTemp<?> resp = globalService.editMonitorInfo(monitorinfo);
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info","修改成功");
    		
    	}else{
    		model.addAttribute("info","修改失败");
    		state = "fail";
    	}
    	operationLog.insertRecord(new OperationRecord(
				monitorinfo.getTransfer(), 
				ResourceType.CONFIGURATION,
				OperationType.MODIFY, 
				"", 
				"", 
				state, 
				"", 
				DateUtil.dateFormatToMillis(new Date())
		));
    	return showMonitor(model);
    }
}
