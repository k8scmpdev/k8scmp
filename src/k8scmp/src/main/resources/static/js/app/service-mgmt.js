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
				alert("启动服务成功！");
			}else{
				alert("启动服务异常！");
			}
		},
		error: function(data) {
			alert("启动服务异常！");
		}
	});
	
	$("#startServiceDisplay").modal("hide");
	if(startFlag){
		refreshServices();
	}
});

//hide start service
$("button[class*='cancleStartService']").bind("click",function(event){
	$("#startServiceDisplay").modal("hide");
});

//hide scale service
$("button[class*='cancleScaleService']").bind("click",function(event){
	$("#scaleUpDown").modal("hide");
});

//hide rollback service
$("button[class*='cancleRollbackService']").bind("click",function(event){
	$("#updateRollback").modal("hide");
});

//stop service
function stopService(){
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length !=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	var serviceState = selectedRow[0]["state"]
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
	var flag = true;
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length !=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	var serviceState = selectedRow[0]["state"];
	if(!checkServiceState(serviceState,"startService")){
		alert("不能启动服务，请检查服务状态！");
		return;
	}
	//clear wish start instance number
	$("#wishStartInstanceNumber").val("");
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
			}else{
				alert("获取版本信息出错！");
				flag = false;
			}
		},
		error: function(data) {
			alert("error!");
			flag = false;
		}
	});
	
	if(!flag){
		return;
	}
	//show modal until all data init
	$("#startServiceDisplay").modal();
}

//upgrade rollback
function startUpdateRollback(){
	//get current version number
	var flag = true;
	var currentVersionNum = "";
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length !=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	var serviceState = selectedRow[0]["state"];
	if(!checkServiceState(serviceState,"upRollBack")){
		alert("不能升级或回滚服务，请检查服务状态！");
		return;
	}
	$("#rollBackInsNumber").val();
	var AjaxURL = "/app/service/getCurrentVersionNum?serviceId="+serviceId;
	//get current version num,syn
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		async:false,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					currentVersionNum = data.result.join(",");
					$("#rollCurrentVerNum").val(currentVersionNum);
				}else{
					alert('获取当前版本信息出错！');
					flag = false;
				}
			}else{
				alert('获取当前版本信息出错！');
				flag = false;
			}
		},
		error: function(data) {
			alert("error!");
			flag = false;
		}
	});
	if(!flag){
		return;
	}
	
	//get version names,syn
	var versionList = null;
	AjaxURL = "/app/version/getVersionNames?serviceId="+serviceId;
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		async:false,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					versionList = eval(data.result);
					var selectSecond = $("#rollSelectVerNum");
					selectSecond.empty();
					for(var i=0;i<versionList.length;i++){
						$("#rollSelectVerNum").append("<option value='"+versionList[i].version+"' selected='selected'>"+versionList[i].versionName+"</option>");
					}
				}
			}else{
				alert("获取版本信息出错！");
				flag = false;
			}
		},
		error: function(data) {
			alert("error!");
			flag = false;
		}
	});
	if(!flag){
		return;
	}
	//show modal until all data init
	$("#updateRollback").modal();
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
	var flag = true;
	var selectedRow = getSelectedRow();
	if(selectedRow == null || selectedRow.length!=1){
		alert("请选择一条记录！");
		return;
	}
	var serviceId = selectedRow[0]["id"];
	var serviceState = selectedRow[0]["state"];
	if(!checkServiceState(serviceState,"scaleUpDown")){
		alert("不能扩容或缩容服务，请检查服务状态！");
		return;
	}
	$("#scaleaWishInstanceNum").val();
	var AjaxURL = "/app/service/getReplicasByServiceId?serviceId="+serviceId;
	
	//get current replicas,syn
	$.ajax({
		type: "GET",
		dataType: "json",
		url: AjaxURL,
		async:false,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				$("#scaleCurrInstanceNum").val(data.result==null?"":data.result);
			}
		},
		error: function(data) {
			alert("error!");
			flag = false;
		}
	});
	
	if(!flag){
		return;
	}
	
	AjaxURL = "/app/service/getCurrentVersionNum?serviceId="+serviceId;
	//get current version num,syn
	$.ajax({
		type: "POST",
		dataType: "json",
		url: AjaxURL,
		async:false,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				if(data.result != null && data.result.length>0){
					$("#scaleCurrVersionNum").val(data.result.join(",")==null?"":data.result.join(","));
				}
			}else{
				alert("获取当前版本信息出错!");
				flag = false;
			}
		},
		error: function(data) {
			alert("获取当前版本信息出错!");
			flag = false;
		}
	});
	
	if(!flag){
		return;
	}
	
	//show modal until all data init
	$("#scaleUpDown").modal();
}

