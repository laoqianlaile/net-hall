/*
 * Web Front Lib
 * web前端通用脚本库
 * Copyright 华信永道. All rights reserved.
 * @author: 万隆
 * @version: 1.1.0
 */

/*
 *请先引入wfl.base.js
 */

/****************************************************************
 * @namespace wfl.custom
 */
 wfl.custom = wfl.custom || {};

/**
 * 加入浏览器收藏夹
 * @name wfl.custom.addFavorite
 * @function
 * @grammar wfl.custom.addFavorite(pageUrl, pageTitle)
 * @param {String} pageUrl 需要加入收藏夹的url地址
 * @param (String) pageTitle 需要加入收藏夹的名称

 */
wfl.custom.addFavorite = wfl.addFavorite = function(pageUrl, pageTitle){
	try{
		window.external.addFavorite(pageUrl, pageTitle);
	}catch(err){
		try{
			window.sidebar.addPanel(pageTitle, pageUrl, "");
		}catch(err){
			try{
				//chrome不支持自动加入收藏, 创建快捷方式来替代
				createShortcut();
			}catch(err){
				alert("加入收藏失败，请使用Ctrl+D进行添加!");
			}
		}
	}
};

/**
 * 设为首页
 * @name wfl.custom.setHomepage
 * @function
 * @grammar wfl.custom.setHomepage(pageUrl)
 * @param {String} pageUrl 需要设为首页的url地址
 */
wfl.custom.setHomepage = wfl.setHomepage = function(pageUrl){
	try{
		document.body.style.behavior='url(#default#homepage)';
		document.body.setHomePage(pageUrl);
	}catch(err){
		try{
			netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
        }catch(err){
			try{
				var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch);
				 prefs.setCharPref('browser.startup.homepage', pageUrl);
			}catch(err){
				alert("您的浏览器不支持设为首页操作!");
			}
		}
	}
}

/**
 * 拷贝到剪贴板
 * @name wfl.custom.copyToClipboard
 * @function
 * @grammar wfl.custom.copyToClipboard(txt)
 * @param {String} txt 需要拷贝的字符串文本
 */
wfl.custom.copyToClipboard = wfl.copyToClipboard = function(txt){
	if(typeof txt != 'string'){
		return false;
	}
	if (window.clipboardData){
		window.clipboardData.setData("Text", txt);
	}else if (window.netscape){
		try{
			netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
		}catch(e){
			alert("该浏览器不支持一键复制！请手工复制文本框链接地址～");
		}
		var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
		if (!clip) {
			return false;
		}
		var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
		if (!trans) {
			return false;
		}
		trans.addDataFlavor('text/unicode');
		var str = new Object();
		var len = new Object();
		var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
		var copytext = txt;
		str.data = copytext;
		trans.setTransferData("text/unicode",str,copytext.length*2);
		var clipid = Components.interfaces.nsIClipboard;
		if (!clip){
			return false;
		}
		clip.setData(trans,null,clipid.kGlobalClipboard);
	}
}

/**
 * 内容块打印
 * @name wfl.custom.print
 * @function
 * @grammar wfl.custom.print(printArea)
 * @param {String} printArea 内容块ID属性名称,若为空或找不到该元素,则打印当前页面
 */
wfl.custom.print = wfl.print = function(printArea){	
	printArea = printArea ? ($("#" + printArea).length > 0 ? $("#" + printArea) : $('body')) : $('body');
	$('body').html(printArea.html()); 
	window.print(); 
}

/**
 * 图片无缝滚动
 * @name wfl.custom.imgScroll
 * @function
 * @grammar wfl.custom.imgScroll(element, direction, duration)
 * @param {String|jQuery} element 元素
 * @param {String} direction 滚动方向, 可选值有两个 垂直方向:'vertical' , 水平方向:'horizontal',默认为水平方向
 * @param {Number} duration 滚动速度,数值越小滚动越快,默认为30
 * @remark 该方法要求element的HTML结构必须严格按照以下结构才能生效:
		<div id="">
			<div>
				<table border="0" cellspacing="0" cellpadding="0">
					<tbody>
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tbody>
										<tr>
											<td></td>
										</tr>
									</tbody>
								</table>
							</td>
							<td></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
 */
