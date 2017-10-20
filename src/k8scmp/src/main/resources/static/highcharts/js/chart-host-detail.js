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
	var arr = []
	$.ajaxSettings.async = false;
//	if(isReInit){
		$.getJSON("/monitor/host/getDetailData",function(data){//获取Json文件,并创建Json对象
			if(type=='cpu'){
		    	$.each(data.datalist['cpu.busy'],function(key, value){
		    		arr.push({
			            x: key*1000,
			            y: value
			        });
		    	});
			}else if(type=='mem'){
				$.each(data.datalist['mem.memused.percent'],function(key, value){
		    		arr.push({
			            x: key*1000,
			            y: value
			        });
		    	});
			}
			else if(type=='disk'){
				$.each(data.datalist['df.bytes.used.percent'],function(key, value){
					arr.push({
			            x: key*1000,
			            y: value
			        });
		    	});
			}
	    })
//	}
    return arr;
}

function refreshDate(series,chart,type){
	var x= (new Date()).getTime();
	var y =  Math.random();
//	if(isRefresh){
		$.getJSON("/monitor/host/getDetailData1raw",function(data){//获取Json文件,并创建Json对象
			if(type=='cpu'){
		    	$.each(data.datalist['cpu.busy'],function(key, value){
		    		x=key*1000;
			        y= value;
			        series.addPoint([x, y], true, true);
			    	activeLastPointToolip(chart)
			    	return;
		    	});
			}else if(type=='mem'){
				$.each(data.datalist['mem.memused.percent'],function(key, value){
		    		x=key*1000;
			        y= value;
			        series.addPoint([x, y], true, true);
			    	activeLastPointToolip(chart)
			    	return;
		    	});
			}else if(type=='disk'){
				$.each(data.datalist['df.bytes.used.percent'],function(key, value){
		    		x=key*1000;
			        y= value;
			        series.addPoint([x, y], true, true);
			    	activeLastPointToolip(chart)
			    	return;
		    	});
			}
	    })
//	}
	
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
                }, 10000);
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
                }, 10000);
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
                }, 10000);
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
