package org.k8scmp.appmgmt.controller;

import org.k8scmp.appmgmt.domain.AppInfo;
import org.k8scmp.appmgmt.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by KaiRen on 2016/9/23.
 */
@Controller
@RequestMapping("/api/app")
public class AppController {
    @Autowired
    AppService appService;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public HttpResponseTemp<?> createApp(@RequestBody AppInfo appInfo) throws Exception {
        return ResultStat.OK.wrap(appService.createApp(appInfo));
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteApp(@PathVariable int id) throws IOException {
        return appService.deleteApp(id);
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HttpResponseTemp<?> modifyApp(@PathVariable int id,
                                                   @RequestBody AppInfo appInfo) throws Exception {
        return appService.modifyApp(id, appInfo);
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpResponseTemp<?> listApp() throws Exception {
        return appService.listApps();
    }

}