wfl.custom.imgScroll = wfl.imgScroll = function(element, direction, duration){
	if(!element) return false;
	element = element.jquery ? element : $("#" + element);
	if(element.length === 0) return false;
	direction = direction === 'vertical' ? 'vertical' : 'horizontal';
	duration = wfl.isNumber(duration) ? Math.abs(duration) : 30;
	try{
		var wrap_table = element.children("div").children("table");
		var wrap_table_td = wrap_table.children("tbody").children("tr").children("td");
		wrap_table_td.eq(1).html(wrap_table_td.eq(0).html());
		var imgScrollEle = window.setInterval(imgScrollAnimate, duration);		
		element.on("mouseover", function(){
			window.clearInterval(imgScrollEle);
		}).on("mouseleave", function(){			
			imgScrollEle = window.setInterval(imgScrollAnimate, duration);
		});
	}catch(exp){
		return false;
	}
	function imgScrollAnimate(){
		var $this = element.children("div");
		var width = $this.children("table").children("tbody").children("tr").children("td").eq(0).width();
		var height = $this.children("table").children("tbody").children("tr").children("td").eq(0).height();
		if(direction === "vertical"){		
			if($this.scrollTop() >= height){
				$this.scrollTop(0);
			}else{
				$this.scrollTop($this.scrollTop() + 1);
			}
		}else{
			if($this.scrollLeft() >= width){
				$this.scrollLeft(0);
			}else{
				$this.scrollLeft($this.scrollLeft() + 1);
			}
		}
	}
}

/**
 * 引入外部SWF文件
 * @name wfl.custom.getSWF
 * @function
 * @grammar wfl.custom.getSWF(file, sWidth, sHeight)
 * @param {String} file 需要引入的SWF文件地址
 * @param {Number} sWidth SWF文件页面显示宽度
 * @param {Number} sHeight SWF文件页面显示高度

 * @return {String} 返回字符串
 */
wfl.custom.getSWF = wfl.getSWF = function(file, sWidth, sHeight){	
	var swfHTML = "", sWidth = sWidth + "px", sHeight = sHeight + "px";
	swfHTML += '<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" width="'+ sWidth +'" height="'+ sHeight +'">';
	swfHTML += '<param name="movie" value="'+ file +'"><param name="quality" value="high">';
	swfHTML += '<param name="menu" value="false"><param name=wmode value="Transparent">';	
	swfHTML += '<embed src="'+ file +'" wmode="Transparent" menu="false" quality="high" width="'+ sWidth +'" height="'+ sHeight +'" type="application/x-shockwave-flash" />'; 
	swfHTML += '</object>';
	return swfHTML;	
}

/****************************************************************
 * 一些常用的校验方法
 * @namespace wfl.validate
 */
 wfl.validate = wfl.validate || {};

/**
 * 判断是否是正确的Email地址

 */


/****************************************************************
 * 一些和页面有关的方法
 * @namespace wfl.page
 */
wfl.page = wfl.page || {};

/**
 * 内容块随滚动条浮动
 * @name wfl.page.scrollAnimate
 * @function
 * @grammar wfl.page.scrollAnimate(element, cssStyle, animateConfig)
 * @param {jQuery} element 元素, jQuery对象
 * @param {Object} cssStyle 样式对象
 * @param {Object} animateConfig animate方法配置对象
 */
wfl.page.scrollAnimate = wfl.scrollAnimate = function(element, cssStyle, animateConfig){
	if(!element) return false;
	cssStyle ? element.css(cssStyle) : element.css({position: 'absolute', top: '0px', left: '0px'});
	var d_offsetTop = element.offset().top || 0;
	animateConfig = animateConfig || {duration: 300};
	$(window).scroll(function(){
		var t_offsetTop = d_offsetTop + $(window).scrollTop();
		element.animate({
			top : t_offsetTop
		}, animateConfig);
	});
}
