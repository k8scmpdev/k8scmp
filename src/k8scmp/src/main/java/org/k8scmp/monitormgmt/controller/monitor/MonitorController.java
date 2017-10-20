package org.k8scmp.monitormgmt.controller.monitor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.k8scmp.monitormgmt.domain.monitor.InstenceInfoBack;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfoBack;
import org.k8scmp.monitormgmt.domain.monitor.falcon.GraphHistoryResponse;
import org.k8scmp.monitormgmt.service.monitor.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/monitor")
public class MonitorController {
	
	@Autowired
    MonitorService monitorService;
	
	
	@RequestMapping(value = "")
    public String getMonitor() throws Exception {
        return "monitor/monitor";
    }
	
	@RequestMapping(value = "/hostlist",method = RequestMethod.GET)
    public String getMonitorHostlist(Model model) throws Exception {
		List<NodeInfoBack> nodeInfoList = monitorService.getNodeMonitorData(null);
		model.addAttribute("nodeInfoList", nodeInfoList);
		model.addAttribute("total", nodeInfoList.size());
        return "monitor/monitor-hostlist";
    }
	
	@ResponseBody
	@RequestMapping(value = "/hostsearch", method = RequestMethod.POST)
    public HttpResponseTemp<?> getMonitorHostsearch(@RequestParam("hostName") String hostName) throws Exception {
		List<NodeInfoBack> nodeInfoList = monitorService.getNodeMonitorData(hostName);
		return ResultStat.OK.wrap(nodeInfoList);
    }
	
	@RequestMapping(value = "/instencelist",method = RequestMethod.GET)
    public String getMonitorInstencelist(Model model) throws Exception {
		List<InstenceInfoBack> instenceList = monitorService.getInstenceMonitorData(null);
		model.addAttribute("instenceList", instenceList);
		model.addAttribute("total", instenceList.size());
        return "monitor/monitor-instencelist";
    }
	
	@ResponseBody
	@RequestMapping(value = "/instenceSearch", method = RequestMethod.POST)
    public HttpResponseTemp<?> getMonitorInstenceSearch(@RequestParam("serviceName") String serviceName) throws Exception {
		List<InstenceInfoBack> instenceList = monitorService.getInstenceMonitorData(serviceName);
        return ResultStat.OK.wrap(instenceList);
    }
	
	@RequestMapping(value = "/host", method = RequestMethod.GET)
    public String getMonitorHost(Model model) throws Exception {
		//默认监控当前时间1小时内的值  单个主机的指标展示
		String type = "node";
		Calendar current = Calendar.getInstance();
        long endTime = current.getTimeInMillis();
        current.set(Calendar.MINUTE, current.get(Calendar.MINUTE) - 60*1000);
        long startTime = current.getTimeInMillis();
		String dataSpec = "AVERAGE";
		model.addAttribute("monitorDetailData", monitorService.getMonitorDetailData(type,startTime,endTime,dataSpec));
		
        return "monitor/monitor-host";
    }
	
	@ResponseBody
    @RequestMapping(value="/host/getDetailData", method=RequestMethod.GET)
    public String getDetailData() {
     	
    	ObjectMapper obj = new ObjectMapper();

		//默认监控当前时间1小时内的值  单个主机的指标展示
		String type = "node";
		Calendar current = Calendar.getInstance();
		long endTime = current.getTimeInMillis();
		current.set(Calendar.MILLISECOND, current.get(Calendar.MILLISECOND) - 30*60*1000);
		long startTime = current.getTimeInMillis();
		String dataSpec = "AVERAGE";
		Map<String,Map<Long,Double>> monitorDetailDataList = monitorService.getMonitorDetailData(type,startTime,endTime,dataSpec);
		
		Map<String, Object> appinfo = new HashMap<>();
		appinfo.put("datalist", monitorDetailDataList);
		
        try {
        	return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

    }
	
	@ResponseBody
    @RequestMapping(value="/host/getDetailData1raw", method=RequestMethod.GET)
    public String getDetailData1row() {
     	
    	ObjectMapper obj = new ObjectMapper();

		//默认监控当前时间半小时内的值  单个主机的指标展示
		String type = "node";
		Calendar current = Calendar.getInstance();
		long endTime = current.getTimeInMillis();
		current.set(Calendar.MILLISECOND, current.get(Calendar.MILLISECOND) - 30*1000);
		long startTime = current.getTimeInMillis();
		String dataSpec = "AVERAGE";
		Map<String,Map<Long,Double>> monitorDetailDataList = monitorService.getMonitorDetailData(type,startTime,endTime,dataSpec);
		
		Map<String, Object> appinfo = new HashMap<>();
		appinfo.put("datalist", monitorDetailDataList);
		
        try {
        	return obj.writeValueAsString(appinfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

    }
	
	@RequestMapping(value = "/instence", method = RequestMethod.GET)
    public String getMonitorInstence(Model model) throws Exception {
		//默认监控当前时间1小时内的值 单个实例的指标展示
		String type = "pod";
		Calendar current = Calendar.getInstance();
        long endTime = current.getTimeInMillis();
        current.set(Calendar.MINUTE, current.get(Calendar.MINUTE) - 60*10*1000);
        long startTime = current.getTimeInMillis();
		String dataSpec = "AVERAGE";
		model.addAttribute("instenceData", getMonitorData(type,startTime,endTime,dataSpec));
				
        return "monitor/monitor-instence";
    }
	
	@ResponseBody
    @RequestMapping(value = "/monitor/data", method = RequestMethod.GET)
    public HttpResponseTemp<?> getMonitorData(@RequestParam(value = "type", required = true) String type,
                                              @RequestParam(value = "start", required = true) long start,
                                              @RequestParam(value = "end", required = true) long end,
                                              @RequestParam(value = "dataSpec", defaultValue = "AVERAGE") String dataSpec) {
        return monitorService.getMonitorData(type, start, end, dataSpec);
    }
	
}
