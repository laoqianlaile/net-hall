$(function () {
	$("#dateEle").text(ydl.formatDate(new Date(), "yyyy��MM��dd�� ����E"));
	//��ʼ��һ���˵�
	var $menu1 = $('#nav_bar ul');
	var pidObj = {};
	for (var i = 0; i < menu.length; i++) {
		if (menu[i].pid === '$$$$$$$$') {
			$menu1.append('<li><a id="' + menu[i].id + '" href="' + menu[i].url + '">' + menu[i].name + '</a></li>');
		}
		else pidObj[menu[i].pid] = 1;
	}
	//�Ƿ�����һ���˵�
	$.each($('#nav_bar ul a'), function(index, ele) { 
	     if(!pidObj[ele.id]  && $(this).attr('href') == '') $(this).parent().hide();
	});

	//���һ���˵�����ʾ�����˵�����������
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
	
	//��������˵���������
	$menu2.on('click', 'a', function () {
		if ($(this).attr('href')) document.getElementById('main_frame').contentWindow.location.replace(this.href);
		return false;
	});
	
	//���ݼ�����Ϻ���¿�ܸ߶�
	$('#main_frame').load(function () {
		try {
			this.contentWindow.document.body.style.margin = '0';
			pageResize();
		}
		catch (ex) {
			ydl.log('���¿�ܸ߶�ʧ�ܣ�' + ex.message);
		}
	});
	
	//�˳���¼
	$('#logout').click(function () {
		document.getElementById('logoutForm').submit();
		return false;
	});
});

//����ҳ��Ԫ�ش�С 
function pageResize() {
	var left_height = ($('#left_menu ul').get(0).offsetHeight + 53) + 'px';
	var frame_body = document.getElementById('main_frame').contentWindow.document.body;
	var main_height = (frame_body.offsetHeight + 20) + 'px';
	document.getElementById('main_container').style.height = (parseInt(left_height) > parseInt(main_height) || frame_body.children.length == 0) ?  left_height : main_height;
	//document.getElementById('main_container').style.height = document.getElementById('main_frame').contentWindow.document.body.clientHeight + 'px';
}

//��Ϊ��ҳ
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
				alert('�����������֧����Ϊ��ҳ����!');
			}
		}
	}
}


//����ղ�
function addFavorite(pageUrl, pageTitle) {
	try{
		window.external.addFavorite(pageUrl, pageTitle);
	}catch(err){
		try{
			window.sidebar.addPanel(pageTitle, pageUrl, "");
		}catch(err){
			try{
				//chrome��֧���Զ������ղ�, ������ݷ�ʽ�����
				createShortcut();
			}catch(err){
				alert("�����ղ�ʧ�ܣ���ʹ��Ctrl+D�������!");
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
