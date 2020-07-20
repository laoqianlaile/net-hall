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
  window.location.href=$('meta[name=contexPath]').attr('content')+"/common/mobile/app.html";
} else if (os.isTablet) {
    //alert("平板" );
    window.location.href=$('meta[name=contexPath]').attr('content')+"/common/mobile/app.html";
} else if (os.isPc) {
   // alert("电脑" );
}

var imagePath = $('meta[name=contexPath]').attr('content')+'/template/login/'+$('meta[name=cssTemplate]').attr('content')+'/image/';
var login = (function() {
	function setBodyHeight() {
		var height = $(window).height();
		$('.login-body,.login-type').height(Math.max(height - $('.login-header').height(), 600 - $('.login-header').height()));
	}
	/**
	 * 初始化头部
	 */
	function setHead(info) {
		if(info.img) $('.head-img img').attr('src', imagePath + info.img);
		if(info.headUrl) {
			var html = '';
			$.each(info.headUrl, function(k, v) {
				info.headUrl.length == k + 1 ? html += '<li><a href=' + this.url + '>' + this.name + '</a></li>' : html += '<li><a href=' + this.url + '>' + this.name + '</a></li><li class="kong"></li>';
			});
			$('.head-ul ul').html(html);
		}
	}

	/**
	 * 初始化大背景图片
	 */
	function bgbanner(url) {
		/*
		if(url) $('.login-bg').css({
			'background': 'url('+imagePath + url + ') no-repeat'
		});
		*/
		if (url) $('.login-bg')[0].src = imagePath + url;
	}
	/**
	 * 初始化登录类型
	 */
	function loginType(userType, loginType) {
		var width = 0;
		//循环用户类型
		$.each(userType,function (key, v){
			var ulHtml = '';
			var $userLogin = $('<div class="login-type-btn login-type-' + key + '" data-typeName="login-type-' + key + '-hover">'+	
				//循环每个用户类型下的 登录类型配置
				$.map(v, function(index, i){
					ulHtml+= '<li class="list-' + i + '" url = ' + loginType[key][index].url + '><a><span class="icon-' + index + '"></span>' + loginType[key][index].name + '</a></li>';
					return '<div class="ie-icon-bg' + i + ' icon' + i + '-bg type"></div>';
				}).join('')
			+'<ul class="list-ul">'+ulHtml+'</ul><div class="hover-directly"></div></div>').data('typeName','login-type-' + key + '-hover');
			//添加用户类型html结构
			$('.login-type-mold').append($userLogin);
			width += $('.login-type-btn').outerWidth(true);
		});
		$('.login-type-mold').width(width);
	}

	/**
	 * 是否设置只显示一级菜单
	 * @param {bollean} onlyRootMenu 是否开启，true：开启，false：不开启，默认不开启。
	 */
	function onlyRootMenu(onlyRootMenu) {
		var onlyRootMenu = onlyRootMenu || false;
		//如果没开启功能，直接退出
		if(onlyRootMenu){
			$('.login-type-btn').each(function(){
				var $this = $(this);
				var $thisLi = $this.find('li');
				//如果一级菜单下有不止一个二级菜单或无菜单则不做处理
				if($thisLi.length > 1 || $thisLi.length == 0) return;
				else {
					$this.children('div:not(.hover-directly)').addClass('hide');
					//隐藏二级菜单图标
					$thisLi.addClass('hide');
					//点击一级菜单相当于直接点击下面的第一项二级菜单
					$this.on('click',function(){
						$thisLi.click()
					});
				}
			});
		}else return;
	}

	/**
	 * 初始化内部链接
	 * @param {Object[]} links 链接数据 
	 */
	function initLinks(links) {
		//内部网站链接
		var html = $.map(links.website || [], function(link) {
			var href = link.url ? ' href="' + link.url + '"' : '';
			return '<a target="_blank" ' + href + '>' + link.name + '</a>';
		}).join('');
		if(html) $('.login-footer-link  dd').append(html);
		else $('.login-footer-link dl').addClass('hide');
		//其他链接
		if(links.manual) $('.login-footer-main-2 ul').append('<li><a target="_blank" href="' + links.manual +
			'"><span class="link-manual"></span>用户指南</a></li>');
		if(links.download) $('.login-footer-main-2 ul').append('<li><a target="_blank" href="' + links.download +
			'"><span class="link-download"></span>驱动下载</a></li>');
		if(html == '' && $('.login-info ul').html() == '') $('.login-info').addClass('hide');
		html = '';
		if(links.telno) html += '技术支持电话：<b>' + links.telno + '</b>';
		if(links.about) html += ' <a class="about" href="' + links.about + '">关于系统</a>';
		if(links.feedback) html += ' <a class="feedback" href="' + links.feedback + '">意见反馈</a>';
		$('.login-footer>.info').append(html);
	}

	/**
	 * 初始化底部
	 */
	function setFoot(foodCon) {
		if(foodCon.leftImg) $('.login-footer-content>img').attr('src', imagePath + foodCon.leftImg);
		if(foodCon.phone) $('.login-footer-content-info .phone img').attr('src', imagePath + foodCon.phone);
		if(foodCon.tel) $('.login-footer-content-info .tel span').html(foodCon.tel);
		var html = '';
		$.each(foodCon.QRCode, function(k, v) {
			html += '<li><img src="'+ imagePath + foodCon.QRCode[k].img + '"/><span>' + foodCon.QRCode[k].name + '</span></li>'
		});
		$('.login-footer-main-3 ul').html(html);
	}

	/**
	 * 交互事件处理
	 */
	function attachEvents() {
		//进入登录用户类型图标，依次显示登录类型
		$('.list-ul').mouseenter(function(e) {
			var $loginType = $(this).parent();
			$loginType.addClass($loginType.data('typeName'));
			$loginType.find('li').each(function(k, v) {
				(function(v, k) {
					setTimeout(function() {
						$loginType.children('.type').eq(k).addClass('fadein');
						$loginType.find('li').eq(k).show().addClass('fadein');
					}, k * 100);
				})(v, k);
			});
			$loginType.find('.hover-directly').data('stop',true);
			e.stopPropagation();
		});
		//离开登录类型遮罩，依次隐藏登录类型
		$('.hover-directly').mouseleave(function() {
			var $directly = $(this);
			setTimeout(function(){
				if (!$directly.data('stop')) {
					loginTypeHide($directly)
				}
				$directly.data('stop', false);
			},100);
		});
		//指定用户类型 依次隐藏登录类型
		function loginTypeHide($directly){
			var $loginType = $directly.parent();
			$loginType.removeClass($loginType.data('typeName'));
			$loginType.find('li').each(function(k, v) {
				(function(v, k) {
					setTimeout(function() {
						$loginType.children('.type').eq(k).removeClass('fadein');
						$loginType.find('li').eq(k).removeClass('fadein').hide();
					}, k * 100);
				})(v, k);
			})
		}
		//点击登录类型，显示iframe
		$('.list-ul').on('click','li',function(e){
			var $loginType = $(this).closest('.login-type-btn');
			$loginType.removeClass($loginType.data('typeName'))
			var frameUrl = $('.login-iframe>iframe').attr('src');
			//替换新的Url
			var urlArr = frameUrl.split('/');
			urlArr[urlArr.length-1] = $(this).attr('url');
			frameUrl = urlArr.join('/');
			//console.log(frameUrl)
			$('.login-iframe>iframe').attr('src', frameUrl);
			
			$('.login-bg').addClass('top');
			$('.login-main').addClass('login-main-down');
			$('.login-enter').addClass('bottom-0');
			e.stopPropagation();
		})
		//进入 登录类型 阻止隐藏
		.on('mouseenter','li',function(e){
			$(this).closest('.login-type-btn').children('.hover-directly').data('stop',true);
			e.stopPropagation();
		})
		//离开 登录类型 依次隐藏本用户类型 的所有 登录类型
		.on('mouseleave','li', function(){
			loginTypeHide($(this).closest('.login-type-btn').children('.hover-directly'));
		})
		//返回 按钮
		$('.login-r-out').click(function() {
			$('.login-bg').removeClass('top');
			$('.login-main').removeClass('login-main-down');
			$('.login-enter').removeClass('bottom-0');
		});	
	}

	/**
	 * 初始化页面
	 * @param {Object} options 初始化参数
	 * 
	 * @param {Object} options.setHead 头部信息
	 * @param {Object} options.setHead.img 头部logo图片名字
	 * @param {Object[]} options.setHead.headurl 头部相关链接
	 * @param {String} options.setHead.headurl[].url 头部链接地址
	 * @param {String} options.setHead.headurl[].name 头部链接名字
	 *
	 * @param {Array} options.setTypeName 登录类型配置
	 * @param {Array{}} options.setTypeName.danwei登录类型
	 * @param {String} options.setTypeName.danwei.name 登录类型名字
	 * @param {String} options.setTypeName.danwei.url 登录类型对应链接
	 * 
	 * @param {Object} options.loginTypes 选择登录类型
	 * @param {Array} options.loginTypes.danwei 根据登录类型下表
	 *  
	 * @param {Object} options.links 相关链接
	 * @param {Object[]} options.links.website 内网链接
	 * @param {String} options.links.website[].url 内网链接地址
	 * @param {String} options.links.website[].name 内网链接名称
	 * @param {String} options.links.manual 用户指南
	 * @param {String} options.links.download 驱动下载
	 *
	 * @param {String} options.bgbanner 背景大图
	 * 
	 * @param {Object} options.food 底部配置
	 * @param {String} options.food.leftImg 最左侧图片
	 * @param {String} options.food.iphone 客服电话
	 * @param {String} options.food.telno 支持电话
	 * @param {Object[]} options.food.towCode 二维码配置
	 * @param {String} options.food.towCode[].imgCode 二维码图片
	 * @param {String} options.food.towCode[].name 二维码名字
	 * 
	 * @param {String} options.onlyRootMenu 是否设置只显示一级菜单
	 */
	function initPage(options) {
		setBodyHeight();
		//初始化头部相关链接
		setHead(options.setHead);
		//初始化大背景图片
		bgbanner(options.bgBanner);
		//初始化登录方式
		loginType(options.loginTypes, options.setTypeName);
		//初始化相关链接
		initLinks(options.links || {});
		//交互事件处理
		attachEvents();

		//是否设置只显示一级菜单
		onlyRootMenu(options.onlyRootMenu);

		//初始化底部
		setFoot(options.foot)
		//加载自定义处理
		if(window.pageOnload) pageOnload();
	}
	return {
		init: initPage
	};

})();