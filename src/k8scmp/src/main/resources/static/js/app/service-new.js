//table
var imageTable;
//global container volume draft
var volumeDrafts = [];
var volumeDraftsMap = {};

var containerDrafts = [];
var currentContainerMap = {};

//bind dynamic hide and display
//next
$("#btnnew1next").bind("click", function(event) {
	var versionType=$('input:radio[name="cfradios"]:checked').val();
	if(versionType == "CUSTOM"){
		$("#service-new1").css("display","none");
		$("#service-new2").css("display","block");
		$("#service-new4").css("display","none"); 
	}else if(versionType == "YAML"){
		$("#service-new1").css("display","none");
		$("#service-new2").css("display","none");
		$("#service-new4").css("display","block"); 
	}
});
$("#btnnew2next").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","none");
	$("#service-new4").css("display","block"); 
});
//pre
$("#btnnew2pre").bind("click", function(event) {
	$("#service-new1").css("display","block"); 
	$("#service-new2").css("display","none"); 
	$("#service-new4").css("display","none"); 
});
$("#btnnew4pre").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","block");
	$("#service-new4").css("display","none"); 
});
$("*.cancleService").bind("click", function(event) {
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","none");
	$("#service-new4").css("display","none"); 
	var appId = $("#appId").val();
	refreshService(appId);
});

//refresh service mgmt
function refreshService(appId){
	var AjaxURL = "/app/service/"+appId+"?appId="+appId;
	window.location.href=AjaxURL;
}

//go to mgmt when click service mgmt
function goToMgmt(){
	var appId = $("#appId").val();
	refreshService(appId);
}

$("#btnnew2next").bind("click",function(event){
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","block");
	$("#service-new4").css("display","none");
});




//set width onload
$(document).ready(function(){
	imageTable=$('.image-table').dataTable({
		"scrollY": "100px",
	    "scrollCollapse": false,
	    "paging": false,
		"sorting":false,
		"searching":false,
		"lengthChange":false,
		"info":false
		/*"language":	{
	        "zeroRecords": "没有找到记录",
	        "infoEmpty": "无记录",
	    },*/
	    /*"columnDefs": [
	      {
	        "targets": [ 4 ],
	        "visible": false,
	        "searchable": false
	      },
	      {
	        "targets": [ 5 ],
	        "visible": false
	      }
	    ],*/
	    /*"fnDrawCallback":function(oSettings){
	    	$(".dataTables_length").offset({top:$(".dataTables_info").offset().top});
	    }*/
	});
	//set table width
	$(".dataTables_scrollHeadInner").width("100%");
	$(".nosearch-table").width("100%");
	$(".image-table").width("100%");
	
	//set imageList width
	$(".i10").css("width","30%");
	$(".i10").css("min-width","30%");
	$(".i10").css("max-width","30%");
	
	$(".i20").css("width","30%");
	$(".i20").css("min-width","30%");
	$(".i20").css("max-width","30%");
	
	$(".i30").css("width","10%");
	$(".i30").css("min-width","10%");
	$(".i30").css("max-width","10%");
	
	$(".i40").css("width","30%");
	$(".i40").css("min-width","30%");
	$(".i40").css("max-width","30%");
	
	//init service data
});

//create service
/*$('#createServiceForm').submit(function(){
	var AjaxURL= "/app/service/create";
	var paramData = getServiceFormJson();
	var appId = $("#appId").val();
	$.ajax({
		type: "POST",
		dataType: "html",
		url: AjaxURL,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			refreshService(appId);
		},
		error: function(data) {
			alert("error!");
		}
	});
});*/
function createService(){
	var AjaxURL= "/app/service/create";
	var paramData = getServiceFormJson();
	var appId = $("#appId").val();
	$.ajax({
		type: "POST",
		dataType: "html",
		url: AjaxURL,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			refreshService(appId);
		},
		error: function(data) {
			alert("error!");
		}
	});
}

//get service form value
function getServiceFormJson(){
	var paramData = {};//param data
	var serviceConfigInfo = {};//serviceConfigInfo key object
	var versions = [];//versions key object
	
	//get serviceConfigInfo item value
	var appId = $("#appId").val();
	var serviceCode = $("#serviceName").val();
	var startSeq = $("#startSeq").val();
	var serviceDescription = $("#serviceDescription").val();
	var defaultReplicas = $("#defaultReplicas").val();
	var versionType=$('input:radio[name="cfradios"]:checked').val();
	//set serviceConfigInfo
	serviceConfigInfo["serviceCode"] = serviceCode;
	serviceConfigInfo["appId"] = appId;
	serviceConfigInfo["startSeq"] = startSeq;
	serviceConfigInfo["description"] = serviceDescription;
	serviceConfigInfo["defaultReplicas"] = defaultReplicas;
	
	//custom versions
	if(versionType=="CUSTOM"){
		var versionItem = getVersion(currentContainerMap);
		versionItem["versionType"] = versionType;
		versions[0] = versionItem;
	//YAML versions
	}else if(val=="YAML"){
		var podSpecStr = $("#yamlSpec").val();
		var versionItem = {};
		versionItem["podSpecStr"] = podSpecStr;
		versionItem["versionType"] = versionType;
		versions[0] = versionItem;
	}
	paramData["serviceConfigInfo"] = serviceConfigInfo;
	paramData["versions"] = versions;
	return paramData;
}

