/**
 * 登录页脚本
 */
var imagePath = $('meta[name=contexPath]').attr('content')+'/template/login/'+$('meta[name=cssTemplate]').attr('content')+'/image/';
var loginHeader = (function() {

	/**
	 * 初始化头部
	 */
	function setHead(info) {
		if(info.img) $('#head img').attr('src', imagePath + info.img);
	}

	function initPage(options) {
		//初始化头部
		setHead(options);
	}
	return {
		init: initPage
	};

})();