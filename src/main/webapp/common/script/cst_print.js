/*********************\ 
 * Description: 新版凭证打印前端处理 
 * Author: zhc 
 * Date: 2018-01-17 * \
 ************************/
'use strict';

var print = {};

(function(){
	var options = {
        key: '',//打印模板对应的poolKey,必传,后台调用yDVoucherUtil.saveWordVoucher(dataPool, "YYSQ01");的返回值
        type: '',//打印文件的类型,必传,例如：word、excel或pdf
        name: '',//实现IYDVoucher接口的实现类名称,必传,例如： WordYYSQ01 implements IYDVoucher 中的YYSQ01
        id: '',//打印文件的显示页面元素id,必传
        data: {},//需要保存的业务关键字,必传,凭证补打查询时做查询条件
        fileid: '',
        width: '100%',//打印文件的宽度默认100% 可以是百分数也可以指定大小100px
        height: '100%',//打印文件的高度默认100% 可以是百分数也可以指定大小100px
        style: '',//指定凭证页面的CSS样式，非必传
        reprt: '',//是否重打 0-否，1-是，非必传,默认为1
        callback:null,// 回调函数，非必传
    };
	
	//展示打印模板
	function prt(options){
		var width=options.width?options.width:'100%';
		var height=options.height?options.height:'100%';
		var url=location.protocol+"//"+location.host+ydl.contexPath+"/view/ydvoucher/"+options.key
			+"?modeltype="+options.type+"&modelName="+options.name;  
		if(options.style != '' && options.style != null){
			var html= "<iframe width='"+width+"' id='printFrame' height='"+height+"' style='"+options.style+"' src='"+url+"'  frameborder='0' scrolling='yes'></iframe>";
		}else{
			var html= "<iframe width='"+width+"' id='printFrame' height='"+height+"' src='"+url+"' frameborder='0' scrolling='yes'></iframe>";
		}
		if(options.id == '' || options.id == null){
		   	$("#page_tabs .tab-content").append("<div id='printDiv'></div>");
			$("#printDiv").html(html);
		}else{
			$("#"+options.id).html(html);
		}
		if ($.isFunction(options.callback)) {
			options.callback.call();
		}
	}
	
/**
 * 初始化凭证打印信息
 * 
 * @param {Object}
 */
print.initPrintPage = function(options) {
	if(options.key == '' || options.key == null){
        console.log("打印模板key不能为空");
        alert("模板key不能为空");
        return false;
	}
	if(options.type == '' || options.type == null){
		console.log("打印类型不能为空");
		alert("打印类型不能为空");
		return false;
	}
	if(options.name == '' || options.name == null){
		console.log("打印实现类名称不能为空");
		alert("打印实现类名称不能为空");
		return false;
	}
	if(options.data == '' || options.data == null){
		console.log("业务关键字不能为空");
		alert("业务关键字不能为空");
		return false;
	}
    var ywgjz = options.data?options.data:{};
    // 请求参数
    var param={};
    param["_POOLKEY"]=poolSelect["_POOLKEY"];
    param["fileid"]=options.key;
    param["filetype"]=options.type;
    param["filename"]=options.name;
    // 组装业务关键字默认最多传8个
    var a = 1;
    	$.each(ywgjz, function(i) {
    		if(a<=8){
	    		param["ywgjz"+a]=ywgjz[i];
	    		a = a+1;
    		}
    	});
    param["ywgjznum"]=a-1;
    
    //若不需要重打直接展示打印模板
    if(options.reprt == '0'){
    	prt(options);
    }
    // 调用后台凭证信息保存接口
    else{
    	ydl.ajax(ydl.contexPath + "/print/save", param, function(data) {
    		prt(options);
    	});
    }
}
/**
 * 凭证打印,弹出打印窗口
 * 
 * @param {Object}
 */
print.print = function(options) {
	// 弹出打印窗口
	window.frames[0].print();
}
/**
 * 凭证补打
 * 
 * @param {Object}
 */
print.rePrintPage = function(options) {
	if(options.type == '' || options.type == null){
		console.log("打印类型不能为空");
		alert("打印类型不能为空");
		return false;
	}
	if((options.data == '' || options.data == null) && (options.fileid == '' || options.fileid == null)){
		console.log("业务关键字与fileid不能同时为空");
		alert("业务关键字与fileid不能同时为空");
		return false;
	}

	var width=options.width?options.width:'100%';
	var height=options.height?options.height:'100%';
	
    var ywgjz = options.data?options.data:{};
    // 请求参数
    var param={};
    if(options.data == '' || options.data == null){
		param["fileid"]=options.fileid;
	}
    param["_POOLKEY"]=poolSelect["_POOLKEY"];
    param["filetype"]=options.type;
    // 组装业务关键字默认最多传8个，作为查询凭证信息的条件。
    var a = 1;
    	$.each(ywgjz, function(i) {
    		if(a<=8){
	    		param["ywgjz"+a]=ywgjz[i];
	    		a = a+1;
    		}
    	});
    param["ywgjznum"]=a-1;
    // 调用后台凭证信息查询接口，并修改打印次数
    ydl.ajax(ydl.contexPath + "/print/select", param, function(data) {
		var url=location.protocol+"//"+location.host+ydl.contexPath+"/view/ydvoucher/"+data.key+"?modeltype="+options.type;
		if(options.style != '' && options.style != null){
    		var html= "<iframe width='"+width+"' id='printFrame' height='"+height+"' style='"+options.style+"' src='"+url+"'  frameborder='0' scrolling='yes'></iframe>";
    	}else{
    		var html= "<iframe width='"+width+"' id='printFrame' height='"+height+"' src='"+url+"' frameborder='0' scrolling='yes'></iframe>";
    	}
        if(options.id == '' || options.id == null){
        	$("#page_tabs .tab-content").append("<div id='printDiv'></div>");
    		$("#printDiv").html(html);
    	}else{
    		$("#"+options.id).html(html);
    	}
        if ($.isFunction(options.callback)) {
			options.callback.call();
		}
	});
    
}
})();