//make up volumeDrafts in a service
function getVolumeDrafts(){
	var returnValue = [];
	var iteratorMap = volumeDraftsMap;
	if(iteratorMap != null){
		for(var key in iteratorMap){
			var tmpMap = iteratorMap[key];
			if(tmpMap != null){
				for(var singleKey in tmpMap){
					returnValue.concat(tmpMap[singleKey]);
				}
			}
			returnValue.concat(iteratorMap[key]);
		}
	}
	return returnValue;
}

//image
/*var chooseImagesList = [];
chooseImagesList.push({"id":"test-java","version":"6.0"});
chooseImagesList.push({"id":"dev-mysql","version":"5.0"});
chooseImagesList.push({"id":"nginx","version":"test1"});
chooseImagesList.push({"id":"tomcat","version":"bate"});*/

//save choose image window value into a image map

//show correct single image configuration window
/**
 * imageId made up of image name+image registry+image versions
 * volumeList is single image VolumeMountDraft
 * @param imageId
 * @param volumeList
 * @returns
 */

//get jquery datatable all rows checked
function getTableContent(){  
	/*var selectedTableRows = [];
	var nTrs = imageTable.tables().nodes();//fnGetNodes get all rows，nTrs[i]表示第i行tr对象  
	alert("begin");
	for(var i = 0; i < nTrs.length; i++){  
    	alert("enter");
    	if($(nTrs[i][0]).hasClass('selected')){  
    		alert("select");
    		selectedTableRows.push(table.fnGetData(nTrs[i]));//fnGetData获取一行的数据  
		}  
	} 
    return selectedTableRows;*/
} 

//get all checked rows
function getSelectedRow(){
	var returnObj = [];
	var tmpObj = {};
	$(".image-table span[class='checked'] input[type='checkbox']").each(function(){
		tmpObj = $(this).parent().parent().parent().parent().parent().parent();
		var tr = {};
		tr["id"] = tmpObj.children("td[id='id']").html();
		tr["registry"] = tmpObj.children("td[id='registry']").html()==null?"":tmpObj.children("td[id='registry']").html();
		tr["versions"] = tmpObj.children("td[id='versions']").children("select[name='versions']").val();
		returnObj.push(tr);
	});
	return returnObj;
}

//get volumetype by volumename
function getDirType(name,localVolumeDrafts){
	var returnDir = null;
	if(localVolumeDrafts != null){
		for(var i=0;i<localVolumeDrafts.length;i++){
			var tmpObj = localVolumeDrafts[i];
			if(tmpObj["name"] == name){
				returnDir = tmpObj["volumeType"];
				break;
			}
		}
	}
	return returnDir;
}

function iteratorVolumeDrafts(voolumeDrafts,name){
	var returnList = [];
	if(voolumeDrafts !=null && voolumeDrafts.size()>0){
		for(var i=0;i<voolumeDrafts.size();i++){
			var tmp = voolumeDrafts[i];
			if(name == tmp["name"]){
				returnList = returnList[i]
			}
		}
		
	}
	return returnList;
}

//save choose image window value into a image map
$("#imagesbtn").bind("click",function(event){
	loadsUpgradeDatas(getSelectedRow())
	$("#myModal").modal("hide");
});

function loadsUpgradeDatas(containers){
	var htmlContent = "";
	if($("#tBody1").children("tr").children("td[class='dataTables_empty']").length){
		//empty tables
		$("#tBody1").html("");
	}
	
	htmlContent = $("#tBody1").html();
	if(containers!=null){
		for(var i=0;i<containers.length;i++){
			var container = containers[i];
			var image = container["image"]==null?"":container["image"];
			var registry = container["registry"]==null?"":container["registry"];
			var tag = container["tag"]==null?"":container["tag"];
			var id = registry+image;
			//add new row to the images list table
			var trRow = "<tr>" +
					"<td class='i10' style='width: 30%;min-width:30%;max-width:30%;text-align: center;'>"+image+"</td>" +
					"<td class='i20' style='width: 30%;min-width:30%;max-width:30%;text-align: center;'>"+registry+"</td>" +
					"<td class='i30' style='width: 10%;min-width:10%;max-width:10%;text-align: center;'>"+tag+"</td>" +
					"<td class='i40' style='width: 30%;min-width:30%;max-width:30%;text-align: center;'><a title='配置' class='tip-bottom iconOperation' data-target='#service-new3' data-toggle='modal' onclick='showImageConfiguration(\""+id+"\");'><i class='icon-edit'></i><span>配置</span></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
					"<a title='删除' class='tip-bottom iconOperation' href='javascript:void(0);' onclick='deleteImage(this);'><i class='icon-trash'></i><span>删除</span></a></td>" +
					"</tr>";
			htmlContent = htmlContent.concat(trRow);
			currentContainerMap[id]= container;
		}
	}
	$("#tBody1").html(htmlContent);
}

