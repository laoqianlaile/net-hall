(function(loginCommon, $) {
  'use strict';

	//阻止浏览器的返回按钮导致页面退回到奇怪的地方
	history.pushState(null, null, document.URL);
	window.addEventListener("popstate", function(e) {
		parent.location.reload();
	}, false);
	
 //刷新验证码图片方法
 loginCommon.refresh = function(url){
	 $.ajax({
		type: 'POST',
		url: url,
		data: {},
		dataType: 'json',
		success: function(data){
			if(data["returnCode"]==0){
	             $('#t_yzm')[0].src=data["image"];
	             $('#yzmkey')[0].value=data["key"];
	         }
		}
	});
 }  
 
 // 登录基础参数
 var login_options={
    url:"" , // 地址
    data:{}, // 请求数据
    before:function(){},// 请求之前校验函数
    fail:function(reason){}, // 失败回调函数
 };
 //登录方法
 loginCommon.login = function(options,data){
	 // 请求前校验处理
	 if(options.before &&  typeof options.before == "function") {
		var flag = options.before.call();
		if(!flag){
			return false;
		}
	 }
	 
	 // 将登录按钮设为禁止状态
	 var loginButtonId = options.loginButtonId || 'buSubmit';
	 loginCommon.buttonAble(loginButtonId, 'disable');
	 
	 $.ajax({
         type: "POST",
         dataType: "json",
         url: options.url ,
         data: data,
         success: function (result) {
        	 // 登录成功会自动跳转成功页面
         	if(result.url !="" && result.url !=null && result.url !="undefined"){
         		window.parent.location.href=result.url;
         	}else{
         		//失败则回调失败函数
         		console.log("登录失败："+result.message);
         		options.fail.call(result,result.returnCode,result.message);
         		
         		//解除按钮禁用状态
         		loginCommon.buttonAble(loginButtonId, 'able');
         	}
         },
         error : function(XMLHttpRequest, textStatus, errorThrown) {
         	 // 状态码
             console.log(XMLHttpRequest.status);
             // 状态
             console.log(XMLHttpRequest.readyState);
             // 错误信息   
             console.log(textStatus);
             options.fail.call(XMLHttpRequest,9999,'系统繁忙，请稍后再试');
             //解除按钮禁用状态
             loginCommon.buttonAble(loginButtonId, 'able');
         }
     });
 }  

 /**	设置按钮状态，
  * @param {String} buttonId 按钮的id
  * @param {String} able 'able'或默认为可用，'disable'为不可用
  */
 loginCommon.buttonAble = function(buttonId, able){
	 var able =  able || 'able';
	 var $button = $('#'+ buttonId);
	 if(able == 'able') $button.removeAttr('disabled');
	 else $button.attr('disabled', 'disabled');
 }
})(
  window.loginCommon ? loginCommon : window.loginCommon = {},
  jQuery
);