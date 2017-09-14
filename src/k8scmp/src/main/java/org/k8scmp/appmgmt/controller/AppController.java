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

    @RequestMapping(value = "/create")
    public String createApp(@RequestBody AppInfo appInfo) throws Exception {
    	appService.createApp(appInfo);
        return "app/app-mgmt";
    }

    @RequestMapping(value = "/delete/{id}")
    public String deleteApp(@PathVariable String id) throws Exception {
        appService.deleteApp(id);
        return "app/app-mgmt";
    }

    @RequestMapping(value = "/modify")
    public String modifyApp(@RequestBody AppInfo appInfo) throws Exception {
        appService.modifyApp(appInfo);
        return "app/app-mgmt";
    }

    @RequestMapping(value = "")
    public ModelAndView listApps() throws Exception {
    	return searchApps(null);
    }

    @RequestMapping(value = "/search")
    public ModelAndView searchApps(@RequestBody AppInfo appInfo) throws Exception {
        return new ModelAndView("app/app-mgmt","appList",appService.listApps(appInfo));
    }
}
