package org.k8scmp.overview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.json.JSONException;
import org.json.JSONObject;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResourceType;
import org.k8scmp.login.domain.User;
import org.k8scmp.login.domain.UserPassword;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.operation.OperationType;
import org.k8scmp.overview.domain.OverviewCountInfo;
import org.k8scmp.overview.domain.ResourceOverview;
import org.k8scmp.overview.service.OverviewService;
import org.k8scmp.util.AuthUtil;

/**
 * Created by jason on 2017/8/29.
 */
@Controller
public class OverviewController {
	@Autowired
    OperationLog operationLog;
	@Autowired
    OverviewService overviewService;
    	
    @RequestMapping(value="/overview", method=RequestMethod.GET)
    public String overview(Model model) {
    	//获取当前登录用户
        User user = AuthUtil.getUser();
     	model.addAttribute("user", user);
     	
     	//获取操作日志
     	List<OperationRecord> operecords = operationLog.listOperationRecord4Overview();
     	//获取告警信息
     	
     	
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
    
    @RequestMapping(value="/opeloglist", method=RequestMethod.GET)
    public String showAllLog(Model model) {
     	//获取操作日志
     	List<OperationRecord> operecords = operationLog.listAllOperationRecord4Overview();
     	model.addAttribute("operecords", operecords);
     	model.addAttribute("ResourceType", ResourceType.values());
     	model.addAttribute("OperationType", OperationType.values());
     	String[] infos = {"","","",""};
     	infos[0].equalsIgnoreCase("");
     	model.addAttribute("infos", infos);
        return "monitor/opelog-list";
    }
    
    @RequestMapping(value = "/opeloglist/getbykw", method = RequestMethod.POST)
    public String getOpelogList(Model model,@RequestParam List<String> infos) {
    	String keyword = infos.get(0);
    	String rtype = infos.get(1);
    	String otype = infos.get(2);
    	String status = infos.get(3);
    	List<OperationRecord> operecords = operationLog.listAllOperationRecordByKey("%"+keyword+"%",rtype,otype,status);
    	model.addAttribute("operecords", operecords);
     	model.addAttribute("ResourceType", ResourceType.values());
     	model.addAttribute("OperationType", OperationType.values());
     	model.addAttribute("infos", infos);
     	return "monitor/opelog-list";
    }
    
    
    @ResponseBody
    @RequestMapping(value="/overview/appinfo", method=RequestMethod.GET)
    public String showAppInfo() {
     	
    	ObjectMapper obj = new ObjectMapper();

    	Map<String, Object> appinfo = new TreeMap<String, Object>();
     	appinfo.put("type", "pie");
     	appinfo.put("name", "应用");

     	//获取app数据
     	Map<String, Integer> appinfolist = overviewService.getAppInfo();
		appinfo.put("datalist", appinfolist);

        try {
			return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

    }
    
    @ResponseBody
    @RequestMapping(value="/overview/serviceinfo", method=RequestMethod.GET)
    public String showServiceInfo() {
     	
    	ObjectMapper obj = new ObjectMapper();

    	Map<String, Object> appinfo = new TreeMap<String, Object>();
     	appinfo.put("type", "pie");
     	appinfo.put("name", "服务");
		appinfo.put("datalist", overviewService.getServiceInfo());

        try {
			return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
    @ResponseBody
    @RequestMapping(value="/overview/meminfo", method=RequestMethod.GET)
    public String showMem() {
     	
    	ObjectMapper obj = new ObjectMapper();

    	Map<String, Object> appinfo = new HashMap<>();
     	appinfo.put("type", "pie");
     	appinfo.put("name", "内存使用量");
		appinfo.put("datalist", overviewService.getMemoryInfo());
		
        try {
			return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

    }
    
    @ResponseBody
    @RequestMapping(value="/overview/cpuinfo", method=RequestMethod.GET)
    public String showCpu() {
     	
    	ObjectMapper obj = new ObjectMapper();

    	Map<String, Object> appinfo = new HashMap<>();
     	appinfo.put("type", "pie");
     	appinfo.put("name", "CPU使用量");
		appinfo.put("datalist", overviewService.getCPUInfo());
		
        try {
			return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

    }
    
    @ResponseBody
    @RequestMapping(value="/overview/nodeinfo", method=RequestMethod.GET)
    public String showNode() {
     	
    	ObjectMapper obj = new ObjectMapper();

    	Map<String, Object> appinfo = new HashMap<>();
     	appinfo.put("type", "pie");
     	appinfo.put("name", "主机使用量");
		appinfo.put("datalist", overviewService.getNodeInfo());
		
        try {
			return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

    }
    
    @ResponseBody
    @RequestMapping(value="/overview/diskinfo", method=RequestMethod.GET)
    public String showDisk() {
     	
    	ObjectMapper obj = new ObjectMapper();

    	Map<String, Object> appinfo = new HashMap<>();
     	appinfo.put("type", "pie");
     	appinfo.put("name", "磁盘使用量");
		appinfo.put("datalist", overviewService.getDiskInfo());
		
        try {
			return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

    }
   
}
