/*$(document).ready(function(){
	//set href
	$("#addService").attr("th:href","@{/app/service/service-new(appCode=${#httpServletRequest.getParameter('appCode')})}");
});*/

/*$("#example-select-all").on("click",function(){
	var rows = table.rows({'search':'applied'}).nodes();
	$('input[type="checkbox"]', rows).prop('checked', this.checked); 
});

$(".scroll-table tbody").on("change","input[type='checkbox']",function(){
	if(!this.checked){
		var el=$("#example-select-all").get(0);
	}
	if(el && el.chekced && ("inderterminate" in el)){
		el.inderterminate=true;
	}
});
*/

//delete multi services
function deleteService(){
	
	
}

//start service submit
$("#startServiceSubmit").bind("click",function(event){
	var startFlag = false;
	var selectedRow = getSelectedRow();
	var serviceId = selectedRow[0]["id"];
	var version = $("#selectStartVersionNumber").val();
	var replicas = $("#wishStartInstanceNumber").val();
	var AjaxURL = "/app/service/start?serviceId="+serviceId+"&version="+version+"&replicas="+replicas;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			//success
			if(data.resultCode == 200){
				startFlag = true;
			}else{
				alert("启动服务异常！");
			}
		},
		error: function(data) {
			alert("启动服务异常！");
		}
	});
	if(startFlag){
		refreshServices();
		alert("启动服务成功！");
	}
	
});

//hide start service
$("#cancleStartService").bind("click",function(event){
	$("#startServiceDisplay").modal("hide");
});


//stop service
function stopService(){
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length !=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	var AjaxURL = "/app/service/stopService?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			//success
			if(data.resultCode == 200){
				
			}else{
				alert("停止服务异常！");
			}
		},
		error: function(data) {
			alert("停止服务异常！");
		}
	});
}

//start service check
function startService(){
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length !=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	//clear start instance number
	$("#wishStartInstanceNumber").val("");
	
	//init start version,get version names
	var versionList = null;
	AjaxURL = "/app/version/getVersionNames?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					versionList = eval(data.result);
					var selectSecond = $("#selectStartVersionNumber");
					selectSecond.empty();
					for(var i=0;i<versionList.length;i++){
						$("#selectStartVersionNumber").append("<option value='"+versionList[i].version+"' selected='selected'>"+versionList[i].versionName+"</option>");
					}
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
}

//upgrade rollback
function startUpdateRollback(){
	//get current version number
	var currentVersionNum = "";
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length !=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	var AjaxURL = "/app/service/getCurrentVersionNum?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					currentVersionNum = data.result.join(",");
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	//get version names
	var versionList = null;
	AjaxURL = "/app/version/getVersionNames?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					versionList = eval(data.result);
					var selectSecond = $("#selectVersionNumber");
					selectSecond.empty();
					for(var i=0;i<versionList.length;i++){
						var opt= new Option();
						opt.value=versionList[i].version;
						opt.text = versionList[i].versionName;
						selectSecond.options.add(opt);
					}
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	//set init value
	$("#currentVersionNumber").html(currentVersionNum);
}

//show service modify page
function showServiceModify(serviceId,description,serviceCode){
	$("#serviceName").html(serviceCode==null?"":serviceCode);
	$("#serviceDescription").val(description==null?"":description);
	$("#hiddenSingleServiceId").val(serviceId);
}

//modify service submit 
$("#serviceModifySubmit").bind("click",function(event){
	var serviceId = $("#hiddenSingleServiceId").val();
	var description = $("#serviceDescription").val();
	var AjaxURL = "/app/service/modify";
	var paramData = {};
	paramData["id"] = serviceId;
	paramData["description"] = description;
	$.ajax({
		type: "POST",
		dataType: "html",
		url: AjaxURL,
		data:JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			refreshServices();
		},
		error: function(data) {
			alert("error!");
		}
	});
});

