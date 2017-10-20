function showPortMapped(){
    var childdiv=$('<div></div>');  
	childdiv.attr("id","id" + new Date().getTime());
	$("#portMapped").append(childdiv);
	childdiv.load("/js/statichtml/app/portMappedTemplate.html #portMappedTemplate");
}

$(".radioItem").change(function(event){
	var selectValue=$("input[name='publishRadios']:checked").val();
	if(selectValue == 1){
		$(".portMappes").css("display","block");
	}else{
		$(".portMappes").css("display","none");
	}
});

$(document).ready(function(){
	var oTable=$('.storage-table').dataTable({
		"scrollY": "160px",
	    "scrollCollapse": "true",
		"pagingType":"full_numbers",
	    "paging": "true",
		"lengthMenu":[5],
		"language":	{
			"search":"搜索",
	        "lengthMenu": "每页 _MENU_ 条记录",
	        "zeroRecords": "没有找到记录",
	        "info": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
	        "infoEmpty": "无记录",
	        "infoFiltered": "(从 _MAX_ 条记录过滤)",
	        "paginate": {
	            "first": "首页",
	            "previous": "上页",
	            "next": "下页",
	            "last": "末页"
	        }
	    },
	    "fnDrawCallback":function(oSettings){
	    	$(".dataTables_length").offset({top:$(".dataTables_info").offset().top+6});
	    }
	});
});

$("#btnnew2pre").bind("click",function(event){
	//show port mapped detail into sPort tab
	
	//close this window
	$("#sPortMappedModel").modal("hide");
});