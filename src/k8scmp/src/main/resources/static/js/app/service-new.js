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
	$("#service-new1").css("display","none");
	$("#service-new2").css("display","block");
	$("#service-new4").css("display","none"); 
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

//save single image configuration 
$("#btnnew3pre").bind("click", function(event) {
	//save storages
	$("#storages >div").each(function(){
		var singleImageVolumeDrafts = {};
		var selectDir = $(this).find("select[name='dirs']").val();
		var volumeName = $(this).find("input[name='volumeName']").val();
		//hostpath
		if(selectDir == "HOSTPATH"){
			
		//emptydir
		}else if(selectDir == "EMPTYDIR"){
			//set name,volumetype into volumeDrafts
			singleVolumeDraft = {};
			singleVolumeDraft["name"] = volumeName;
			singleVolumeDraft["volumeType"] = "EMPTYDIR";
			volumeDraftsMap.put(volumeName,singleVolumeDraft);
			
			//set name,readonly,mountpath into containerDrafts
			
		}
		
	});
	/*var singleImageVolumesLength = $("#storages").children().length;
	for(var i=0;i<singleImageVolumesLength;i++){
		alert($("#storages").children[i].html());
	}*/
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
		"language":	{
	        "zeroRecords": "没有找到记录",
	        "infoEmpty": "无记录",
	    },
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
	    "fnDrawCallback":function(oSettings){
	    	$(".dataTables_length").offset({top:$(".dataTables_info").offset().top});
	    }
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
	$.ajax({
		type: "POST",
		dataType: "html",
		url: AjaxURL,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			window.location.href="/app";
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
	paramData["serviceConfigInfo"] = serviceConfigInfo;
	paramData["versions"] = versions;
	
	//get serviceConfigInfo item value
	var appCode = $("#appCode").val();
	var serviceCode = $("#serviceName").val();
	var startSeq = $("#startSeq").val();
	var serviceDescription = $("#serviceDescription").val();
	var defaultReplicas = $("#defaultReplicas").val();
	var versionType=$('input:radio[name="cfradios"]:checked').val();
	
	//set serviceConfigInfo
	serviceConfigInfo["serviceCode"] = serviceCode;
	serviceConfigInfo["appId"] = appCode;
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
	}else if(val==2){
		var podSpecStr = $("#yamlSpec").val();
		var versionItem = {};
		versionItem["podSpecStr"] = podSpecStr;
		versionItem["versionType"] = versionType;
		versions[0] = versionItem;
	}
}

//get all images configuration in a service 
function getContainerDrafts(){
	var returnLength = new Array(contanierLength);
	for(var i=0;i<contanierLength;i++){
		var containerDraftsItem = {};
	}
	return returnValue;
}

function getVolumeDrafts(){
	var returnValue = [];
	var volumeDraftsItem = {};
	return returnValue;
}

//image
var chooseImagesList = [];
chooseImagesList.push({"id":"test-java","version":"6.0"});
chooseImagesList.push({"id":"dev-mysql","version":"5.0"});
chooseImagesList.push({"id":"nginx","version":"test1"});
chooseImagesList.push({"id":"tomcat","version":"bate"});

//save choose image window value into a image map
$("#imagesbtn").bind("click",function(event){
	var testlist =[];
	testlist.push("d");
	testlist.push("s");
	testlist.push("m");
	alert(testlist[0]);
	var selectedRows = getSelectedRow();
	for(var i=0;i<selectedRows.length;i++){
		var oneRow = selectedRows[i];
		alert(oneRow["id"]);
		var singleContainerDraft = {};
		var uniqueImage = oneRow["registry"]+oneRow["id"]+oneRow["versions"];
		singleContainerDraft["registry"] = oneRow["registry"];
		singleContainerDraft["image"] = oneRow["id"];
		singleContainerDraft["tag"] = oneRow["versions"];
		//save into containerDraftsMap
		containerDraftsMap[uniqueImage] = singleContainerDraft;
		
		//check whether has added 
		
		//add new row to the images list table
		var trRow = "<tr><td class='i10'>"+oneRow["id"]+"</td>" +
				"<td>"+oneRow["registry"]+"</td>" +
				"<td>"+oneRow["versions"]+"</td>" +
				"<td><a title='配置' class='tip-bottom iconOperation' href='#service-new3' data-toggle='modal' onclick='showImageConfiguration('+singleContainerDraft+');'><i class='icon-edit'></i><span>配置</span></a><a title='删除' class='tip-bottom iconOperation' href='deleteImage();'><i class='icon-trash'></i><span>删除</span></a></td>"+
				"</tr>";
		alert(trRow);
	}
});

//show correct single image configuration window
/**
 * imageId made up of image name+image registry
 * volumeList is single image VolumeMountDraft
 * @param imageId
 * @param volumeList
 * @returns
 */
function showImageConfiguration(imageId,volumeList){
	$("#storages").html();//clear all sub divs
	var singleContainerDraft = containerDraftsMap.get(imageId);
	//haved setted storages before
	if(singleContainerDraft != null || volumeList!=null && volumeList.size()>0){
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
	}
	
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
	var tr = {};
	var tmpObj = {};
	$(".image-table span[class='checked'] input[type='checkbox']").each(function(){
		tmpObj = $(this).parent().parent().parent().parent().parent().parent();
		tr["id"] = tmpObj.children("td[id='id']").html();
		tr["registry"] = tmpObj.children("td[id='registry']").html();
		tr["versions"] = tmpObj.children("td[id='versions']").html();
		returnObj.push(tr);
	});
	return returnObj;
}

