package org.k8scmp.monitormgmt.controller.monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/monitor")
public class MonitorController {
	
	@RequestMapping(value = "")
    public String getMonitor() throws Exception {
        return "monitor/monitor";
    }
	
	@RequestMapping(value = "/host",method = RequestMethod.GET)
    public String getMonitorHost() throws Exception {
		
        return "monitor/monitor-host";
    }
	
	@RequestMapping(value = "/instence")
    public ModelAndView getMonitorInstence() throws Exception {
        return new ModelAndView("monitor/monitor-instence","nodeList", null);
    }
	
}
