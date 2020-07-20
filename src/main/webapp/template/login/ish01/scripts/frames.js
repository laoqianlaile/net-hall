$(function () {
	$("#dateEle").text(ydl.formatDate(new Date(), "yyyy年MM月dd日 星期E"));
	//初始化一级菜单
	var $menu1 = $('#nav_bar ul');
	var pidObj = {};
	for (var i = 0; i < menu.length; i++) {
		if (menu[i].pid === '$$$$$$$$') {
			$menu1.append('<li><a id="' + menu[i].id + '" href="' + menu[i].url + '">' + menu[i].name + '</a></li>');
		}
		else pidObj[menu[i].pid] = 1;
	}
	//是否隐藏一级菜单
	$.each($('#nav_bar ul a'), function(index, ele) { 
	     if(!pidObj[ele.id]  && $(this).attr('href') == '') $(this).parent().hide();
	});

	//点击一级菜单，显示二级菜单或链接内容
	var $menu2 = $('#left_menu ul');
	$menu1.on('click', 'a', function () {
		$menu2.empty();
		$('li', $menu1).removeClass('selected');
		for (var i = 0; i < menu.length; i++) {
			if (menu[i].pid === this.id) {
				$menu2.append('<li><a href="' + menu[i].url + '">' + menu[i].name + '</a></li>');
			}
		}
		if ($menu2.is(':empty')) $menu2.append('<li><a href="' + $(this).attr('href') + '">' + $(this).text() + '</a></li>');
		document.getElementById('main_frame').contentWindow.location.replace($(this).attr('href') == '' ? ydl.contexPath + '/platform/welcome.jsp?openval=1' : this.href);
		$(this).parent().addClass('selected');
		return false;
	});
	$('a:first', $menu1).click();
	
	//点击二级菜单，打开链接
	$menu2.on('click', 'a', function () {
		if ($(this).attr('href')) document.getElementById('main_frame').contentWindow.location.replace(this.href);
		return false;
	});
	
	//内容加载完毕后更新框架高度
	$('#main_frame').load(function () {
		try {
			this.contentWindow.document.body.style.margin = '0';
			pageResize();
		}
		catch (ex) {
			ydl.log('更新框架高度失败！' + ex.message);
		}
	});
	
	//退出登录
	$('#logout').click(function () {
		document.getElementById('logoutForm').submit();
		return false;
	});
});

//重设页面元素大小 
function pageResize() {
	var left_height = ($('#left_menu ul').get(0).offsetHeight + 53) + 'px';
	var frame_body = document.getElementById('main_frame').contentWindow.document.body;
	var main_height = (frame_body.offsetHeight + 20) + 'px';
	document.getElementById('main_container').style.height = (parseInt(left_height) > parseInt(main_height) || frame_body.children.length == 0) ?  left_height : main_height;
	//document.getElementById('main_container').style.height = document.getElementById('main_frame').contentWindow.document.body.clientHeight + 'px';
}

//设为首页
function setHomepage(pageUrl){
//	var pageUrl = "http://localhost:8080/bswsyyt";
	try {
		document.body.style.behavior='url(#default#homepage)';
		document.body.setHomePage(pageUrl);
	}
	catch (err) {
		try {
			netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
        }
		catch (err) {
			try {
				var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch);
				 prefs.setCharPref('browser.startup.homepage', pageUrl);
			}
			catch (err) {
				alert('您的浏览器不支持设为首页操作!');
			}
		}
	}
}


//添加收藏
function addFavorite(pageUrl, pageTitle) {
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
String.prototype.endWith=function(str){
	if(str==null||str==""||this.length==0||str.length>this.length)
	  return false;
	if(this.substring(this.length-str.length)==str)
	  return true;
	else
	  return false;
	return true;
	}
