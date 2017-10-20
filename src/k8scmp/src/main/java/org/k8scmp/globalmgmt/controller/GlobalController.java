package org.k8scmp.globalmgmt.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.engine.k8s.util.NodeWrapper;
import org.k8scmp.exception.K8sDriverException;
import org.k8scmp.globalmgmt.domain.ClusterInfo;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.globalmgmt.domain.LogicClusterInfo;
import org.k8scmp.globalmgmt.domain.MonitorInfo;
import org.k8scmp.globalmgmt.domain.RegisterInfo;
import org.k8scmp.globalmgmt.service.GlobalService;
import org.k8scmp.login.domain.ChangeUserPassword;
import org.k8scmp.login.domain.LoginType;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.UserPassword;
import org.k8scmp.login.service.UserService;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfo;
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

import io.fabric8.kubernetes.api.model.Node;

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
    	
    	//获取当前登录用户
        User user = AuthUtil.getUser();
    	model.addAttribute("user", user);
    	
    	return "/global/cluster-edit";
    }
    
    
    @RequestMapping(value = "/cluster/logic/list", method = RequestMethod.GET)
    public String showLogicCluster(Model model) {
    	long userId = AuthUtil.getUserId();
        if (userId <= 0) {
            model.addAttribute("info","请先登录");
        }
        
//        HttpResponseTemp<?> resp = globalService.listLogicCluster();
//        List<LogicClusterInfo> logicClusterInfos = (List<LogicClusterInfo>) resp.getResult();
        List<LogicClusterInfo> logicClusterInfos  = new ArrayList<>();
        LogicClusterInfo lci = new LogicClusterInfo();
        lci.setName("基础平台部-逻辑集群1");
        lci.setHostNum(2);
        logicClusterInfos.add(lci);

        model.addAttribute("logicClusterInfos",logicClusterInfos);
        return "/global/cluster-logic-list";
    }
    
    @RequestMapping(value = "/cluster/logic/create", method = RequestMethod.GET)
    public String createLogicCluster(Model model) {
//        HttpResponseTemp<?> resp = globalService.listLogicCluster();
//        List<LogicClusterInfo> logicClusterInfos = (List<LogicClusterInfo>) resp.getResult();
        LogicClusterInfo lci = new LogicClusterInfo();
		try {
			NodeWrapper nodeWrapper = new NodeWrapper().init("default");
			List<NodeInfo> nodeInfoInCluster = nodeWrapper.getNodeInfoListWithoutPods();
			lci.setNodeList(nodeInfoInCluster);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        model.addAttribute("logicClusterDetail",lci);
        return "/global/cluster-logic-new";
    }
    
    @RequestMapping(value = "/cluster/logic/edit", method = RequestMethod.GET)
    public String editLogicCluster(Model model) {
//        HttpResponseTemp<?> resp = globalService.listLogicCluster();
//        List<LogicClusterInfo> logicClusterInfos = (List<LogicClusterInfo>) resp.getResult();
        LogicClusterInfo lci = new LogicClusterInfo();
        lci.setName("基础平台部-逻辑集群1");
        lci.setHostNum(2);
		try {
			NodeWrapper nodeWrapper = new NodeWrapper().init("default");
			List<NodeInfo> nodeInfoInCluster = nodeWrapper.getNodeInfoListWithoutPods();
			lci.setNodeList(nodeInfoInCluster);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        model.addAttribute("logicClusterDetail",lci);
        return "/global/cluster-logic-edit";
    }
    
    @RequestMapping(value="/cluster/edit", method = RequestMethod.POST)
    public String editCluster(Model model,@ModelAttribute ClusterInfo clusterInfo) {
    	HttpResponseTemp<?> resp = globalService.editClusterInfo(clusterInfo);
    	String info = "集群配置信息修改成功";
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info",info);
    		
    	}else{
    		info ="集群仓库配置信息修改失败";
    		state="fail";
    		model.addAttribute("info",info);
    	}
    	operationLog.insertRecord(new OperationRecord(
    			clusterInfo.getName(), 
				ResourceType.CONFIGURATION,
				OperationType.MODIFY, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));
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
    	
    	//获取当前登录用户
        User user = AuthUtil.getUser();
    	model.addAttribute("user", user);
    	
    	return "/global/registry-edit";
    }
    
    @RequestMapping(value="/registry/edit", method = RequestMethod.POST)
    public String editRegistry(Model model,@ModelAttribute RegisterInfo registryinfo) {
    	HttpResponseTemp<?> resp = globalService.editRegistryInfo(registryinfo);
    	String info = "镜像仓库配置信息修改成功";
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info",info);
    		
    	}else{
    		info ="镜像仓库配置信息修改失败";
    		state="fail";
    		model.addAttribute("info",info);
    	}
    	operationLog.insertRecord(new OperationRecord(
    			registryinfo.getName(), 
				ResourceType.CONFIGURATION,
				OperationType.MODIFY, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));
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
    	
    	//获取当前登录用户
        User user = AuthUtil.getUser();
    	model.addAttribute("user", user);
    	
    	return "/global/monitor-edit";
    }
    
    @RequestMapping(value="/monitor/edit", method = RequestMethod.POST)
    public String editMonitor(Model model,@ModelAttribute MonitorInfo monitorinfo) {
    	HttpResponseTemp<?> resp = globalService.editMonitorInfo(monitorinfo);
    	String info = "监控配置信息修改成功";
    	String state = "ok";
    	if(resp.getResultCode()==200){
    		model.addAttribute("info",info);
    		
    	}else{
    		info ="监控配置信息修改失败";
    		state="fail";
    		model.addAttribute("info",info);
    	}
    	operationLog.insertRecord(new OperationRecord(
				"监控配置信息", 
				ResourceType.CONFIGURATION,
				OperationType.MODIFY, 
				AuthUtil.getCurrentLoginName(), 
				AuthUtil.getUserName(), 
				state, 
				info, 
				DateUtil.dateFormatToMillis(new Date())
		));
    	return showMonitor(model);
    }
}
