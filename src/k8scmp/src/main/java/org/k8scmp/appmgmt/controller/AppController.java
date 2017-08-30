package org.k8scmp.appmgmt.controller;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("app")
public class AppController {
    @Autowired
    AppService appService;

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createApp(@RequestBody AppInfo appInfo) throws Exception {
    	appService.createApp(appInfo);
        return "/app-mgmt";
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteApp(@PathVariable String id) throws Exception {
        appService.deleteApp(id);
        return "/app-mgmt";
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public String modifyApp(@RequestBody AppInfo appInfo) throws Exception {
        appService.modifyApp(appInfo);
        return "/app-mgmt";
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView listApps() throws Exception {
    	return searchApps(null);
//        return new ModelAndView("/appList","appList",appService.listApps(app));
    }

    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView searchApps(AppInfo appInfo) throws Exception {
        return new ModelAndView("/app-mgmt","appList",appService.listApps(appInfo));
    }
}
