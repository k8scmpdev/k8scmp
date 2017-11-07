
/**
 * Unicorn Admin Template
 * Diablo9983 -> diablo9983@gmail.com
**/
$(document).ready(function(){
	var oxTable=$('.data-table').dataTable({
		"scrollY": "200px",
	    "scrollCollapse": true,
		"pagingType":"full_numbers",
	    "paging": true,
		"language":	{
			"search":"搜索",
	        "lengthMenu": "每页 _MENU_ 条记录",
	        "zeroRecords": "没有找到记录",
	        "info": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
	        "infoEmpty": "无记录",
	        "infoFiltered": "",
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
	
	var oxyTable=$('.scroll-table').dataTable({
		"scrollY": "200px",
		"scrollX":true,
	    "scrollCollapse": true,
		"pagingType":"full_numbers",
	    "paging": true,
		"sorting":false,
		"searching":false,
		"lengthChange":false,
		"language":	{
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
	    "columnDefs": [ 
	    	{
	        "targets": 8,
	        "visible":false
	    	} 
	    ],
	    "fnDrawCallback":function(oSettings){
	    	$(".dataTables_length").offset({top:$(".dataTables_info").offset().top+6});
	    }
	});

	var osTable=$('.nosearch-table').dataTable({
		"scrollY": "100px",
	    "scrollCollapse": false,
		"pagingType":"full_numbers",
	    "paging": true,
		"sorting":false,
		"searching":false,
		"lengthChange":false,
		"language":	{
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
/*	    	alert($(".dataTables_info").offset().top);
	    	alert($(".dataTables_length").offset().top);*/
	    	$(".dataTables_length").offset({top:$(".dataTables_info").offset().top});
	    }
	});
	
	$('input[type=checkbox],input[type=radio],input[type=file]').uniform();
	
	$('select').select2();
	
	$("span.icon input:checkbox, th input:checkbox").click(function() {
		var checkedStatus = this.checked;
		var checkbox = $(this).parents('.widget-box').find('tr td:first-child input:checkbox');		
		checkbox.each(function() {
			this.checked = checkedStatus;
			if (checkedStatus == this.checked) {
				$(this).closest('.checker > span').removeClass('checked');
			}
			if (this.checked) {
				$(this).closest('.checker > span').addClass('checked');
			}
		});
	});	
}); 
