package org.k8scmp.appmgmt.controller;

import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("app/service")
public class ServiceController {
    @Autowired
    ServiceService serviceService;
    
    @RequestMapping(value = "/service-new")
    public String showCreateService() throws Exception {
        return "app/service-new";
    }
    
    @RequestMapping(value = "/service-mgmt")
    public String showService() throws Exception {
        return "app/service-mgmt";
    }

    @RequestMapping(value = "/service-info")
    public String showServiceInfo() throws Exception {
        return "app/service-info";
    }
    
    @RequestMapping(value = "/create")
    public String createService(@RequestBody ServiceDetail serviceDetail) throws Exception {
    	serviceService.createService(serviceDetail);
        return "app/service-mgmt";
    }

    @RequestMapping(value = "/delete/{id}")
    public String deleteService(@PathVariable String id) throws Exception {
        serviceService.deleteService(id);
        return "app/service-mgmt";
    }

    @RequestMapping(value = "/modify")
    public String modifyService(@RequestBody ServiceInfo serviceInfo) throws Exception {
        serviceService.modifyService(serviceInfo);
        return "app/service-mgmt";
    }

    @RequestMapping(value = "")
    public ModelAndView listServices() throws Exception {
    	return searchServices(null);
    }

    @RequestMapping(value = "/search")
    public ModelAndView searchServices(@RequestBody ServiceInfo serviceInfo) throws Exception {
        return new ModelAndView("app/service-mgmt","serviceList",serviceService.listServices(serviceInfo));
    }
    
    @RequestMapping(value = "/{appId}")
    public ModelAndView getServicesByAppId(@PathVariable String appId) throws Exception {
        return new ModelAndView("app/service-mgmt","serviceList",serviceService.getServicesByAppId(appId));
    }
    
    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String startService(@RequestParam(value = "serviceId", required = true) String serviceId,
    						   @RequestParam(value = "version", required = true) int version,
    						   @RequestParam(value = "replicas", required = true) int replicas) throws Exception {
    	String msg = serviceService.startService(serviceId,version,replicas);
    	return msg;
    }
}
