/**
 * 登录页脚本
 */
var imagePath = $('meta[name=contexPath]').attr('content')+'/template/login/'+$('meta[name=cssTemplate]').attr('content')+'/image/';
var htmlPath = $('meta[name=contexPath]').attr('content')+'/template/login/'+$('meta[name=cssTemplate]').attr('content')+'/html/';
var loginFooter = (function() {

	/**
	 * 初始化底部
	 */
	function setFoot(foodCon) {
		
		//底部连接
		var linksHtml = '';
		$.map(foodCon.links, function(v){
			linksHtml += '<a href="' + v.href + '"><li><img src="' + imagePath + v.img + '">' + v.name + '</li></a>';
		});
		$('#bottomLink').append(linksHtml);

		//底部信息
		var messageHtml = '';
		$.map(foodCon.message, function(v){
			messageHtml += '<label>' + (v.img ? '<img src="' + imagePath + v.img + '" />' : '') + v.text + (v.tilt ? '<label>' + v.tilt + '</label>' : '') + '</label>';
		});
		$('#bottomMessage').append(messageHtml);

	}

	function initPage(options) {

		//初始化底部
		setFoot(options)

	}
	return {
		init: initPage
	};

})();