//get all checked rows
function getSelectedRow(){
	var returnObj = [];
	var tmpObj = {};
	var valid = true;
	$(".image-table span[class='checked'] input[type='checkbox']").each(function(){
		tmpObj = $(this).parent().parent().parent().parent().parent().parent();
		var tr = {};
		tr["image"] = tmpObj.children("td[id='id']").html()==null?"":tmpObj.children("td[id='id']").html();
		tr["registry"] = tmpObj.children("td[id='registry']").html()==null?"":tmpObj.children("td[id='registry']").html();
		tr["tag"] = tmpObj.children("td[id='versions']").children("select[name='versions']").val();
		
		if(currentContainerMap[tr["registry"]+tr["image"]]!=null){
			alert("镜像["+tr["image"]+"]已存在!");
			valid = false;
			return false;
		}
		returnObj.push(tr);
	});
	if(!valid){
		return null;
	}
	return returnObj;
}

//remove image tr from image list table
function deleteImage(obj){
	var tr=obj.parentNode.parentNode;
	var tbody=tr.parentNode;
	tbody.removeChild(tr);
}

function showImageConfiguration(id){
	var container = currentContainerMap[id];
	$("#storages").html("");//clear all sub divs
	
	/************************show storages*******************/
	//haved setted part containerdraft
	$("#uniqueImage").val(id);
	$("#registryConfig").val(container["registry"]);
	$("#imageConfig").val(container["image"]);
	$("#tagConfig").val(container["tag"]);
	
	if(container != null){
		volumeMountDrafts = container["volumeMountDrafts"];
		//曾经打开过该页面设过值
		if(volumeMountDrafts != null){
			loadStorages(volumeMountDrafts,volumeMountDrafts.length);
		}
		/*************show commands***********/
		$("#startCommands").val(container["commands"]==null?"":container["commands"].join(","));
		
		/*************show envs***********/
		$("#envvar").val(container["envs"]==null?"":getEnvsToString(container["envs"]));
		
		/*************show cpu***********/
		$("#cpus").val(container["cpu"]==null?0:container["cpu"]);

		/*************show mem***********/
		$("#mems").val(container["mem"]==null?0:container["mem"]);
	}
	
}

function loadStorages(volumeMountDrafts,seq){
	if(seq>0){
		var childdiv=$('<div></div>'); 
		$("#storages").append(childdiv);
		childdiv.load("/js/statichtml/app/storageTemplate.html  #storageTemplate",
				function(responseTxt,statusTxt,xhr){
			tmpDraft = volumeMountDrafts[seq-1];
			//set value
			if(tmpDraft["volumeType"] == "EMPTYDIR"){
				$(this).find("select[name='dirs']").find("option[value='EMPTYDIR']").attr("selected","selected");
				$(this).find("select[name='writeOrRead']").find("option[value='"+tmpDraft["readOnly"]+"']").attr("selected","selected");
				$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
				$(this).find("input[name='insPath']").val(tmpDraft["mountPath"]);
				
				//hide hostpath and show emptydirs info
				$(this).find("input[name='mountPath']").css("display","none");
				$(this).find("input[name='hostPath']").css("display","none");
				$(this).find("input[name='insPath']").css("display","");
			}else if(tmpDraft["volumeType"] == "HOSTPATH"){
				$(this).find("select[name='dirs']").find("option[value='HOSTPATH']").attr("selected","selected");
				$(this).find("select[name='writeOrRead']").find("option[value='"+tmpDraft["readOnly"]+"']").attr("selected","selected");
				$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
				$(this).find("input[name='mountPath']").val(tmpDraft["mountPath"]);
				$(this).find("input[name='hostPath']").val(tmpDraft["hostPath"]);
				
				//hide emptydirs and show hostpath info
				$(this).find("input[name='mountPath']").css("display","");
				$(this).find("input[name='hostPath']").css("display","");
				$(this).find("input[name='insPath']").css("display","none");
			}
			
			//bind change event
			$($(this).find("select[name='dirs']")).bind("change",function(){
				var currentSelect = $(this).val();
				//host
				if(currentSelect == "HOSTPATH"){
					$(this).parent().children("input[name='insPath']").hide();
					$(this).parent().children("input[name='mountPath']").show();
					$(this).parent().children("input[name='hostPath']").show();
				//instance
				}else if(currentSelect == "EMPTYDIR"){
					$(this).parent().children("input[name='insPath']").show();
					$(this).parent().children("input[name='mountPath']").hide();
					$(this).parent().children("input[name='hostPath']").hide();
				}
			});
			seq--;
			loadStorages(volumeMountDrafts,seq);
			
		});
	}
}

