/**
 * 登录页脚本
 */

//判断是否为手机访问
var os = function (){
	var ua = navigator.userAgent,
	isWindowsPhone = /(?:Windows Phone)/.test(ua),
	isSymbian = /(?:SymbianOS)/.test(ua) || isWindowsPhone,
	isAndroid = /(?:Android)/.test(ua),
	isFireFox = /(?:Firefox)/.test(ua),
	isChrome = /(?:Chrome|CriOS)/.test(ua),
	isTablet = /(?:iPad|PlayBook)/.test(ua) || (isAndroid && !/(?:Mobile)/.test(ua)) || (isFireFox && /(?:Tablet)/.test(ua)),
	isPhone = /(?:iPhone)/.test(ua) && !isTablet,
	isPc = !isPhone && !isAndroid && !isSymbian;
	return {
		isTablet: isTablet,
		isPhone: isPhone,
		isAndroid: isAndroid,
		isPc: isPc
	};	
}();

if (os.isAndroid || os.isPhone) {   
  //alert("手机" );
  window.top.location.href=$('meta[name=contexPath]').attr('content')+"/common/mobile/app.html";
} else if (os.isTablet) {
    //alert("平板" );
    window.top.location.href=$('meta[name=contexPath]').attr('content')+"/common/mobile/app.html";
} else if (os.isPc) {
   // alert("电脑" );
}

var imagePath = $('meta[name=contexPath]').attr('content')+'/template/login/'+$('meta[name=cssTemplate]').attr('content')+'/image/';
var htmlPath = $('meta[name=contexPath]').attr('content')+'/template/login/'+$('meta[name=cssTemplate]').attr('content')+'/html/';
var login = (function() {

	/**
	 * 初始化登录类型
	 */
	function loginType(loginName, loginTypes) {
		var html = '<ul>';
		$.each(loginTypes, function(k , v){
			html += '<li id="'+ k +'"><img  src="'+ imagePath +'icon_0bg_1x1.png" /><div><h2>'+ loginName[k].name +'</h2>';
			$.map(v, function(val){
				if(!loginName[k].type[val]) return;
				html += '<a href="' + ( loginName[k].url == "#" ? '#' : (htmlPath + loginName[k].url + '?type=' + val + '&allType=' + v.join() ) ) +'">'+ loginName[k].type[val].name +'</a>';
			});
			html += '</div></li>';
		});
		html += '</ul>';
		$('#content').append(html);
	}

	/**
	 * 初始化页面
	 * @param {Object} options 初始化参数
	 * 
	 * @param {Array} options.setTypeName 登录类型配置
	 */
	function initPage(options) {

		//初始化登录方式
		loginType(options.loginName, options.loginTypes);

		//加载自定义处理
		if(window.pageOnload) pageOnload();
	}
	return {
		init: initPage
	};

})();