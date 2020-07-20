/**
 * 登录页脚本
 */
var login = (function() {
	function setBodyHeight() {
		var height = $(window).height();
		$('.login-body,.login-type').height(Math.max(height - $('.login-header').height(), 600 - $('.login-header').height()));
		//$('.login-bg').height(height);
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
			html += '<li><img src="'+imagePath + foodCon.QRCode[k].img + '"/><span>' + foodCon.QRCode[k].name + '</span></li>'
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
			var typeName = $loginType.data('typeName');
			//隐藏上次打开的登录类型
			var $directly = $loginType.find('.hover-directly');
			var oldEle = $('.login-type-mold').data('oldEle');
			if (oldEle != undefined && typeName != oldEle.parent().data('typeName')) {
				//console.log('list-ul mouseenter loginTypeHide')
				loginTypeHide(oldEle);
			}
			$('.login-type-mold').data('oldEle',$directly);
			//显示当前用户的登录类型
			$loginType.addClass(typeName);
			//console.log('list-ul mouseenter loginTypeShow')
			loginTypeShow($loginType);
			e.stopPropagation();
		});
		//离开登录类型遮罩，依次隐藏登录类型
		$('.hover-directly').mouseleave(function() {
			//console.log('hover-directly mouseleave loginTypeHide')
			var $directly = $(this);
			loginTypeHide($directly)
		});
		//指定用户类型 依次隐藏登录类型
		function loginTypeHide($directly){
			var $loginType = $directly.parent();
			$loginType.removeClass($loginType.data('typeName'));
			$loginType.find('li').each(function(k, v) {
				(function(v, k) {
					setTimeout(function() {
						$loginType.children('.type').eq(k).hide();
						$loginType.find('li').eq(k).hide();
					}, k * 100);
				})(v, k);
			})
		}
		function loginTypeShow($loginType){
			$loginType.find('li').each(function(k, v) {
				(function(v, k) {
					setTimeout(function() {
						$loginType.children('.type').eq(k).show();
						$loginType.find('li').eq(k).show();
					}, k * 100);
				})(v, k);
			});
		}
		//点击登录类型，显示iframe
		$('.list-ul').on('click','li',function(e){
			var $loginType = $(this).closest('.login-type-btn');
			$loginType.removeClass($loginType.data('typeName'))
			var frameUrl = $('.login-iframe>iframe')[0].src;
			$('.login-iframe>iframe').attr('src', frameUrl.replace(/login\d+\.jsp/,$(this).attr('url')));
			$('.login-bg').addClass('top');
			$('.login-main').addClass('login-main-down');
			$('.login-enter').addClass('bottom-0');
			e.stopPropagation();
		})
		//进入 登录类型 阻止隐藏
		.on('mouseenter','li',function(e){
			//console.log('li mouseenter loginTypeShow')
			var $loginType = $(this).closest('.login-type-btn');
			$loginType.addClass($loginType.data('typeName'));
			loginTypeShow($loginType);
			e.stopPropagation();
		})
		//离开 登录类型 依次隐藏本用户类型 的所有 登录类型
		.on('mouseleave','li', function(){
			//console.log('li mouseenter loginTypeHide')
			loginTypeHide($(this).closest('.login-type-btn').children('.hover-directly'));
		});

		//返回
		$('.login-r-out').click(function() {
			$('.login-bg').removeClass('top');
			$('.login-main').removeClass('login-main-down');
			$('.login-enter').removeClass('bottom-0');
			for(var i=0;i<$('.list-ul').length;i++){
				var parentName = $('.list-ul').eq(i).parent().data('typeName');
				$('.' + parentName + '').removeClass(parentName);
			}
			$('.list-ul').parent().children('.type').hide();
			$('.list-ul li').css({'z-index': '0'}).hide();
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
	 */
	function initPage(options) {
		setBodyHeight();
		//初始化头部相关链接
		setHead(options.setHead);
		// //初始化大背景图片
		bgbanner(options.bgBanner);
		// //初始化登录方式
		loginType(options.loginTypes, options.setTypeName);
		// //初始化相关链接
		initLinks(options.links || {});
		// //交互事件处理
		attachEvents();
		// //初始化底部
		setFoot(options.foot)
		//加载自定义处理
		if(window.pageOnload) pageOnload();
	}

	return {
		init: initPage
	};

})();