//cancle service modify
$("#cancleModifyService").bind("click",function(event){
	$("#modifyServiceModal").modal("hide");
});

//show scale up or down
function scaleUpDown(){
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length!=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	var AjaxURL = "/app/service/getReplicasByServiceId?serviceId="+serviceId;
	var instanceNumber = 0;
	var currentVersionNum = 0;
	
	//get current instance count
	$.ajax({
		type: "GET",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				instanceNumber = data.result;
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	AjaxURL = "/app/service/getCurrentVersionNum?serviceId="+serviceId;
	//get current version num
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					currentVersionNum = data.result.join(",");
				}
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	//set current version number
	$("#currentVersionNum").html(currentVersionNum==null?"":currentVersionNum);
	
	//set current instance number
	$("#instanceNumber").html(instanceNumber);
}

//sacle up or down submit
$("#scaleSubmit").bind("click",function(event){
	var selectedRow = getSelectedRow();
	var serviceId = selectedRow[0]["id"];
	var currentNumber = $("#instanceNumber").html();
	var futureNumber = $("#futureNumber").val();
	var currentVersionNumer = $("#currentVersionNum").html();
	var ajaxUrl = "";
	var paramData = {"serviceId":serviceId,"replicas":futureNumber};
	if(futureNumber == currentNumber){
		alert("期望实例个数与当前实例个数相等，无法进行扩容缩容！");
	}
	
	//up
	if(futureNumber>currentNumber){
		ajaxUrl = "/app/service/scaleUp?serviceId="+serviceId+"&version="+currentVersionNumer+"&replicas="+futureNumber;
	//down
	}else{
		ajaxUrl = "/app/service/scaleDown?serviceId="+serviceId+"&version="+currentVersionNumer+"&replicas="+futureNumber;
	}
	$.ajax({
		type: "GET",
		dataType: "json",
		url: ajaxUrl,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				alert("操作成功！");
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
});

//roll back or upgrade submit
$("#rollbackSubmit").bind("click",function(event){
	var selectedRow = getSelectedRow();
	var serviceId = selectedRow[0]["id"];
	var currentVersionNumber = $("#currentVersionNumber").html();
	var selectVersionNumber = $("#selectVersionNumber").val();
	var wishReplies = $("#wishInstanceNumber").val();
	var ajaxUrl = "";
	
	if(selectVersionNumber == currentVersionNumber){
		alert("期望运行版本与当前版本相同，无法进行升级回滚！");
		return;
	}
	
	//upgrade
	if(selectVersionNumber>currentVersionNumber){
		ajaxUrl = "/app/service/startUpdate?serviceId="+serviceId+"&version="+selectVersionNumber+"&replicas="+wishReplies;
	}else{
		ajaxUrl = "/app/service/startRollback?serviceId="+serviceId+"&version="+selectVersionNumber+"&replicas="+wishReplies;
	}
	
	$.ajax({
		type: "GET",
		dataType: "json",
		url: ajaxUrl,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				alert("操作成功！");
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
});

//get all checked rows
function getSelectedRow(){
	var returnObj = [];
	var tmpObj = {};
	$(".scroll-table span[class='checked'] input[type='checkbox']").each(function(){
		tmpObj = $(this).parent().parent().parent().parent().parent().parent().parent();
		var tr = {};
		var serviceIdtd = tmpObj.children("td").eq(6);
		tr["id"] = serviceIdtd.attr("name");
		returnObj.push(tr);
	});
	return returnObj;
}


//delete single service by serviceid
function deleteSingleService(serviceId,obj){
	var AjaxURL= "/app/service/delete/"+serviceId;
	$.ajax({
		type: "GET",
		dataType: "html",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			var tr=obj.parentNode.parentNode;
			var tbody=tr.parentNode;
			tbody.removeChild(tr);
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//refresh service mgmt
function refreshServices(){
	var appId = $("#appId").val();
	var AjaxURL= "/app/service/"+appId;
	window.location.href = AjaxURL;
} 
