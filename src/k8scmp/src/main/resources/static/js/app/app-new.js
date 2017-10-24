function createApp(){
	var AjaxURL= "";
	var paramData = getSerializeJson();
	var edit = $("#edit").val();
	if(edit == "true"){
		AjaxURL= "/app/modify";
	}else{
		AjaxURL= "/app/create";
	}
	$.ajax({
		type: "POST",
		dataType: "html",
		url: AjaxURL,
		data: JSON.stringify(paramData),
		contentType:"application/json",
		success: function (data) {
			refreshApp();
			return false;
		},
		error: function(data) {
			alert("error!");
			return false;
		}
	});
}
function refreshApp(){
	window.location.href="/app";
}

function getSerializeJson(){
	var serializeObj={};    
	var array=$('#createAppFrom').serializeArray();    
	var str=$('#createAppFrom').serialize();    
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
