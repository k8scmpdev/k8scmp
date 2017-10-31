Highcharts.setOptions({
    global: {
        useUTC: false
    }
});
function activeLastPointToolip(chart) {
//	alert(chart.series[0].points)
    var points = chart.series[0].points;
    chart.tooltip.refresh(points[points.length -1]);
}

function initData(isReInit,type){
	var arr = [];
	var hostname = $('#hostname').val();
	var hostlist = [hostname];
	var dataSpec = $('#dataSpec').val();
	var startTime = Number($('#startTime').val().replace(/\-/g,''));
	var endTime = Number($('#endTime').val().replace(/\-/g,''));
	$.ajaxSettings.async = false;
//	if(isReInit){
//		$.getJSON("/monitor/host/getDetailData",function(data){//获取Json文件,并创建Json对象
		$.getJSON("/monitor/host/getDetailDataByKeys?hostlist="+hostlist+"&dataSpec="+dataSpec+"&startTime="+startTime+"&endTime="+endTime,function(data){//获取Json文件,并创建Json对象
			if(type=='cpu'){
				arr = createArr(data.datalist['cpu.busy']);
			}else if(type=='mem'){
				arr = createArr(data.datalist['mem.memused.percent']);
			}
			else if(type=='disk'){
				arr = createArr(data.datalist['df.bytes.used.percent']);
			}
	    })
//	}
    return arr;
}

function refreshDate(series,chart,type){
	var x= (new Date()).getTime();
	var y =  0;
	var hostname = $('#hostname').val();
	var hostlist = [hostname];
	var dataSpec = $('#dataSpec').val();
	var startTime = Number($('#startTime').val().replace(/\-/g,''));
	var endTime = Number($('#endTime').val().replace(/\-/g,''));
//	if(isRefresh){
		$.getJSON("/monitor/host/getDetailData1raw?hostlist="+hostlist+"&dataSpec="+dataSpec+"&startTime="+startTime+"&endTime="+endTime,function(data){//获取Json文件,并创建Json对象
			if(type=='cpu'){
				createRefreshData(series,chart,data.datalist['cpu.busy']);
			}else if(type=='mem'){
				createRefreshData(series,chart,data.datalist['mem.memused.percent']);
			}else if(type=='disk'){
				createRefreshData(series,chart,data.datalist['df.bytes.used.percent']);
			}
	    })
//	}
}

