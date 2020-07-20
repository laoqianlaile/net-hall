$(function() {
	/* 快捷菜单加载 */
	ydl.ajax(ydl.contexPath + '/menuInfo/selectShortcutMenu', {}, function(data) {
			var shortcutMenuHtml = '';
			$.each(data, function(key, result) { //每一行
				var menuName = result.name || '';
				var menuId = result.id || '';
				var menuUrl = result.url || '';
				var menuSign = result.sign || '';
				shortcutMenuHtml += '' +
					'<div  class="menu-tab col-xs-2 col-sm-2 .col-md-2 .col-lg-2 col-custom">' +
					'<div  data-id=' + menuId + '>' +
					'<span data-url=' + menuUrl + ' class="ydico-base ydico-base-' + menuSign + '"></span>' +
					'<p>' + menuName + '</p>' +
					'</div>' +
					'</div>';
			});
			$('.shortcut-menu').append(shortcutMenuHtml);
	});

	/* 公告列表加载 */
	//处理比较无奈的公告信息获取加载顺序问题（第一次进入页面之后获取到的为字符串……刷新之后才是正常的object）
	var noticeInfo = (typeof pageData['noticeInfo']) == "string" ? JSON.parse(pageData['noticeInfo']) : pageData['noticeInfo'] ;
	notice(noticeInfo);
	function notice(data) {
		var noticeHtml = '';
		$.each(data.data, function(key, result) { //每一行
			var noticeId = result.seqno || '';
			var noticeTitle = result.title || '';
			var noticeUrl = result.url || '';
			var noticeTime = result.time || '';
			noticeHtml += '' +
				'<li class="notice-list" data-id=' + noticeId + '>' +
				'<span data-url=' + noticeUrl + '>' +
				'<img src="' + ydl.contexPath + '/ydpx/ish02/home/image/icon_news_green.png">' +
				'<a class="dialog-link dialog-scroll" href="javascript:void(0);">' + noticeTitle + '</a>' +
				'</span>' +
				'<span class="time">' + noticeTime + '</span>' +
				'</li>';
		});
		$('.notice ul').append(noticeHtml);
	}

	/* 消息提醒加载 */
	ydl.ajax(ydl.contexPath + '/message/queryMessage',{}, function(data) {
			var messageHtml = '';
			$.each(data, function(key, result) { //每一行	
				var messageId = result.messageid || '';
				var messageTitle = result.title || '';
				var messageUrl = result.url || '';
				var messageTime = result.time || '';
				var messageContent = result.message;
				var messageStat = result.stat;
				messageHtml += '' +
					'<li class="message-list" data-id=' + messageId + '>' +
					'<span>' +
					'<img src="' + ydl.contexPath + '/ydpx/ish02/home/image/icon_messages_red.png">' +
					'<a class="dialog-link" href="javascript:void(0);" data-message="' + messageContent + '" data-stat="' + messageStat + '">' + messageTitle + '</a>' +
					'</span>' +
					'<span class="time">' + messageTime + '</span>' +
					'</li>';
			});
			$('.message ul').append(messageHtml);
	});

	/* 快捷菜单点击事件 */
	$('.shortcut-menu').on('click', '.menu-tab', function(e) {
		//var id = this.children[0].getAttribute('data-id');menu-tab-id
		var url = this.children[0].children[0].getAttribute('data-url');
		var dataId = this.children[0].getAttribute('data-id');
		ydl.frame.add((ydl.contexPath + url),  {'dataId':dataId});
	});

	/* 公告列表点击事件 */
	$('#homeNotice').on('click', '.notice-list', function(){
		var $this = $(this).find('a.dialog-link');
		//点击弹出显示 消息、任务
		ydl.displayRunning(true);
		//格式为 / 开头 或 http 开头，支持流程和ydpx页面
		//var linkUrl = '/flow/menu/XXTX';
		//var linkUrl = '/ydpx/parsepage?$page=common_xxtx_01.ydpx'
		//var linkUrl = '/ish/flow/menu/WFXTGGCK01?id=16'
		var linkUrl = $this.closest('span').attr('data-url');
		//链接带根目录时
		var regex = new RegExp('^' + ydl.contexPath + '');
		if (regex.test(linkUrl)) linkUrl = linkUrl.replace(regex,'');
		var paras = $this.hasClass('dialog-scroll') ? {dialogVersion: 'scroll'} : {};
		//打开页面对话框
		ydl.subPage(linkUrl,paras).fail(function(){
			ydl.log('获取内容失败');
			location.href = ydl.contexPath + linkUrl;
		}).always(function(){
			ydl.displayRunning(false);
		});
	}).on('click', '#listMore', function(e) {
		/* 公告列表更多按钮点击事件 */
		var $this = $(this);
		var dataId = $this.attr('data-id');
		var url = ydl.contexPath + '/flow/menu/' +$this.attr('data-flow');
		ydl.frame.add(url, {'dataId':dataId});
	});

	/* 消息列表点击事件 */
	$('#homeMessage').on('click', '.message-list', function(){
		var $this = $(this).find('a.dialog-link');
		ydl.customDialog({'message': $this.html(),
											'desc': $this.attr('data-message'),
											'buttons': [{'text':'确定', 'id':'messageConfirm'}],
											'open': function(){
												if($this.attr('data-stat') == '0'){
													//如果是未读的消息，标记为已读
													ydl.ajax(ydl.contexPath + '/message/updateMessage',{'messageid': $this.closest('li').attr('data-id')}, function() {});
												}
											}
										});
	}).on('click', '#messageMore', function(e) {
		/* 消息列表列表更多按钮点击事件 */
		var $this = $(this);
		var dataId = $this.attr('data-id');
		var url = ydl.contexPath + '/flow/menu/' +$this.attr('data-flow');
		ydl.frame.add(url, {'dataId':dataId});
	});
})
