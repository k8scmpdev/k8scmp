//table
var imageTable;
//global container volume draft
var volumeDrafts = [];
var volumeDraftsMap = {};

var containerDrafts = [];
var containerDraftsMap = {};

var singleContainerDraft = {};
var singleVolumeDraft = {};

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
	window.location.href="/app/service/service-mgmt";
});

function refreshService(appId){
	var AjaxURL = "/app/service/"+appId+"?appId="+appId;
	window.location.href=AjaxURL;
}

$("#btnnew2next").bind("click",function(event){
	$("#service-new1").css("display","none"); 
	$("#service-new2").css("display","block");
	$("#service-new4").css("display","none");
});

//save single image configuration 
$("#btnnew3pre").bind("click", function(event) {
	var uniqueImage = $("#hiddenUnique").val();
	var commands = $("#startCommands").val().split(",");
	var envs = $("#envvar").val().split(",");
	var cpu = $("#cpus").val();
	var mem = $("#mems").val();
	var singleVolumeDraftList = [];
	var currentContainerDraft = [];
	var singleVolumeMountDrafts=[];
	//save storages
	$("#storages >div").each(function(){
		currentContainerDraft = containerDraftsMap[uniqueImage];
		singleVolumeMountDrafts = currentContainerDraft["volumeMountDrafts"]==null?[]:currentContainerDraft["volumeMountDrafts"];
		var singleImageVolumeDrafts = {};
		var selectDir = $(this).find("select[name='dirs']").val();
		var volumeName = $(this).find("input[name='volumeName']").val();
		var writeOrRead = new Boolean($(this).find("select[name='writeOrRead']").val());
		var mountPath = $(this).find("input[name='mountPath']") == null?"":$(this).find("input[name='mountPath']").val();
		var hostPath = $(this).find("input[name='hostPath']") == null?"":$(this).find("input[name='hostPath']").val();
		var insPath = $(this).find("input[name='insPath']") == null?"":$(this).find("input[name='insPath']").val();
		//hostpath
		if(selectDir == "HOSTPATH"){
			//saved into containerdraft --> volumeMountDraft
			singleVolumeMountDrafts["name"] = volumeName;
			singleVolumeMountDrafts["readOnly"] = writeOrRead;
			singleVolumeMountDrafts["mountPath"] = mountPath;
			
			//saved into volumedraft
			var singleVolumeDraftItem = {};
			var singleVolumeDraftItemMap = {};
			singleVolumeDraftItem["name"] = volumeName;
			singleVolumeDraftItem["volumeType"] = "EMPTYDIR";
			singleVolumeDraftItem["hostPath"] = hostPath;
			singleVolumeDraftItemMap[volumeName] = singleVolumeDraftItem;
			singleVolumeDraftList.push(singleVolumeDraftItemMap);	
		//emptydir
		}else if(selectDir == "EMPTYDIR"){
			//set name,volumetype into volumeDrafts
			singleVolumeDraft = {};
			singleVolumeDraft["name"] = volumeName;
			singleVolumeDraft["volumeType"] = "EMPTYDIR";
			volumeDraftsMap[hiddenUnique]=singleVolumeDraft;
			
			//set name,readonly,mountpath into containerDrafts
			singleVolumeMountDrafts["name"] = volumeName;
			singleVolumeMountDrafts["readOnly"] = writeOrRead;
			singleVolumeMountDrafts["mountPath"] = mountPath;
		}
		
	});
	
	volumeDraftsMap[hiddenUnique]=singleVolumeDraftList;
	
	//save commands
	currentContainerDraft["commands"] = commands;
	
	//save envs
	currentContainerDraft["envs"] = envs;
	
	//save cpu and mem
	currentContainerDraft["cpu"] = cpu;
	currentContainerDraft["mem"] = mem;
	//readd into
	currentContainerDraft["volumeMountDrafts"] = singleVolumeMountDrafts;
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
			if(currentSelect == "1"){
				$(this).parent().children("input[name='insPath']").hide();
				$(this).parent().children("input[name='conPath']").show();
				$(this).parent().children("input[name='hostPath']").show();
			//instance
			}else if(currentSelect == "2"){
				$(this).parent().children("input[name='insPath']").show();
				$(this).parent().children("input[name='conPath']").hide();
				$(this).parent().children("input[name='hostPath']").hide();
			}
		});
		
	});
}

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
});

