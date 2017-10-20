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

//stop service
function stopService(){
	
}

//start service
function startService(){
	
}

//upgrade rollback
function startUpdateRollback(){
	
}

//sacle up or down
function scaleUpDown(){
	var selectedRow = getSelectedRow();
	var serviceId = selectedRow[0]["id"];
	var AjaxURL = "/app/service/getReplicasByServiceId?serviceId="+serviceId;
	var instanceNumber = 0;
	$.ajax({
		type: "GET",
		dataType: "json",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			alert("success");
			if(data.resultCode == 200){
				instanceNumber = data.result;
			}
		},
		error: function(data) {
			alert("error!");
		}
	});
	
	//set current instance number
	$("#instanceNumber").val();
	
}

//sacle up or down submit
$("#scaleSubmit").bind("click",function(event){
	var currentNumber = $("#instanceNumber").val();
	var futureNumber = $("#futureNumber").val();
	var ajaxUrl = "";
	var paramData = {"serviceId":serviceId,"replicas":futureNumber};
	if(futureNumber == currentNumber){
		alert("期望实例个数与当前实例个数相等，无法进行扩容缩容！");
	}
	//up
	if(futureNumber>currentNumber){
		ajaxUrl = "/app/service/scaleUp";
	//down
	}else{
		ajaxUrl = "/app/service/scaleDown";
	}
	$.ajax({
		type: "GET",
		dataType: "html",
		url: ajaxUrl,
		data:JSON.stringify(paramData),
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
			//refreshServices();
			var tr=obj.parentNode.parentNode;
			var tbody=tr.parentNode;
			tbody.removeChild(tr);
		},
		error: function(data) {
			alert("error!");
		}
	});
}


function refreshServices(){
	var appId = $("#appId").val();
	alert(appId);
	var AjaxURL= "/app/service/"+appId;
	$.ajax({
		type: "GET",
		dataType: "html",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			
		},
		error: function(data) {
			alert("error!");
		}
	});
} 
