package org.k8scmp.appmgmt.controller;

import java.util.List;

import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.appmgmt.domain.Instance;
import org.k8scmp.appmgmt.domain.NodePortDraft;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceDetail;
import org.k8scmp.appmgmt.domain.ServiceInfo;
import org.k8scmp.appmgmt.domain.VersionString;
import org.k8scmp.appmgmt.service.ServiceService;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
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
    public HttpResponseTemp<Object> startService(@RequestParam(value = "serviceId", required = true) String serviceId,
    						   @RequestParam(value = "version", required = true) int version,
    						   @RequestParam(value = "replicas", required = true) int replicas) throws Exception {
    	serviceService.startService(serviceId,version,replicas);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/startUpdate", method = RequestMethod.POST)
    public HttpResponseTemp<Object> startUpdate(@RequestParam(value = "serviceId", required = true) String serviceId,
    						   @RequestParam(value = "version", required = true) int version,
    						   @RequestParam(value = "replicas", required = true) int replicas) throws Exception {
    	serviceService.startUpdate(serviceId, version, replicas);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/startRollback", method = RequestMethod.POST)
    public HttpResponseTemp<Object> startRollback(@RequestParam(value = "serviceId", required = true) String serviceId,
    		@RequestParam(value = "version", required = true) int version,
    		@RequestParam(value = "replicas", required = true) int replicas) throws Exception {
    	serviceService.startRollback(serviceId, version, replicas);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/scaleUp", method = RequestMethod.POST)
    public HttpResponseTemp<Object> scaleUp(@RequestParam(value = "serviceId", required = true) String serviceId,
    		@RequestParam(value = "version", required = true) int version,
    		@RequestParam(value = "replicas", required = true) int replicas) throws Exception {
    	serviceService.scaleUpDeployment(serviceId, version, replicas);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/scaleDown", method = RequestMethod.POST)
    public HttpResponseTemp<Object> scaleDown(@RequestParam(value = "serviceId", required = true) String serviceId,
    		@RequestParam(value = "version", required = true) int version,
    		@RequestParam(value = "replicas", required = true) int replicas) throws Exception {
    	serviceService.scaleDownDeployment(serviceId, version, replicas);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/stopService", method = RequestMethod.POST)
    public HttpResponseTemp<Object> stopService(@RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
    	serviceService.stopService(serviceId);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/getYamlStr")
    public HttpResponseTemp<VersionString> getYamlStr(@RequestBody ServiceConfigInfo serviceConfigInfo) throws Exception {
    	return ResultStat.OK.wrap(serviceService.getYamlStr(serviceConfigInfo));
    }
    
    @ResponseBody
    @RequestMapping(value = "/listDeployEvent")
    public HttpResponseTemp<List<DeployEvent>> listDeployEvent(@RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
    	return ResultStat.OK.wrap(serviceService.listDeployEvent(serviceId));
    }
    
    @ResponseBody
    @RequestMapping(value = "/listPodsByServiceId")
    public HttpResponseTemp<List<Instance>> listPodsByServiceId(@RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
    	return ResultStat.OK.wrap(serviceService.listPodsByServiceId(serviceId));
    }
    
    @ResponseBody
    @RequestMapping(value = "/getServiceURLs")
    public HttpResponseTemp<List<String>> getServiceURLs(@RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
    	return ResultStat.OK.wrap(serviceService.getServiceURLs(serviceId));
    }
    
    @RequestMapping(value = "/createLoadBalancer")
    public HttpResponseTemp<Object> createLoadBalancer(@RequestParam(value = "serviceId", required = true) String serviceId,
    							@RequestBody List<NodePortDraft> nodePorts) throws Exception {
    	serviceService.createLoadBalancer(serviceId, nodePorts);
        return ResultStat.OK.wrap(null);
    }
    
    
}