//create service
$('#createServiceForm').submit(function(){
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
});

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
		var containerDrafts = getContainerDrafts();
		var volumeDrafts = getVolumeDrafts();
		var versionItem = {};
		versionItem["versionType"] = versionType;
		versionItem["containerDrafts"] = containerDrafts;
		versionItem["volumeDrafts"] = volumeDrafts;
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

//make up containerdraft in a service 
function getContainerDrafts(){
	var returnContainerDrafts = [];
	var iteratorContainerDraftsMap = containerDraftsMap;
	if(iteratorContainerDraftsMap != null){
		for(var key in iteratorContainerDraftsMap){
			returnContainerDrafts.concat(iteratorContainerDraftsMap[key]);
		}
	}
	return returnContainerDrafts;
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
$("#imagesbtn").bind("click",function(event){
	var selectedRows = getSelectedRow();
	var newHtml = "";
	var oldContent = "";
	//alert("beforeContent:"+beforeContent);
	if($("#tBody").children("tr").children("td[class='dataTables_empty']").length){
		//empty tables
		$("#tBody").html("");
	}
	oldContent = $("#tBody").html();
	for(var i=0;i<selectedRows.length;i++){
		var oneRow = selectedRows[i];
		var singleContainerDraft = {};
		var registry = oneRow["registry"];
		var image = oneRow["id"];
		var tag = oneRow["versions"];
		var uniqueImage = registry.concat(image,tag);
		singleContainerDraft["registry"] = registry;
		singleContainerDraft["image"] = image;
		singleContainerDraft["tag"] = tag;
		//save into containerDraftsMap
		containerDraftsMap[uniqueImage] = singleContainerDraft;
		
		//check whether has added 
		
		//add new row to the images list table
		var trRow = "<tr><td class='i10' style='width: 30%;min-width:30%;max-width:30%;'>"+image+"</td>" +
				"<td class='i20' style='width: 30%;min-width:30%;max-width:30%;'>"+registry+"</td>" +
				"<td class='i30' style='width: 10%;min-width:10%;max-width:10%;'>"+tag+"</td>" +
				"<td class='i40' style='width: 30%;min-width:30%;max-width:30%;'><a title='配置' class='tip-bottom iconOperation' href='#service-new3' data-toggle='modal' onclick='showImageConfiguration(\""+uniqueImage+"\","+"null);'><i class='icon-edit'></i><span>配置</span></a>&nbsp;&nbsp;<a title='删除' class='tip-bottom iconOperation' href='javascript:void(0);' onclick='deleteImage("+"\""+uniqueImage+"\""+",this);'><i class='icon-trash'></i><span>删除</span></a></td>"+
				"</tr>";
		newHtml = newHtml.concat(trRow);
	}
	$("#tBody").html(newHtml.concat(oldContent));
	$("#myModal").modal("hide");
});

//remove image tr from image list table
function deleteImage(uniqueImage,obj){
	if(containerDraftsMap[uniqueImage] != null){
		delete containerDraftsMap[uniqueImage];
		var tr=obj.parentNode.parentNode;
		var tbody=tr.parentNode;
		tbody.removeChild(tr);
	}
}

//show correct single image configuration window
/**
 * imageId made up of image name+image registry+image versions
 * volumeList is single image VolumeMountDraft
 * @param imageId
 * @param volumeList
 * @returns
 */
function showImageConfiguration(imageId,localvolumeMountDrafts,localVolumeDrafts){
	var uniqueImage = $("#hiddenUnique").val(imageId);//unique image
	var singleVolumeDraftsMap = volumeDraftsMap[uniqueImage];//volumedraft of this image
	$("#storages").html();//clear all sub divs
	
	var singleContainerDraft = containerDraftsMap[imageId];
	var singleVolumeMountDrafts = [];
	
	/************************show storages*******************/
	//haved setted part containerdraft
	if(singleContainerDraft != null){
		singleVolumeMountDrafts = singleContainerDraft["volumeMountDrafts"];
		//曾经打开过该页面设过值
		if(singleVolumeMountDrafts != null){
			//show stoages
			//show emptydirs for singleVolumeMountDrafts saved all emptydirs info
			var iteratorVolumeMountDrafts = singleVolumeMountDrafts;
			var iteratorVolumeDrafts = singleVolumeDraftsMap[tmpDraft["name"]];
			iteratorStorages(iteratorVolumeMountDrafts,iteratorVolumeDrafts);
			for(var k=0;k<singleVolumeMountDrafts.length;k++){
				var tmpDraft = singleVolumeMountDrafts[k];
				var dirType= iteratorVolumeDrafts["volumeType"];
				var childdiv=$('<div></div>');  
				childdiv.attr("id","id" + new Date().getTime());
				$("#storages").append(childdiv);
				childdiv.load("/js/statichtml/app/storageTemplate.html #storageTemplate",
						function(responseTxt,statusTxt,xhr){
					
					//set value
					if(dirType == "EMPTYDIR"){
						$(this).find("select[name='dirs']").find("option[value='EMPTYDIR']").attr("selected","selected");
						$(this).find("select[name='writeOrRead']").find("option[value=']"+tmpDraft["readOnly"]+"'").attr("selected","selected");
						$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
						$(this).find("input[name='insPath']").val(tmpDraft["mountPath"]);
						
						//hide hostpath and show emptydirs info
						$(this).find("input[name='mountPath']").css("display","none");
						$(this).find("input[name='hostPath']").css("display","none");
						$(this).find("input[name='insPath']").css("display","block");
					}else if(dirType == "HOSTPATH"){
						$(this).find("select[name='dirs']").find("option[value='HOSTPATH']").attr("selected","selected");
						$(this).find("select[name='writeOrRead']").find("option[value=']"+tmpDraft["readOnly"]+"'").attr("selected","selected");
						$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
						$(this).find("input[name='mountPath']").val(tmpDraft["mountPath"]);
						$(this).find("input[name='hostPath']").val(iteratorVolumeDrafts["hostPath"]);
						
						//hide emptydirs and show hostpath info
						$(this).find("input[name='mountPath']").css("display","block");
						$(this).find("input[name='hostPath']").css("display","block");
						$(this).find("input[name='insPath']").css("display","none");
					}
					
					//bind change event
					$($(this).find("select[name='dirs']")).bind("change",function(){
						var currentSelect = $(this).val();
						//host
						if(currentSelect == "1"){
							$(this).parent().children("input[name='insPath']").hide();
							$(this).parent().children("input[name='conPath']").show();
							$(this).parent().children("input[name='hostPath']").show();
						//instance
						}else if(currentSelect == "2"){
							$(this).parent().children("input[name='insPath']").show();
							$(this).parent().children("input[name='conPath']").hide();
							$(this).parent().children("input[name='hostPath']").hide();
						}
					});
					
				});
			}
			
			/*************show commands***********/
			$("#startCommands").val(singleContainerDraft["commands"].join(","));
			
			/*************show envs***********/
			$("#envvar").val(singleContainerDraft["envs"].join(","));
			
			/*************show cpu***********/
			$("#cpus").val(singleContainerDraft["cpu"]);
			
			/*************show mem***********/
			$("#mems").val(singleContainerDraft["mem"]);
		
		}else{
			/*var childdiv=$('<div></div>');  
			childdiv.attr("id","id" + new Date().getTime());
			$("#storages").append(childdiv);
			childdiv.load("/js/statichtml/app/storageTemplate.html #storageTemplate",
					function(responseTxt,statusTxt,xhr){
				
				//bind change event
				$($(this).find("select[name='dirs']")).bind("change",function(){
					var currentSelect = $(this).val();
					//host
					if(currentSelect == "1"){
						$(this).parent().children("input[name='insPath']").hide();
						$(this).parent().children("input[name='conPath']").show();
						$(this).parent().children("input[name='hostPath']").show();
					//instance
					}else if(currentSelect == "2"){
						$(this).parent().children("input[name='insPath']").show();
						$(this).parent().children("input[name='conPath']").hide();
						$(this).parent().children("input[name='hostPath']").hide();
					}
				});
				
			});*/
		}
	}
	
	//saved into db before
	if(localVolumeDrafts != null){
		for(var k=0;k<localvolumeMountDrafts.length;k++){
			var tmpDraft = localvolumeMountDrafts[k];
			var dirType= getDirType(tmpDraft["name"],localVolumeDrafts);
			var childdiv=$('<div></div>');  
			childdiv.attr("id","id" + new Date().getTime());
			$("#storages").append(childdiv);
			childdiv.load("/js/statichtml/app/storageTemplate.html #storageTemplate",
					function(responseTxt,statusTxt,xhr){
				
				//set value
				if(dirType == "EMPTYDIR"){
					$(this).find("select[name='dirs']").find("option[value='EMPTYDIR']").attr("selected","selected");
					$(this).find("select[name='writeOrRead']").find("option[value=']"+tmpDraft["readOnly"]+"'").attr("selected","selected");
					$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
					$(this).find("input[name='insPath']").val(tmpDraft["mountPath"]);
					
					//hide hostpath and show emptydirs info
					$(this).find("input[name='mountPath']").css("display","none");
					$(this).find("input[name='hostPath']").css("display","none");
					$(this).find("input[name='insPath']").css("display","block");
				}else if(dirType == "HOSTPATH"){
					$(this).find("select[name='dirs']").find("option[value='HOSTPATH']").attr("selected","selected");
					$(this).find("select[name='writeOrRead']").find("option[value=']"+tmpDraft["readOnly"]+"'").attr("selected","selected");
					$(this).find("input[name='volumeName']").val(tmpDraft["name"]);
					$(this).find("input[name='mountPath']").val(tmpDraft["mountPath"]);
					$(this).find("input[name='hostPath']").val(iteratorVolumeDrafts["hostPath"]);
					
					//hide emptydirs and show hostpath info
					$(this).find("input[name='mountPath']").css("display","block");
					$(this).find("input[name='hostPath']").css("display","block");
					$(this).find("input[name='insPath']").css("display","none");
				}
				
				//bind change event
				$($(this).find("select[name='dirs']")).bind("change",function(){
					var currentSelect = $(this).val();
					//host
					if(currentSelect == "1"){
						$(this).parent().children("input[name='insPath']").hide();
						$(this).parent().children("input[name='conPath']").show();
						$(this).parent().children("input[name='hostPath']").show();
					//instance
					}else if(currentSelect == "2"){
						$(this).parent().children("input[name='insPath']").show();
						$(this).parent().children("input[name='conPath']").hide();
						$(this).parent().children("input[name='hostPath']").hide();
					}
				});
				
			});
		}
		
		/*************show commands***********/
		$("#startCommands").val(singleContainerDraft["commands"].join(","));
		
		/*************show envs***********/
		$("#envvar").val(singleContainerDraft["envs"].join(","));
		
		/*************show cpu***********/
		$("#cpus").val(singleContainerDraft["cpu"]);
		
		/*************show mem***********/
		$("#mems").val(singleContainerDraft["mem"]);
		
	}
/*	if(singleContainerDraft != null || volumeList!=null && volumeList.size()>0){
		//get configuration window value
		var singleVolumeMountDraftsList =  singleContainerDraft["volumeMountDrafts"];
		var commandsList = singleContainerDraft["commands"];
		var argsList = singleContainerDraft["args"];
		var cpu = singleContainerDraft["cpu"];
		var mem = singleContainerDraft["mem"];
		
		for(var j=0;j<singleVolumeMountDraftsList.length;j++){
			//load associated number storages and set value 
			var tempSingleVolumeMountDraft = singleVolumeMountDraftsList.get(j);
			
		}
	//new configuration
	//show one storage
	}else{
		var childdiv=$('<div></div>');  
		childdiv.attr("id","id" + new Date().getTime());
		$("#storages").append(childdiv);
		childdiv.load("/js/statichtml/app/storageTemplate.html #storageTemplate",
				function(responseTxt,statusTxt,xhr){
			
			//bind change event
			$($(this).find("select[name='dirs']")).bind("change",function(){
				var currentSelect = $(this).val();
				//host
				if(currentSelect == "1"){
					$(this).parent().children("input[name='insPath']").hide();
					$(this).parent().children("input[name='conPath']").show();
					$(this).parent().children("input[name='hostPath']").show();
				//instance
				}else if(currentSelect == "2"){
					$(this).parent().children("input[name='insPath']").show();
					$(this).parent().children("input[name='conPath']").hide();
					$(this).parent().children("input[name='hostPath']").hide();
				}
			});
			
		});
		
	}*/
}

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
