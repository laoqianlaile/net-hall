/**
 * 结果页脚本
 * 用于/ydpx/common/end.ydpx
 */

function initPage() {
	/*
	//测试数据
	var poolSelect = {
		_RESULT_PAGE_TYPE: "0",
		_RESULT_PAGE_TITLE: "流程成功完成！",
		_RESULT_PAGE_CONTENT: "申请成功，本笔交易审批号为10023cvzxvxvzxvzxvzxvxzvzxvxzvzxvxvxzvzvzvzxvzvzvzxvzxvxvzxvxzvzvvzvvzvzvzvvzxvzvxzvxzvz<br>姓名·：张三<br>单位：华信永道",
		_RESULT_PAGE_LINKS: JSON.stringify({
			"recent": [],
			"prev": [
				{"name": "联名卡批量办理维护", "url": "flow/menu/PB0001",funcid:''},
				{"name": "批量制卡审核", "url": "flow/menu/PB0001"},
				{"name": "公积金密码设置与修改", "url": "flow/menu/PB0001"},
				{"name": "密码挂失解挂", "url": "flow/menu/PB0001"},
				{"name": "制卡撤销交易恢复", "url": "flow/menu/PB0001"}
			],
			"next": [
				{"name": "联名卡批量办理维护", "url": "flow/menu/PB0001"},
				{"name": "批量制卡审核qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq", "url": "flow/menu/PB0001"},
				{"name": "公积金密码设置与修改", "url": "flow/menu/PB0001"},
				{"name": "密码挂失解挂", "url": "flow/menu/PB0001"},
				{"name": "制卡撤销交易恢复", "url": "flow/menu/PB0001"}
			]
		})
	};
	*/
	
	// 结果页类型，0=成功，1=失败，2=系统错误；缺省为0
	var type = poolSelect._RESULT_PAGE_TYPE || '0';
	
	$('#resultInfo').find('.result-type').addClass('type_' + type);
	
	// 结果页信息标题，此参数可省略，成功时缺省为“业务办理完成！”，失败时缺省为“哎呀...出错了！”
	var title = poolSelect._RESULT_PAGE_TITLE || (type === '0' ? '业务办理完成！' : '哎呀...出错了！');
	$('#resultInfo').find('.result-title').text(title);
	
	// 结果页信息内容（支持HTML格式），此参数可省略，缺省不显示信息内容
	$('#resultInfo').find('.result-content').html(poolSelect._RESULT_PAGE_CONTENT || '');
	
	//结果页按钮
	//系统提供了三个预定义按钮：q（返回前一页）、r（重新执行当前流程）、o（返回首页）
	//如果未设置结果页按钮，缺省只显示 （返回首页）
	//如果超时，只显示（重新登录）
	//如果结果页类型不是成功，显示（复制到剪贴板）
	//可以分别设置显示哪个预定义按钮，也可以新增自定义按钮
	var $buttons = $('#resultInfo').find('.result-buttons');
	$buttons.append($('#page_flow_buttons').children());
	
	if (/错误代码：TIMEOUT/.test(poolSelect._RESULT_PAGE_CONTENT)){
		$buttons.html('<button type="button" id="b_flow_n" class="btn btn-success btn-sm end-button">重新登录</button>');
	}
	else {
		if ($buttons.children().length == 0) {
			if (poolSelect._OPERID != 'false') $('<button type="button" id="b_flow_o" class="btn btn-success btn-sm end-button">返回首页</button>').appendTo($buttons);
			else $('<button type="button" id="b_flow_n" class="btn btn-success btn-sm end-button">重新登录</button>').appendTo($buttons);
		}
		if (type != '0' && $('#b_flow_p').length == 0) {
			$('<button type="button" id="b_flow_p" class="btn btn-default result-copy btn-sm end-button">复制到剪贴板</button>').appendTo($buttons);
		}
	}
	$buttons.find('button').addClass('btn-sm');
	$buttons.find('button.btn-primary').removeClass('btn-primary').addClass('btn-info');
	
	//按钮注册事件 
	//重新登录
	$('body').on('click','#b_flow_n',function (){
		top.location.href = ydl.contexPath;
	});
	//返回首页 
	$('body').on('click','#b_flow_o',function (){
		location.href = ydl.contexPath + '/home';
	});
	
	//复制到剪贴板
	$('#b_flow_p').attr('data-clipboard-text',poolSelect._RESULT_PAGE_COPYINFO ||( 
			$('#resultInfo').find('.result-title').text() + '\n' +
			$('#resultInfo').find('.result-content').text()));
	//如果不是ie8则加载插件
	if(!/MSIE 8/.test(navigator.userAgent)){
		//获取剪贴板插件js文件
		$.getScript(ydl.contexPath +'/common/script/clipboard.min.js',function(){
			//初始化复制信息到剪贴板按钮
			new Clipboard('.result-copy').on('success', function(e) {
				$(e.trigger).text('已复制到剪贴板');
			});
		});	
	}
	//如果是ie8则单独处理
	else{
		$('.result-copy').click(function () {
			alert($(this).data('clipboard-text') + '\n\n请按Ctrl+C复制');
		});
	}
	/*
	//返回前一页
	$('#b_flow_q').click(function (){
		history.back();
	});
	//重新执行当前流程
	$('#b_flow_r').click(function (){
		location.href = ydl.contexPath + '/flow/menu/' + poolSelect._WF;
	});
	*/
	//结果页链接（JSON对象序列化后的字符串，具体格式见下）
	//此参数可省略，如果未定义此参数，或所有类别中的链接数均为0，则整个链接区域都不显示
	//只要任意类别中的链接数大于0，则显示所有类别的链接列表，链接数为0的列表中显示"（无）"字样
	var maxlinks = 5;	//结果页每一类最大链接数
	var EMPTY = '<div class="link-empty">（无）</div>';
	var $links = $('#resultInfo').find('.result-links');
	var links = poolSelect._RESULT_PAGE_LINKS ? JSON.parse(poolSelect._RESULT_PAGE_LINKS) : {};

	if (links.recent && links.recent.length || links.prev && links.prev.length || links.next && links.next.length) {
		var addLinks = function (type) {
			var htmls = $.map(links[type] || [], function(link, index) {
				return index < maxlinks ? '<a class ="func-menu" data-funcid="'+link.funcid+'" href="' + ydl.contexPath + link.url + '">' + link.name + '</a>' : null;
			});
			var count = $links.data('count') ? $links.data('count')+1 : 1;
			if (!htmls.length) htmls.push(EMPTY);
			$links.data('count',count).find('.result-' + type).removeClass('hidden').append(htmls.join(''));
		};
		for (var type in links) addLinks(type);
		
		$links.find('.result-link').removeClass('col-md-4').addClass('col-md-'+12/$links.data('count'));
		$links.find('.hidden').remove();
	}
	else {
		$links.hide();
	}

}