function getEnvsToString(envs){
	if(envs==null || envs.length==0){
		return "";
	}
	var arr=[];
	for(var i=0;i<envs.length;i++){
		var map = envs[i];
		arr.push(map.key+"="+map.value);
	}
	return arr.join(",");
}

function getEnvsToArray(envsStr){
	if(envsStr==null || envsStr.trim()==""){
		return null;
	}
	var envs=[];
	var arr = envsStr.split(",");
	for(var i=0;i<arr.length;i++){
		envStr = arr[i];
		var env={};
		env.key=envStr.split("=")[0]+"";
		env.value=envStr.split("=")[1]+"";
		envs.push(env);
	}
	return envs;
}
//save single image configuration 
$("#btnnew3pre").bind("click", function(event) {
	var uniqueImage = $("#uniqueImage").val();
	var registry = $("#registryConfig").val();
	var image = $("#imageConfig").val();
	
	var tag = $("#tagConfig").val();
	var commands = $("#startCommands").val().split(",");
	var envs = getEnvsToArray($("#envvar").val());
	var cpu = $("#cpus").val();
	var mem = $("#mems").val();
	var currentContainerDraft = {};
	var volumeMountDrafts=[];
	//save storages
	$("#storages >div").each(function(){
		var volumeMountDraft = {};
		var selectDir = $(this).find("select[name='dirs']").val();
		var volumeName = $(this).find("input[name='volumeName']").val();
		if(volumeName==null || volumeName.trim()==""){
			return;
		}
		var writeOrRead = new Boolean($(this).find("select[name='writeOrRead']").val());
		var mountPath = $(this).find("input[name='mountPath']") == null?"":$(this).find("input[name='mountPath']").val();
		var hostPath = $(this).find("input[name='hostPath']") == null?"":$(this).find("input[name='hostPath']").val();
		var insPath = $(this).find("input[name='insPath']") == null?"":$(this).find("input[name='insPath']").val();
		volumeMountDraft["name"] = volumeName;
		volumeMountDraft["readOnly"] = writeOrRead;
		volumeMountDraft["volumeType"] = selectDir;
		if(selectDir == "HOSTPATH"){
			volumeMountDraft["mountPath"] = mountPath;
			volumeMountDraft["hostPath"] = hostPath;
		}else if(selectDir == "EMPTYDIR"){
			volumeMountDraft["mountPath"] = insPath;
		}
		volumeMountDrafts.push(volumeMountDraft);
	});
	
	currentContainerDraft["registry"] = registry;
	currentContainerDraft["image"] = image;
	currentContainerDraft["tag"] = tag;
	
	//save commands
	currentContainerDraft["commands"] = commands;
	
	//save envs
	currentContainerDraft["envs"] = envs;
	
	//save cpu and mem
	currentContainerDraft["cpu"] = cpu;
	currentContainerDraft["mem"] = mem;
	//readd into
	currentContainerDraft["volumeMountDrafts"] = volumeMountDrafts;
	
	currentContainerMap[uniqueImage] = currentContainerDraft;
	$("#service-new3").modal("hide");
});

//add new storage
function showStorage(){
    var childdiv=$('<div></div>');  
	childdiv.attr("id","id" + new Date().getTime());
	$("#storages").append(childdiv);
	childdiv.load("/js/statichtml/app/storageTemplate.html #storageTemplate",
			function(responseTxt,statusTxt,xhr){
		
		//bind change event
		$($(this).find("select[name='dirs']")).bind("change",function(){
			var currentSelect = $(this).val();
			//host
			if(currentSelect == "HOSTPATH"){
				$(this).parent().children("input[name='insPath']").hide();
				$(this).parent().children("input[name='mountPath']").show();
				$(this).parent().children("input[name='hostPath']").show();
			//instance
			}else if(currentSelect == "EMPTYDIR"){
				$(this).parent().children("input[name='insPath']").show();
				$(this).parent().children("input[name='mountPath']").hide();
				$(this).parent().children("input[name='hostPath']").hide();
			}
		});
		
	});
}

$("#btncancel3pre").bind("click",function(event){
	//show port mapped detail into sPort tab
	
	//close this window
	$("#service-new3").modal("hide");
});
