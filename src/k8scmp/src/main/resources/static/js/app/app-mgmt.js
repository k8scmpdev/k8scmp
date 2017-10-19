function getSerializeJson(){
	var serializeObj={};    
	var array=$('#appSearchForm').serializeArray();    
	var str=$('#appSearchForm').serialize();    
	$(array).each(function(){    
		if(serializeObj[this.name]){    
			if($.isArray(serializeObj[this.name])){    
				serializeObj[this.name].push(this.value);    
			}else{    
				serializeObj[this.name]=[serializeObj[this.name],this.value];    
			}    
		}else{
			serializeObj[this.name]=this.value;     
		}    
	});    
	return serializeObj;    
}

/*function searchServicesInApp(appId){
	var AjaxURL= "/app/service/search";
	var paramData = {"id":appId};
	$.ajax({
		type: "POST",
		dataType: "html",
		url: AjaxURL,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			$("html").html(data);
		},
		error: function(data) {
			alert("error!");
		}
	});
}*/

function deleteApp(appId){
	var AjaxURL= "/app/delete/"+appId;
	$.ajax({
		type: "GET",
		dataType: "html",
		url: AjaxURL,
		contentType:"application/json",
		success: function (data) {
			refreshApp();
		},
		error: function(data) {
			alert("error!");
		}
	});
}

function refreshApp(){
	window.location.href="/app";
}




