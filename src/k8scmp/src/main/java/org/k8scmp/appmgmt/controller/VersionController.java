package org.k8scmp.appmgmt.controller;

import java.util.List;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.service.VersionService;
import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.basemodel.ResultStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("app/version")
public class VersionController {
    @Autowired
    VersionService versionService;
    
    @ResponseBody
    @RequestMapping(value = "/createVersion", method = RequestMethod.POST)
    public HttpResponseTemp<Object> createVersion(@RequestParam(value = "serviceId", required = true) String serviceId,
    						   @RequestBody Version version) throws Exception {
    	versionService.createVersion(serviceId, version);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/listVersion")
    public HttpResponseTemp<List<Version>> listVersionByServiceId(@RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
    	return ResultStat.OK.wrap(versionService.listVersion(serviceId));
    }
    
    @ResponseBody
    @RequestMapping(value = "/getVersion")
    public HttpResponseTemp<Version> getVersion(@RequestParam(value = "serviceId", required = true) String serviceId,
    											@RequestParam(value = "version", required = true) int version) throws Exception {
    	return ResultStat.OK.wrap(versionService.getVersion(serviceId, version));
    }

    @ResponseBody
    @RequestMapping(value = "/deprecateVersion", method = RequestMethod.POST)
    public HttpResponseTemp<Object> deprecateVersion(@RequestParam(value = "serviceId", required = true) String serviceId,
															@RequestParam(value = "version", required = true) int version) throws Exception {
    	versionService.deprecateVersion(serviceId, version);
    	return ResultStat.OK.wrap(null);
    }
    
    @ResponseBody
    @RequestMapping(value = "/enableVersion", method = RequestMethod.POST)
    public HttpResponseTemp<Object> enableVersion(@RequestParam(value = "serviceId", required = true) String serviceId,
    		@RequestParam(value = "version", required = true) int version) throws Exception {
    	versionService.enableVersion(serviceId, version);
    	return ResultStat.OK.wrap(null);
    }
    
}
