package org.k8scmp.appmgmt.controller;

import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
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

    @RequestMapping(value = "/create")
    public String createService(@RequestBody ServiceConfigInfo serviceConfigInfo) throws Exception {
    	serviceService.createService(serviceConfigInfo);
        return "app/service-mgmt";
    }

    @RequestMapping(value = "/delete")
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
    public ModelAndView searchServices(ServiceInfo serviceInfo) throws Exception {
        return new ModelAndView("app/service-mgmt","serviceList",serviceService.listServices(serviceInfo));
    }
    
    @RequestMapping(value = "/{appId}")
    public ModelAndView getServicesByAppId(@PathVariable String appId) throws Exception {
        return new ModelAndView("app/service-mgmt","serviceList",serviceService.getServicesByAppId(appId));
    }
}
