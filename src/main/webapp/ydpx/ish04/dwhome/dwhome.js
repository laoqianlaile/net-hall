$(function() {
	ydl.sessionData('plat_curplace','首页');
	
	/*var ddd={};
	
	ydl.ajax(url,{},function(data){
		aaaaa(data);
	})
	
	aaaaa(ddd);
	
	function aaaaa(tttt){
		tttt;
	}*/
	
	  $('#page_wrap').append('<div class="home-main-nav"><div class="kuai-left"><div class="kuai-left-con"><span class="ydico-ish ydico-ish-T01_01_xtcd"></span><p>系统菜单</p></div></div><ul></ul></div>');
	   
	  /*$('#page_wrap').append('<div class="submenu-keep hidden"><div class="submenu"><div class="submenu-content"><div class="submenu-head"></div><div class="submenu-list"></div><div class="submenu-btn"><span class="glyphicon glyphicon-remove"></span><span class="menudesc"></span></div></div></div></div>');*/
	 
	  $('#page_wrap').append('<div class="submenu-keep hidden"><div class="submenu"><div class="submenu-content"><div class="submenu-list"></div></div></div></div>');
	  
	  tpl.getMenuData(function(menuData){
		  //首页菜单
		  var menuInfo = tpl.getMenuInfo(menuData);
		  var menuObj = menuInfo.menuObj;
		  var menuOrder = menuInfo.menuOrder;
		  var menuSeq = 0;
		  $.each(menuOrder,function(k, val){
			  var v = menuObj[val];
			  //是否为一级菜单
			  var isTopMenu = v.pid == '$$$$$$$$'; 
			  if (!isTopMenu) return;
			  var $li = $('<li data-bg="'+menuSeq+'" class="icon-bg'+(menuSeq++ % 5)+'" parentId="'+v.id+'"><a data-menuid="'+v.id+'" '+(v.url ? ('href="'+ydl.contexPath+v.url+'"') : '')+'><span class="ydico-ish ydico-ish-'+(v.sign||'zcxw')+'"></span><p>'+v.name+'</p></a></li>');
			  $li.data('list',v.list || []);
			  $('.home-main-nav ul').append($li);
		  });
		  $('.home-main-nav ul').addClass('col' + Math.min($('.home-main-nav li').length, 6));
	  });
	  
  
	  //一级菜单点击事件
	  var subShow = true;
	  $('.home-main-nav').on('click','li',function(){
		  

		  /*
		  var bgIndex = $(this).data('bg');
		  if ($('.submenu').data('sty') != undefined) $('.submenu').removeClass('sty-col'+$('.submenu').data('sty'));
		  $('.submenu').data('sty',bgIndex).addClass('sty-col'+bgIndex);
		  */
		  if ($(this).children('a').attr('href')) return;
		  var menuList = $(this).data('list');
		  //一级菜单名称
		  $('.submenu-head').html('<span>'+$(this).find('p').text()+'</span>');
		  $('.submenu-list').empty();
		  //添加二级菜单
		  if (menuList.length > 0) {
			  var group2Name = pageData['group2Name'];
			  var show2MenuName = group2Name != ''; //是否显示二级菜单名称
			  $('.submenu-list').toggleClass('exist-grouptitle',show2MenuName);
			  var group2menuList = '';
			  var mlist = '';
			  var group2Index = 0;
			  $.each(menuList || [], function (index,mEle){
				  if (mEle.list) {
					  mlist += '<div><span class="menu-group-desc'+(show2MenuName ? '' : ' hide')+'">'+mEle.name+'</span><div class="menulist3">' +'<div>'+$.map(mEle.list, function(ele3, index){
						  return (index % 5 == 0 && index!= 0? '*****' :'') + '<a href = '+ydl.contexPath+ele3.url+' data-desc="'+(ele3.fdesc||'')+'" data-menuid="'+ele3.id+'">◆ '+ele3.name+'</a>';
					  }).join('').split('*****').join('</div><div>')+'</div>'+ '</div></div>';
				  }
				  else {
					  group2menuList += (group2Index % 5 == 0 && group2Index!= 0? '*****' :'') + '<a href = '+ydl.contexPath+mEle.url+' data-menuid="'+mEle.id+'" data-desc="'+(mEle.fdesc||'')+'">◆ '+mEle.name+'</a>';
					  group2Index++;
				  }
			  });
			  var group2menuHtml = group2menuList == '' ? '' :'<div><span class="menu-group-desc'+(show2MenuName ? '' : ' hide')+'">'+group2Name+'</span><div class="menulist3"><div>'+group2menuList.split('*****').join('</div><div>')+'</div></div></div>';
			  $('.submenu-list').append(mlist + group2menuHtml);
		  }
		  
		  // $('.submenu-keep').toggleClass('hidden');
		  
		  //获取并计算页面阴影需要的高度
		  if(subShow){
			  var height = $(".container-fluid").height();
			  var sheight = height + 126;
			  $('.submenu-keep').height(sheight);
			  $('.submenu-keep').removeClass("hidden")
			  subShow = false;
			  
		  }else{
			  $('.submenu-keep').addClass("hidden")
			  subShow = true;
		  }
		  
		  $('.submenu').css('width','auto');
		  //定位
		  if ($('.submenu-keep').is(':visible')) {
			  var menuWidth = 46;
			  $('.submenu div.menulist3').each(function(){
				  menuWidth += 6;
				  $.each($(this).children('div'), function(){
					  menuWidth += this.clientWidth;
				  });
			  });
			  if (menuWidth == 46) menuWidth += $('.submenu-head')[0].clientWidth;
			  var menuOffset = $(this).offset();
			  var menuLeft = menuOffset.left + menuWidth > document.body.clientWidth ? (menuOffset.left + $(this).width() - menuWidth) : menuOffset.left;
			  if(menuLeft <= 0) menuLeft = 0;
			  
			  /*$('.submenu').css({left: menuOffset.left + 110, top: menuOffset.top + 90,  width: 0, height: 0});*/
			  
			  $('.submenu').css({left: menuOffset.left + 110, bottom: 0, width: 0, height: 0});
			  
			  /*$('.submenu').animate({width: menuWidth, height: 350, left: menuLeft, top: menuOffset.top -90},'300');*/
			  
			  $('.submenu').animate({width: menuWidth, height: 268, left: menuLeft, bottom: 60},'300');
		  }
	  });
	  //悬浮二级菜单，显示菜单描述
	  $('.submenu-keep').on('mouseover','a',function (){
		  var desc = $(this).data('desc');
		  if (desc != '') {
			  $('.submenu-keep .menudesc').text(desc);
			  $('.submenu-keep .glyphicon').removeClass('glyphicon-remove');
			  $('.submenu').addClass('desc');
		  }
	  }).on('mouseout','a',function(){
		  $('.submenu-keep .menudesc').text('');
		  $('.submenu-keep .glyphicon').addClass('glyphicon-remove');
		  $('.submenu').removeClass('desc');
		});
		//点击遮罩层关闭菜单
		$('.submenu-keep').on('click', function(e){
			var $target = $(e.target);
			if($target.hasClass('submenu-keep')) {
				$('.submenu-keep').addClass("hidden")
			  subShow = true;
			}
		});
	  //点击首页菜单设置当前位置
	  $('.submenu-keep').on('click','a',function(){
		  if (this.href) {
			  var menu1 = $(this).closest('.submenu').find('.submenu-head').text();
			  var place = menu1 + ' > ' + $(this).text().replace('◆ ','');
			  ydl.sessionData('plat_curplace', place);
			  ydl.sessionData('plat_curmenuid', $(this).data('menuid')||'');
		  }
	  });
	  //点击首页一级菜单设置当前位置
	  $('.home-main-nav').on('click','li a',function(){
		  ydl.sessionData('plat_curplace', $(this).find('p').text());
	  })
	  
	  //设置当前位置
	 if (poolSelect._TYPE == 'menu') {
	 	 var plat_curplace	= ydl.sessionData('plat_curplace');
		 if (plat_curplace) $('.neck-place').text(plat_curplace);
	 }
	 else {
		 var menuDescMap = ydl.sessionData('menuDescMap') ?  JSON.parse(ydl.sessionData('menuDescMap')) : {};
		 var menuDesc = menuDescMap[ydl.attribute.get('_MENUID')] || ydpxData[0].data.page_title;
		 $('.neck-place').text(menuDesc);
	 }
	  
	  //隐藏遮罩
	  /*$('.submenu-keep').click(function () {
		  $(this).addClass('hidden');
	  });*/
		
		//我的账户信息地址
		var asideMenuUrl = '/template/ish04/grst/gg/hqycxfk';
		//页面信息接口地址
		var companyHuijiaoInfoUrl = '/template/ish04/dwst/dwsy/hqhjxx';	//汇缴信息
		var banliMessageUrl = '/template/ish04/dwst/dwsy/hqnknybl';			//您可能要办理
		var shenbaoMessageUrl = '/template/ish04/dwst/dwsy/hqwdsb';			//我的申报
		var guijiMessageUrl = '/template/ish04/dwst/dwsy/hqgjyw';				//归集业务
		var chaxunMessageUrl = '/template/ish04/dwst/dwsy/hqwycx';			//我要查询
		var operationRecordUrl = '/template/ish04/dwst/dwsy/hqzjczjl';	//操作记录


		//companyHuijiaoInfoUrl = '/template/ish04/dwst/dwsy/hqhjxx1';	//汇缴信息
		//banliMessageUrl = '/template/ish04/dwst/dwsy/hqnknybl1';			//您可能要办理
		//shenbaoMessageUrl = '/template/ish04/dwst/dwsy/hqwdsb1';			//我的申报
		//guijiMessageUrl = '/template/ish04/dwst/dwsy/hqgjyw1';				//归集业务
		//chaxunMessageUrl = '/template/ish04/dwst/dwsy/hqwycx1';			//我要查询
		//operationRecordUrl = '/template/ish04/dwst/dwsy/hqzjczjl1';	//操作记录

		//设置我的账户地址
		ydl.ajax(ydl.contexPath + asideMenuUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			if(data.ywsj.wdzh && data.ywsj.wdzh == '1')	{
				$('#headerMyAccount').removeClass('hide').attr('href', getUrl(data.wdzhUrl));
			}
		}, {silent : true});
		
	  /*汇缴信息模拟接口*/
	  var data1 = {
			ywsj : [
					[
							{ label : '预计汇缴年月', value: '2019-07' },
							{ label : '预计汇缴金额', value: '251,321.00' },
							{ label : '汇缴人数', value: '490' }
					],
					[
							{ label : '预计汇缴年月', value: '2019-08' },
							{ label : '预计汇缴金额', value: '261,321.00' },
							{ label : '汇缴人数', value: '491' }
					],
					[
							{ label : '预计汇缴年月', value: '2019-08' },
							{ label : '预计汇缴金额', value: '271,321.00' },
							{ label : '汇缴人数', value: '492' }
					]
				],
				menu : [
								{
										id : 'hj',
										label : '汇缴',
										icon : 'wydk',
										url : '/xx/xxx/',
										funccode : '123213123',
										children : []
						},
								{
										id : 'bj',
										label : '补缴',
										icon : 'wydk',
										url : '/xx/xxx/',
										funccode : '123213123',
										children : []
						}
				]
		};
	  //汇缴信息
	  //companyHuijiaoInfo(data1);
		ydl.ajax(ydl.contexPath + companyHuijiaoInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			companyHuijiaoInfo(data);
		}, {'ajaxContainer' : $('.huijiao-message')});

		/*您可能要办理模拟接口*/
	  var data2 = {
			menu : [
						{
								id : 'nknybl1',
								label : '您可能要办理1',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},
						{
								id : 'nknybl2',
								label : '您可能要办理2您可能要办理2',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},
						{
								id : 'nknybl3',
								label : '您可能要办理3您可能要办理3您可能要办理3',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},
						{
								id : 'nknybl4',
								label : '您可能要办理4',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},
						{
								id : 'nknybl5',
								label : '您可能要办理5',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						}
				]
		};
		//您可能要办理
		//banliMessage(data2);
		ydl.ajax(ydl.contexPath + banliMessageUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			banliMessage(data);
		}, {'ajaxContainer' : $('.banli-message')});

		/*我的申报模拟接口*/
		var data3 = {
			ywsj : {
							text:'您在xxxxxxx的时候干了申报这件事。<a href="XXXXX">取消申报</a>',
							sbh:''
			},
			menu : [
						{
							id : 'sbxq',
							label : '申报详细',
							icon : 'wydk',
							url : '/xx/xxx/',
							funccode : '123213123',
							children : []
						}
			]
		};
		//我的申报
		//shenbaoMessage(data3);
		ydl.ajax(ydl.contexPath + shenbaoMessageUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			shenbaoMessage(data);
		}, {'ajaxContainer' : $('.shenbao-message')});

		/*归集业务模拟接口*/
	  var data4 = {
			menu : [
						{
								id : 'gjid1',
								label : '个人账户设立',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},{
								id : 'gjid2',
								label : '个人账户同城转移',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},{
								id : 'gjid3',
								label : '个人账户同城转移',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						}
				]
		};
	  //归集业务
	  //guijiMessage(data4);
		ydl.ajax(ydl.contexPath + guijiMessageUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			guijiMessage(data);
		}, {'ajaxContainer' : $('.guiji-message')});

		/*归集业务模拟接口*/
		data5 = {
			menu : [
						{
								id : 'wycxid1',
								label : '缴存明细',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},
						{
								id : 'wycxid2',
								label : '单位明细账',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						}
				]
		};
		//我要查询
		//chaxunMessage(data5);
		ydl.ajax(ydl.contexPath + chaxunMessageUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			chaxunMessage(data);
		}, {'ajaxContainer' : $('.chaxun-message')});

		var data6 = {
			ywsj : [
				{
					date : '2019年03月09日',
					time : '19:30',
					text : '具体操作的菜单名',
						},
						{
					date : '2019年03月09日',
					time : '19:30',
					text : '具体操作的菜单名',
						}
				],
				menu : [
						{
								id : 'ckgd',
								label : '查看更多',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						}
				]
		};		
		//操作记录
		//operationRecord(data6);
		ydl.ajax(ydl.contexPath + operationRecordUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			operationRecord(data);
		}, {silent : true});
  });
  
  //生成首页用户信息HTML
  function userContentHtml(data) {
	  return '<div class="col col-sm-12 home-user-name">' + (data.name || '') + '</div>' +
		  $.map(data.info || [], function (d) {
			  return '<div class="col col-md-' + d.cols + '"><label>' + d.label + '：</label><span>' + d.value + '</span></div>';
		  }).join('');
	}
	
	//生成汇缴信息
	function companyHuijiaoInfo(data){
		var $carouselInner = $('#carousel-inner');
		//业务数据
		var huijiaoInfoHtml = '';
		$.each(data.ywsj, function(k, v){
			huijiaoInfoHtml += '<div class="item ' + (k == 0 ? 'active' : '') + '"><div><p>' + v[0].label +
			'</p><span>' +  v[0].value + '</span><p>' + v[1].label + '</p><span>' +  v[1].value +
			'</span><p>' +  v[2].label + '：</p><span>' +  v[2].value + '</span></div></div>';
		});
		$carouselInner.prepend(huijiaoInfoHtml);
		//菜单
		var menuHtml = '';
		$.each(data.menu, function(k, v){
			menuHtml += '<a href="' + getUrl(v.url) + '" id="' + v.id + '" data-menuid="' + v.funccode + '"><span class="ydico-ish ydico-ish-' + v.icon + '"></span>' + v.label + '</a>';
		});
		$carouselInner.find('#carouselMenu').append(menuHtml);
		//启动自动轮播
		$('#imgCircle').carousel({
			interval: 5000
		});
	}

	//生成您可能要办理
	function banliMessage(data){
		var html = '<ul>';
		$.each(data.menu, function(k, v){
			var labelClass = ''; 
			if(v.label.length > 7 && v.label.length <= 14) labelClass = 'double-line';
			if(v.label.length > 14) labelClass = 'multi-line';
			html += '<li><a href="' + getUrl(v.url) + '" id="' + v.id + '" data-menuid="' + v.funccode + '"><span class="ydico-ish ydico-ish-' + v.icon + ' color-' + (k + 1) + '"></span><p class="' + labelClass + '" title="' + v.label + '">' + v.label + '</p></a></li>';
		});
		html += '</ul>';
		$('.banli-message').append(html);
	}

	//生成我的申报
	function shenbaoMessage(data){
		var $shenbaoMessage = $('.shenbao-message');
		
		//$shenbaoMessage.find('> p').html(data.ywsj.text);
		
		//根据sbh是否为空判断有无内容
		if(data.ywsj.sbh ==""){
			$(".shenbao-message").find('p').remove();
			$(".shenbao-message").append("<div class='zanwu'><div class='zanwu-img'></div><div class='zanwu-word'>暂无申报</div></div>");
		}else{
			$(".shenbao-message .zanwu").remove();
			$(".shenbao-message").append('<p></p>');
			$shenbaoMessage.find('> p').html(data.ywsj.text);
		}
		
		$shenbaoMessage.append('<a id="' + data.menu[0].id + '" href="' + getUrl(data.menu[0].url) + '" data-menuid="' + data.menu[0].funccode + '"><em>' + data.menu[0].label + '</em> <em class="glyphicon glyphicon-menu-right right-arrow"></em></a>');
	}

	//生成归集业务
	function guijiMessage(data){
		var html = '';
		$.each(data.menu, function(k, v){
			html += '<li>◆<a href="' + getUrl(v.url) + '" id="' + v.id + '" data-menuid="' + v.funccode + '" title="' + v.label + '">' + v.label + '</a></li>';
		});
		$('.guiji-message ul').append(html);
	}

	//生成我要查询
	function chaxunMessage(data){
		var html = '';
		$.each(data.menu, function(k, v){
			html += '<li><a href="' + getUrl(v.url) + '" id="' + v.id + '" data-menuid="' + v.funccode + '">' + v.label + '</a></li>';
		});
		$('.chaxun-message ul').append(html);
	}

	//生成操作记录
	function operationRecord(data){
		var $pageWrap = $('#page_wrap');
		$(".operation-record").click(function(){
		  var height = $(".container-fluid").height();
		  var sheight = height + 122;
		  
		  var html = '<div class="operation-record-shadow hidden"><div class="operation-record-content"><div class="operation-record-content-head"><span class="img-clock"></span><span class="operation-record-word">操作记录</span></div><ul class="operation-record-list">';

			$.each(data.ywsj, function(k, v){
				html += '<li><span class="operation-record-date bold">' + v.date + '</span><span class="operation-record-time bold">' + v.time + '</span><span class="operation-record-axis"></span><span class="operation-record-pointer">◆</span><span class="operation-record-con ">' + v.text + '</span></li>';
			});

			html += '</ul><div class="operation-record-content-foot"><a href="' + getUrl(data.menu[0].url) + '" id="' + data.menu[0].id + '" data-menuid="' + data.menu[0].funccode + '"><span class="operation-record-content-more">' + data.menu[0].label + '<span class="glyphicon glyphicon-menu-right"></span></a></div><div class="operation-record-content-up"><span class="glyphicon glyphicon-menu-up"></span></div></div></div>';
			$pageWrap.find('.operation-record-shadow').remove();
			$pageWrap.append(html);
			
			$('.operation-record-shadow').height(sheight);
		  $('.operation-record-shadow').removeClass("hidden");
		  $('.operation-record-content').css({height:0});
		  $('.operation-record-content').animate({height:400},'300');
	  });
	  $pageWrap.on("click", ".operation-record-content-up", function(){
		  setTimeout(function(){
			  $('.operation-record-shadow').addClass("hidden");
		  },400)	  
		  $('.operation-record-content').css({height:400});
		  $('.operation-record-content').animate({height:0},'300');
	  });
	}

	//页面url处理
	function getUrl(url){
		if(!url || url.length == 0) return 'javascript:void(0);';
		else return ydl.contexPath + url;
	}