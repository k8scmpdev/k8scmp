package org.k8scmp.overview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.json.JSONException;
import org.json.JSONObject;
import org.k8scmp.login.domain.User;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.util.AuthUtil;

/**
 * Created by jason on 2017/8/29.
 */
@RestController
public class OverviewRestController {
	@Autowired
    OperationLog operationLog;
    	
//    @RequestMapping(value="/overview/appinfo", method=RequestMethod.GET)
//    public JSONObject showAppInfo() {
//     	
//     	JSONObject appinfo = new JSONObject();
//     	try {
////     		String json = "{'type':'pie','name':'应用'}";
//     		appinfo.put("type", "pie");
//     		appinfo.put("name", "应用");
//     		Map <String, Integer> data = new HashMap <String, Integer>();
//     		data.put("运行中: 3",   3);
//     		data.put("已停止: 2",   2);
//     		data.put("操作中: 1",   1);
//			appinfo.put("data", data);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//        return appinfo;
//    }
   
   
}
