package org.k8scmp.monitormgmt.controller.monitor;

import java.util.List;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.monitormgmt.domain.monitor.InstenceInfoBack;
import org.k8scmp.monitormgmt.domain.monitor.NodeInfoBack;
import org.k8scmp.monitormgmt.service.monitor.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
		List<NodeInfoBack> nodeInfoList = monitorService.getNodeMonitorData();
		model.addAttribute("nodeInfoList", nodeInfoList);
		model.addAttribute("total", nodeInfoList.size());
        return "monitor/monitor-hostlist";
		
    }
	
	@RequestMapping(value = "/instencelist",method = RequestMethod.GET)
    public String getMonitorInstencelist(Model model) throws Exception {
		List<InstenceInfoBack> instenceList = monitorService.getInstenceMonitorData(null);
		model.addAttribute("instenceList", instenceList);
		model.addAttribute("total", instenceList.size());
        return "monitor/monitor-instencelist";
    }
	
	@RequestMapping(value = "/host",method = RequestMethod.GET)
    public String getMonitorHost(Model model) throws Exception {
		//默认监控当前时间1小时内的值  单个主机的指标展示
		String type = "node";
		long end = System.currentTimeMillis();
		long start = end - 1000*60*60;
		String dataSpec = "average";
		model.addAttribute("nodeData", getMonitorData(type,start,end,dataSpec));
		
        return "monitor/monitor-host";
    }
	
	@RequestMapping(value = "/instence")
    public String getMonitorInstence(Model model) throws Exception {
		//默认监控当前时间1小时内的值 单个实例的指标展示
		String type = "pod";
		long end = System.currentTimeMillis();
		long start = end - 1000*60*60;
		String dataSpec = "average";
		model.addAttribute("instenceData", getMonitorData(type,start,end,dataSpec));
				
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