//sacle up or down submit
$("#scaleSubmit").bind("click",function(event){
	var scaleFlag = false;
	var selectedRow = getSelectedRow();
	var serviceId = selectedRow[0]["id"];
	var currentNumber = $("#scaleCurrInstanceNum").val();
	var futureNumber = $("#scaleaWishInstanceNum").val();
	var currentVersionNumer = $("#scaleCurrVersionNum").val();
	var ajaxUrl = "";
	var paramData = {"serviceId":serviceId,"replicas":futureNumber};
	if(futureNumber == currentNumber){
		alert("期望实例个数与当前实例个数相等，无法进行扩容缩容！");
		return;
	}
	
	//up
	if(futureNumber>currentNumber){
		ajaxUrl = "/app/service/scaleUp?serviceId="+serviceId+"&version="+currentVersionNumer+"&replicas="+futureNumber;
	//down
	}else{
		ajaxUrl = "/app/service/scaleDown?serviceId="+serviceId+"&version="+currentVersionNumer+"&replicas="+futureNumber;
	}
	$.ajax({
		type: "POST",
		dataType: "json",
		url: ajaxUrl,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				scaleFlag = true;
				alert("操作成功！");
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	$("#scaleUpDown").modal("hide");
	if(scaleFlag){
		refreshServices();
	}
});

//roll back or upgrade submit
$("#rollbackSubmit").bind("click",function(event){
	var rollFlag = false;
	var selectedRow = getSelectedRow();
	var serviceId = selectedRow[0]["id"];
	var currentVersionNumber = $("#rollCurrentVerNum").val();
	var selectVersionNumber = $("#rollSelectVerNum").val();
	var wishReplies = $("#rollBackInsNumber").val();
	var ajaxUrl = "";
	
	if(selectVersionNumber == currentVersionNumber){
		alert("期望运行版本与当前版本相同，无法进行升级回滚！");
		return;
	}
	
	//upgrade
	//ajaxUrl = "/app/service/startUpdate?serviceId="+serviceId+"&version="+selectVersionNumber+"&replicas="+wishReplies;
	if(selectVersionNumber>currentVersionNumber){
		ajaxUrl = "/app/service/startUpdate?serviceId="+serviceId+"&version="+selectVersionNumber+"&replicas="+wishReplies;
	}else{
		ajaxUrl = "/app/service/startRollback?serviceId="+serviceId+"&version="+selectVersionNumber+"&replicas="+wishReplies;
	}
	
	$.ajax({
		type: "POST",
		dataType: "json",
		url: ajaxUrl,
		contentType:"application/json",
		success: function (data) {
			if(data.resultCode == 200){
				rollFlag = true;
				alert("操作成功！");
			}else{
				alert("error!");
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	$("#updateRollback").modal("hide");
	if(rollFlag){
		refreshServices();
	}
});

//get all checked rows
function getSelectedRow(){
	var returnObj = [];
	var tmpObj = {};
	$(".scroll-table span[class='checked'] input[type='checkbox']").each(function(){
		tmpObj = $(this).parent().parent().parent().parent().parent().parent().parent();
		var tr = {};
		var serviceIdtd = tmpObj.children("td").eq(6);
		var stateTd = tmpObj.children("td").eq(3);
		tr["id"] = serviceIdtd.attr("name");
		tr["state"] = stateTd.html();
		returnObj.push(tr);
	});
	return returnObj;
}

//check service state
function checkServiceState(state,operation){
	var flag = false;
	switch(operation){
	case "startService": flag=(state=="STOP");break;
	case "stopService":
	case "scaleUpDown":
	case "upRollBack" : flag=(state=="RUNNING");break;	
	default:(flag=false);break;
	}
	return flag;
}


//delete single service by serviceid
function deleteSingleService(serviceId,obj){
	var AjaxURL= "/app/service/delete/"+serviceId;
	$.ajax({
		type: "POST",
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
	var hostLabel = $("#serviceHostLabel").html();
	var namespace = $("#serviceNamespace").html();
	var AjaxURL= "/app/service/"+appId+"?appId="+appId+"&hostLabel="+serviceHostLabel+"&namespace="+namespace;
	window.location.href = AjaxURL;
} 