function changeDataByKeys(){
	//重新刷新详情页面
	var logicCluster = $('#logicCluster').val();
	var hostname = $('#hostname').val();
	var hostlist = [hostname];
	var dataSpec = $('#dataSpec').val();
	var startTime = Number($('#startTime').val().replace(/\-/g,'').replace(/\//g,''));
	var endTime = Number($('#endTime').val().replace(/\-/g,'').replace(/\//g,''));
	var timeft = $('input[name=timeft]:checked').val();
	window.location.href="/monitor/host/detail?logicCluster="+logicCluster+"&hostName="+hostname+"&dataSpec="+dataSpec+"&startTime="+startTime+"&endTime="+endTime+"&timeft="+timeft;
//	window.location.href="/monitor/host/getDetailData";
}

function createArr(datalist){
	var arr = [];
	if(typeof(datalist)!="undefined"){
		$.each(datalist,function(key, value){
			arr.push({
	            x: key*1000,
	            y: value
	        });
    	});
	}else{
		var time=(new Date()).getTime(),i;
		for(i=-178;i<=0;i++){
			arr.push({
	            x: time+i*10*1000,
	            y: 0
	        });
		}
	}
	return arr;
}

function createRefreshData(series,chart,datalist){
	var x= (new Date()).getTime();
	var y =  0;
	if(typeof(datalist)!="undefined"){
		$.each(datalist,function(key, value){
    		x = key*1000;
	        y = value;
	        series.addPoint([x, y], true, true);
	    	activeLastPointToolip(chart)
	    	return;
    	});
	}else{
		var time=(new Date()).getTime(),i;
		for(i=-178;i<=0;i++){
            x = time+i*10*1000,
            y = 0,
            series.addPoint([x, y], true, true);
	    	activeLastPointToolip(chart)
	    	return;
		}
	}
}

function setTime(){
//	$('name=[timeft:selected]').val();
	var curDate = new Date();
	var preDate = new Date(curDate.getTime() - 24*60*60*1000); //前一天
	var nextDate = new Date(curDate.getTime() + 24*60*60*1000); //后一天
	if($('input[name=timeft]:checked').val()=='0'){
		$('#startTime').val('');
		$('#endTime').val('');
	}else if($('input[name=timeft]:checked').val()=='1'){
		$('#startTime').val(curDate.toLocaleDateString());
		$('#endTime').val(nextDate.toLocaleDateString());
	}
	changeDataByKeys();
}

$('#hostCPU').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg, // don't animate in old IE
        marginRight: 10,
        events: {
            load: function () {
                // set up the updating of the chart each second
                var series = this.series[0],
                    chart = this;
                setInterval(function () {
                	refreshDate(series,chart,'cpu')
                }, $('#intervalStep').val());//定时器时间间隔
            }
        }
    },
    title: {
        text: '主机CPU使用率'
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 150
    },
    yAxis: {
        title: {
            text: '使用率(%)'
        },
        plotLines: [{
            value: 0,
            width: 1,
            color: '#808080'
        }]
    },
    tooltip: {
        formatter: function () {
            return '<b>' + this.series.name + '</b><br/>' +
                Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                Highcharts.numberFormat(this.y, 2);
        }
    },
    legend: {
        enabled: false
    },
    exporting: {
        enabled: false
    },
    series: [{
        name: '使用率(%)',
        data: (function () {
            var data = initData(true,'cpu');
            return data;
        }())
    }]
}, function(c) {
	activeLastPointToolip(c);
});

$('#hostDisk').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg, // don't animate in old IE
        marginRight: 10,
        events: {
            load: function () {
                // set up the updating of the chart each second
                var series = this.series[0],
                    chart = this;
                setInterval(function () {
                	refreshDate(series,chart,'disk')
                }, $('#intervalStep').val());
            }
        }
    },
    title: {
        text: '主机磁盘使用率'
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 150
    },
    yAxis: {
        title: {
            text: '使用率(%)'
        },
        plotLines: [{
            value: 0,
            width: 1,
            color: '#808080'
        }]
    },
    tooltip: {
        formatter: function () {
            return '<b>' + this.series.name + '</b><br/>' +
                Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                Highcharts.numberFormat(this.y, 2);
        }
    },
    legend: {
        enabled: false
    },
    exporting: {
        enabled: false
    },
    series: [{
        name: '使用率(%)',
        data: (function () {
            var data = initData(false,'disk');
            return data;
        }())
    }]
}, function(c) {
	activeLastPointToolip(c);
});

$('#hostMemory').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg, // don't animate in old IE
        marginRight: 10,
        events: {
            load: function () {
                // set up the updating of the chart each second
                var series = this.series[0],
                    chart = this;
                setInterval(function () {
                	refreshDate(series,chart,'mem')
                }, $('#intervalStep').val());
            }
        }
    },
    title: {
        text: '内存使用率'
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 150
    },
    yAxis: {
        title: {
            text: '使用率(%)'
        },
        plotLines: [{
            value: 0,
            width: 1,
            color: '#808080'
        }]
    },
    tooltip: {
        formatter: function () {
            return '<b>' + this.series.name + '</b><br/>' +
                Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                Highcharts.numberFormat(this.y, 2);
        }
    },
    legend: {
        enabled: false
    },
    exporting: {
        enabled: false
    },
    series: [{
        name: '使用率(%)',
        data: (function () {
            var data = initData(false,'mem');
            return data;
        }())
    }]
}, function(c) {
	activeLastPointToolip(c);
});
