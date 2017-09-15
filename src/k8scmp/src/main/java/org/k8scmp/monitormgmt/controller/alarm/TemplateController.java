package org.k8scmp.monitormgmt.controller.alarm;

import org.k8scmp.basemodel.HttpResponseTemp;
import org.k8scmp.common.ApiController;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfo;
import org.k8scmp.monitormgmt.service.alarm.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by baokangwang on 2016/4/13.
 */
@Controller
@RequestMapping("/alarm")
public class TemplateController extends ApiController {

    @Autowired
    TemplateService templateService;
    
    @RequestMapping(value = "")
    public String getTemplate() throws Exception {
        return "alarm/alarm";
    }
    
    @ResponseBody
    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public HttpResponseTemp<?> listTemplateInfo() {
        return templateService.listTemplateInfo();
    }
    
    @RequestMapping(value = "/templatenew")
    public String createTemplate() throws Exception {
        return "alarm/template-new";
    }
    
    @ResponseBody
    @RequestMapping(value = "/template", method = RequestMethod.POST)
    public HttpResponseTemp<?> createTemplate(@RequestBody TemplateInfo templateInfo) {
        return templateService.createTemplate(templateInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/template", method = RequestMethod.PUT)
    public HttpResponseTemp<?> modifyTemplate(@RequestBody TemplateInfo templateInfo) {
        return templateService.modifyTemplate(templateInfo);
    }

    @ResponseBody
    @RequestMapping(value = "/template/{id}", method = RequestMethod.GET)
    public HttpResponseTemp<?> getTemplateInfo(@PathVariable int id) {
        return templateService.getTemplateInfo(id);
    }

    @ResponseBody
    @RequestMapping(value = "/template/{id}", method = RequestMethod.DELETE)
    public HttpResponseTemp<?> deleteTemplate(@PathVariable long id) {
        return templateService.deleteTemplate(id);
    }

}
