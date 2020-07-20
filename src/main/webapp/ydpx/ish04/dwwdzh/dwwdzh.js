$(function() {
	ydl.sessionData('plat_curplace','首页');
	
	$('#page_wrap').append('<div class="home-main-nav"><div class="kuai-left"><div class="kuai-left-con"><span class="ydico-ish ydico-ish-T01_01_xtcd"></span><p>系统菜单</p></div></div><ul></ul></div>');
	   
	  /*$('#page_wrap').append('<div class="submenu-keep hidden"><div class="submenu"><div class="submenu-content"><div class="submenu-head"></div><div class="submenu-list"></div><div class="submenu-btn"><span class="glyphicon glyphicon-remove"></span><span class="menudesc"></span></div></div></div></div>');*/
	 
	  $('#page_wrap').append('<div class="submenu-keep hidden"><div class="submenu"><div class="submenu-content"><div class="submenu-list"></div></div></div></div>');
	  
	  //操作记录页面
	  $('#page_wrap').append('<div class="operation-record-shadow hidden"><div class="operation-record-content"><div class="operation-record-content-head"><span class="img-clock"></span><span class="operation-record-word">操作记录</span></div><ul class="operation-record-list"><li><span class="operation-record-date bold">2019年03月09日</span><span class="operation-record-time bold">19:30</span><span class="operation-record-axis"></span><span class="operation-record-pointer">◆</span><span class="operation-record-con ">公积金贷款账户信息</span></li></ul><div class="operation-record-content-foot"><span class="glyphicon glyphicon-menu-right operation-record-content-more"></span><span class="operation-record-content-more">查看更多</span></div><div class="operation-record-content-up"><span class="glyphicon glyphicon-menu-up"></span></div></div></div>')
	  
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
			  
			  /*$('.submenu').animate({width: menuWidth, height: 350, left: menuLeft, top: menuOffset.top -90},'500');*/
			  
			  $('.submenu').animate({width: menuWidth, height: 268, left: menuLeft, bottom: 70},'500');
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
	  
	  //操作记录
	  $(".operation-record").click(function(){
		  var height = $(".container-fluid").height();
		  var sheight = height + 122;
		  $('.operation-record-shadow').height(sheight);
		  
		  $('.operation-record-shadow').removeClass("hidden");
		  $('.operation-record-content').css({height:0});
		  $('.operation-record-content').animate({height:400},'500');
	  })
	  $(".operation-record-content-up").click(function(){
		  $('.operation-record-shadow').addClass("hidden");
		  $('.operation-record-content').css({height:400});
		  $('.operation-record-content').animate({height:0},'500');
	  })
	  
	  //左侧展开复用函数
	  function leftOpen(){
		  $(".left-base-con-duan").hide();
		  $(".right-base-top-chang").hide();
		  $(".left-base-con-foot").animate({height:46},'500');
		//个人信息动效
		$(".right-base-top").animate({width:100,height:166},'500',function(){
			$(".left-base-con-chang").fadeIn();
			$(".right-base-top-duan").fadeIn();
		});
		//补缴汇缴信息动效
		$(".right-base-mid-left-chang").hide();
		$(".right-base-mid-right-chang").hide();
		
		$(".right-base-mid-left").animate({width:100,height:166},'500',function(){
			$(".right-base-mid-left-duan").fadeIn();
			$(".right-base-mid-left").css({'border-radius':'15px'});
		})
		$(".right-base-mid-right").animate({width:100,height:166},'500',function(){
			$(".right-base-mid-right-duan").fadeIn();
			$(".right-base-mid-right").css({'border-radius':'15px','margin-left':'16px'});
		})
		//单位明细账
		$(".right-base-foot-chang").hide();
		$("#infoDwmxzMore").show();
		$(".right-base-foot-chang .control-up").hide();
		$(".right-base-foot").animate({width:100,height:162},'500',function(){
			$(".right-base-foot-duan").fadeIn();
		})
		$(".left-base").animate({width:1078},'500');
		$('.left-base .control-up').html("收起<em class='glyphicon glyphicon-menu-left'></em>");
		
	  };
	  
	  //左侧收起
	  function leftClose(){
		  $(".left-base-con-chang").hide();
		  $(".left-base-con-foot").animate({height:88},'500');
		  
		  $(".right-base-mid-left .infoBjHjxx li").hide();
		  $(".right-base-mid-left .level-main").show();
		  $(".right-base-mid-right li").hide();
		  $(".right-base-mid-right .level-main").show();
		  $(".right-base-mid-bottom").css({"bottom":"14px"});
		//个人信息动效
			 $(".right-base-top-duan").hide();
			 $(".left-base").animate({width:390},'500',function(){
				 $(".left-base-con-duan").fadeIn();
				 $(".right-base-top-chang").fadeIn();
			 });
			 $(this).html("更多<em class='glyphicon glyphicon-menu-down'></em>")
			 $(".right-base-top").animate({width:790,height:180},'500');
			 //最近汇缴信息
			 $(".right-base-mid-left-duan").hide();
			 $(".right-base-mid-right-duan").hide();
			 
			 $(".right-base-mid-left").animate({width:395,height:294},'500',function(){
				$(".right-base-mid-left-chang").fadeIn();
				$(".right-base-mid-left").css({'border-radius':'15px 0 0 15px'});
				$("#infoHjxx").show();
			});
			$(".right-base-mid-right").animate({width:395,height:294},'500',function(){
				$(".right-base-mid-right-chang").fadeIn();
				$(".right-base-mid-right").css({'margin-left':'0px','border-radius':'0 15px 15px 0'});
			})
			//单位明细账
			$(".right-base-foot-duan").hide();
			$("#infoDwmxzMore").show();
			$(".right-base-foot-chang .control-up").hide();
			$(".right-base-foot").animate({width:790,height:200},'500',function(){
				$(".right-base-foot-chang").fadeIn();
			})
			$(".right-base-mid-left-chang .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>");
			$('.left-base .control-up').html("展开<em class='glyphicon glyphicon-menu-right'></em>");
	  
	  }
	  //最近汇缴信息展开（当宽为395时）
	  function huijiaoOpen(){
		  
		  //单位下个人信息变化
		  $(".right-base-top .control-up").show();
		  $(".right-base-top").animate({width:790,height:50},'500');
		  //最近汇缴信息
		  $(".right-base-mid-left-duan").hide();
		  $(".right-base-mid-left").animate({width:790,height:508},'500',function(){
			$(".right-base-mid-left-chang").fadeIn();
			$(".right-base-mid-left").css({'border-radius':'15px'});
			$(".right-base-mid-left-chang .control-up").html("收起<em class='glyphicon glyphicon-menu-up'></em>");
			$(".right-base-mid-left li").show();
			$("#infoHjxx").show();
		  });
		  
		  //最近补缴信息
		  $(".right-base-mid-right-duan").hide();
		  $(".right-base-mid-right").animate({width:790,height:50},'500',function(){	
			 $(".right-base-mid-right-chang").fadeIn();
			 $(".right-base-mid-right").css({"margin-left":"16px","border-radius":"15px"});
		  });
		  //单位明细账
		  $(".right-base-foot-duan").hide();
		  $("#infoDwmxzMore").hide();
		  $(".right-base-foot-chang .control-up").show();
		  $(".right-base-foot").animate({width:790,height:50},'500',function(){	
			 $(".right-base-foot-chang").fadeIn();	
			 
		  });
	  }
	  //最近汇缴信息收起（当宽为790）
	  function huijiaoClose(){
		  $(".right-base-mid-bottom").css({'bottom':'14px'});
		 
		  //单位下个人信息变化
		  $(".right-base-top .control-up").hide();
		  $(".right-base-top").animate({width:790,height:180},'500');
		  //最近汇缴信息
		  $(".right-base-mid-left").animate({width:395,height:294},'500',function(){
			$(".right-base-mid-left").css({'border-radius':'15px 0 0 15px'});
			$(".right-base-mid-left .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>") 
			$(".right-base-mid-left .infoBjHjxx li").hide();
			$(".right-base-mid-left .level-main").show();
			$(".right-base-mid-right li").hide();
			$(".right-base-mid-right .level-main").show();
			$("#infoHjxx").show();
			
		  });	  
		  //最近补缴信息
		  $(".right-base-mid-right").animate({width:395,height:294},'500',function(){	
			 $(".right-base-mid-right").css({"margin-left":"0px","border-radius":"0 15px 15px 0"});
			 $(".right-base-mid-right .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>") 
		  });
		  //单位明细账
		  $("#infoDwmxzMore").show();
		  $(".right-base-foot-chang .control-up").hide();
		  $(".right-base-foot").animate({width:790,height:200},'500',function(){	
			  
		  });
	  }
	  //最近补缴信息展开（当宽为395时）
	  function bujiaoOpen(){
		 
		  $(".right-base-mid-bottom").css({'bottom':'-506px'});
		  //单位下个人信息变化
		  $(".right-base-top .control-up").show();
		  $(".right-base-top").animate({width:790,height:50},'500');
		  //最近汇缴信息
		  $(".right-base-mid-left-duan").hide();
		  $(".right-base-mid-left").animate({width:790,height:50},'500',function(){
			$(".right-base-mid-left-chang").fadeIn();
			$(".right-base-mid-left").css({'border-radius':'15px'});
			$("#infoHjxx").hide();
		  });
		  //最近补缴信息
		  $(".right-base-mid-right-duan").hide();
		  $(".right-base-mid-right").animate({width:790,height:508},'500',function(){	
			 $(".right-base-mid-right-chang").fadeIn();
			 $(".right-base-mid-right").css({"margin-left":"16px","border-radius":"15px"});
			 $(".right-base-mid-right .control-up").html("收起<em class='glyphicon glyphicon-menu-up'></em>") 
			 $(".right-base-mid-right li").show();
		  });
		  //单位明细账
		  $(".right-base-foot-duan").hide();
		  $("#infoDwmxzMore").hide();
		  $(".right-base-foot-chang .control-up").show();
		  $(".right-base-foot").animate({width:790,height:50},'500',function(){	
			 $(".right-base-foot-chang").fadeIn();	
		  });
	  }
	  //左侧缩短
	  function leftShort(){
		  $(".left-base-con-chang").hide();
		  $(".left-base-con-foot").animate({height:88},'500');
		  $(".left-base").animate({width:390},'500',function(){
			 $(".left-base-con-duan").fadeIn();
			 $(".left-base .control-up").html("更多<em class='glyphicon glyphicon-menu-right'></em>");
		  });
		  $(".right-base-top-duan").hide();
		  $(".right-base-top-chang").fadeIn();
	  }
	  
	  //当单位基本信息宽度为390时展开
	  $('.left-base .control-up').click(function(){
		  if($('.left-base').width() === 390){
			  leftOpen(); 
		  }
		  else{
			  leftClose();
			  $(".right-base-mid-right .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>") 
		  }
	  })
	  //点击汇缴信息更多时
	  $(".right-base-mid-left-chang .control-up").click(function(){
		  if($('.right-base-mid-left').width() === 395||$('.right-base-mid-left').height() === 50){
			  huijiaoOpen();
			  $(".right-base-mid-right .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>")
			  $(".right-base-mid-bottom").css({'bottom':'14px'});
		  }
		  else{
			  huijiaoClose();
		  }
	  });
	  //点击补缴信息更多时
	  $(".right-base-mid-right-chang .control-up").click(function(){
		  if($('.right-base-mid-right').width() === 395||$('.right-base-mid-right').height() === 50){
			  bujiaoOpen();
			  $(".right-base-mid-left .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>")
		  }
		  else{
			  huijiaoClose();
			  
		  }
	  });
	  //点击个人信息更多（小块）
	  $(".right-base-top .more-bottom").click(function(){
		  leftClose();
	  });
	  //点击点击个人信息展开（长条）
	  $(".right-base-top .control-up").click(function(){
		  huijiaoClose();
	  });
	  //点击最近汇缴信息更多（小块）
	  $(".right-base-mid-left .more-bottom").click(function(){
		  leftShort();
		  huijiaoOpen();
		  $(".right-base-mid-bottom").css({"bottom":"14px"});
		  $(".right-base-mid-right-chang .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>") 
	  });
	  //点击最近补缴信息更多（小块）
	  $(".right-base-mid-right .more-bottom").click(function(){
		  leftShort();
		  bujiaoOpen();
		  $(".right-base-mid-left-chang .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>") 
	  });
	  //点击单位明细账更多（小块）
	  $(".right-base-foot .more-bottom").click(function(){
		  leftClose();
		  $(".right-base-top .control-up").hide();
	  });
	  
	  
	  //点击单位明细账更多（正常）
	  $(".right-base-foot-chang .control-up").click(function(){
		  if($(".right-base-foot").height() < 180 ){
			  leftClose();
			  $(".right-base-mid-right .control-up").html("更多<em class='glyphicon glyphicon-menu-down'></em>")
			  $(".right-base-top .control-up").hide();
			  $(".infoBjHjxx li").not(".level-main").hide();
		  }else{
			  
		  }
		  
	  });
	  
	  
	  
	  //页面信息接口地址
	  var companyBaseInfoUrl = '/template/ish04/dwst/wdzh/hqdwjbxx';	//单位基本信息
	  var companyPersonInfoUrl = '/template/ish04/dwst/wdzh/hqdwxgrxx';	//单位下个人信息
	  var companyMoneyInfoUrl = '/template/ish04/dwst/wdzh/hqzjhbjxx';	//单位最近汇缴信息/最近补缴信息
	  var companyDetailsInfoUrl = '/template/ish04/dwst/wdzh/hqdwmxz';	//单位明细账
		
		//companyBaseInfoUrl = '';	
	  //companyPersonInfoUrl = '';	
	  //companyMoneyInfoUrl = '';	
	  //companyDetailsInfoUrl = ''

	  //单位基本信息测试数据
		/*var data1 =  {
			zlwzd : '80',
			button : [
				{
					id : 'gxzl',
					label : '更新资料',
					icon : 'wydk',
					url : '/xx/xxx/',
					funccode : '123213123',
					children : []
				}
			],
			ywsj : {
				jcxx : [
						{
							id : 'dwzh',
							label : '单位账号',
							value : '1546456464646',
							button : [],
							level : '1'
						},
						{
							id : 'dwslrq',
							label : '单位设立日期',
							value : '2017-07-10',
							button : [],
							level : '1'
						},
						{
							id : 'jcny',
							label : '缴存年月',
							value : '20107',
							button : [],
							level : '1'
						},
						{
							id : 'jjr',
							label : '缴交日',
							value : '每月7日',
							button : [],
							level : '1'
						},
						{
							id : 'zhzt',
							label : '账户状态',
							value : '正常',
							button : [],
							level : '1'
						}		
			],
			qbjcxx : [
						{
							id : 'qbdwzh',
							label : '单位账号',
							value : '1321545646',
							button : [],
							level : '0'
						},
						{
							id : 'tyshxydm',
							label : '统一社会信用代码',
							value : '1321545646',
							button : [],
							level : '0'
						},
						{
							id : 'sbdwbh',
							label : '社保单位编号',
							value : '1321545646',
							button : [],
							level : '0'
						},
						{
							id : 'zzjgdm',
							label : '组织机构代码',
							value : '1321545646',
							button : [],
							level : '0'
						},
						{
							id : 'dwdz',
							label : '单位地址',
							value : '北京市海淀区嘎嘎亮的溜光大道直打出溜滑啊啊啊啊啊',
							button : [],
							level : '0'
						},
						{
							id : 'dwsshy',
							label : '单位所属行业',
							value : '信息',
							button : [],
							level : '0'
						},
						{
							id : 'dwlsgx',
							label : '单位隶属关系',
							value : '市',
							button : [],
							level : '0'
						},
						{
							id : 'dwjjlx',
							label : '单位经济类型',
							value : '内资',
							button : [],
							level : '0'
						},
						{
							id : 'dwfrxm',
							label : '单位法人姓名',
							value : '范德彪',
							button : [],
							level : '0'
						}
			],
			jbrxx : [
						{
							id : 'jbr1',
							label : '经办人张山：',
							value : '1321545646',
							button : [],
							level : '1'
						},
						{
							id : 'jbr2',
							label : '经办人李媛媛：',
							value : '1546546656',
							button : [],
							level : '1'
						}
			]
			
		}	
	};*/
	 //单位基本信息
	//companyBaseInfo(data1);
		ydl.ajax(ydl.contexPath + companyBaseInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			companyBaseInfo(data);
		}, {'ajaxContainer' : $('.left-base')}); 
		
		
		
		
	//页面按钮设置
		function setButton(buttonClass, data){
			var $renewInfo = $('.' + buttonClass);
			var buttonData = data;
			$renewInfo.attr('id', buttonData.id).attr('data-menuid', buttonData.funccode).attr('href', ydl.contexPath + buttonData.url).html( buttonData.label);
		}
		//页面左侧通用结构
		function leftMainInfo(data, id) {
			var html = '';
			$.each(data, function(k, v){
				html += '<li class="' + (v.level == '1' ? 'level-main' : '') + '" id="' +
									v.id + '"><label>' + v.label + '</label>' + (v.button.length > 0 ? ('<a id="' + v.button[0].id +
									'" href="' + ydl.contexPath + v.button[0].url + '" data-menuid="' + v.button[0].funccode +
									'" title="' + v.button[0].label + '"><span class="ydico-ish ydico-ish-' + v.button[0].icon +
									'"></span></a>') : '') + '<span title='+ v.value +'>' + v.value + '</span></li>';
			});
			$('#' + id).html(html);
		}
		
		function leftMainInfo2(data, id) {
			var html = '';
			$.each(data, function(k, v){
				html += '<li class="' + (v.level == '1' ? 'level-main' : '') + '" id="' +
									v.id + '"><label>· ' + v.label + '</label>' + (v.button.length > 0 ? ('<a id="' + v.button[0].id +
									'" href="' + ydl.contexPath + v.button[0].url + '" data-menuid="' + v.button[0].funccode +
									'" title="' + v.button[0].label + '"><span class="ydico-ish ydico-ish-' + v.button[0].icon +
									'"></span></a>') : '') + '<span>：' + v.value + '</span></li>';
			});
			$('#' + id).html(html);
		}
		
	  	//生成个人基本信息
		function companyBaseInfo(data){
			//资料完成度百分比
			$('#progressBar').attr('aria-valuenow', data.zlwzd).css('width',data.zlwzd + '%');
			$('#progressNum').text(data.zlwzd);
			//更新资料
			setButton('renew-info', data.button[0]);
			//基础信息
			leftMainInfo(data.ywsj.jcxx, 'infoJcxx');
			//leftMainInfo(data.ywsj.jbrxx, 'infoJbrxx');
			leftMainInfo2(data.ywsj.jbrxx, 'infoJbrxx');
			//展开后资料完整度
			$('#progressBar2').attr('aria-valuenow', data.zlwzd).css('width',data.zlwzd + '%');
			$('#progressNum2').text(data.zlwzd);
			setButton('renew-info2', data.button[0]);
			//展开后单位基本信息
			leftMainInfo(data.ywsj.jcxx, 'infoQbjcxx');
			$("#infoQbjcxx").append("<li class='jiben-more'><a id=" + data.ywsj.menu[0].id + " href=" + getUrl(data.ywsj.menu[0].url) + " data-menuid=" + data.ywsj.menu[0].funccode + " title=" + data.ywsj.menu[0].label + "><span>" + data.ywsj.menu[0].label + "</span><span class='glyphicon glyphicon-menu-right'></span></a></li>");
		}
		
		//单位下个人信息测试数据
		/*var data2 = {
			ywsj : {
				rsxx : [
					{
						id:'zrs',
						label:'总人数',
						value:'124',
						button:[]
						
					},
					{
						id:'zrrs',
						label:'转入人数',
						value:'1',
						button:[]
						
					},
					{
						id:'zcrs',
						label:'转出人数',
						value:'4',
						button:[]
						
					},
					{
						id:'qfrs',
						label:'启封人数',
						value:'0',
						button:[]
						
					},
					{
						id:'fcrs',
						label:'封存人数',
						value:'0',
						button:[]
						
					},
					{
						id:'khrs',
						label:'开户人数',
						value:'0',
						button:[]
						
					}
				],
				menu : [
					{
						id : 'rzhsl',
						label : '人账户设立',
						icon : 'wydk',
						url : 'xx/xxx/',
						funccode : '123213123',
						children : []
					},
					{
						id:'grfc',
						label:'个人封存',
						icon : 'wydk',
						url : 'xx/xxx/',
						funccode : '123213123',
						children : []
					},
					{
						id:'grqf',
						label:'个人启封',
						icon : 'wydk',
						url : 'xx/xxx/',
						funccode : '123213123',
						children : []
					},
					{
						id:'grzhtczy',
						label:'个人账户同城转移',
						icon : 'wydk',
						url : 'xx/xxx/',
						funccode : '123213123',
						children : []
					},
					{
						id:'grxxbg',
						label:'个人信息变更',
						icon : 'wydk',
						url : 'xx/xxx/',
						funccode : '123213123',
						children : []
					}
				]
		
			}	
		}*/
		//单位下个人信息
		//companyPersonInfo(data2);
		ydl.ajax(ydl.contexPath + companyPersonInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			companyPersonInfo(data);
		}, {'ajaxContainer' : $('.right-base-top')});
		
		
		//页面url处理
		function getUrl(url){
			if(!url || url.length == 0) return 'javascript:void(0);';
			else return ydl.contexPath + url;
		}
		//页面右侧通用底部内容
		function rightMainFoot(data, id){
			var $containerUl = $('#' + id);
			var width = $containerUl.width();
		var html = '';
		$.each(data, function(k, v){
			html += '<li><a id="' + v.id + '" href="' + getUrl(v.url) + '" data-menuid="' + v.funccode + '" title="' + v.label + '"><span class="ydico-ish ydico-ish-' + v.icon + '"></span>' + v.label + '</a></li>';
					//只显示前8个
					if(k >= 7) return false;
			});
			$containerUl.append(html);
			var liLength = $containerUl.find('li').length;
			if(liLength == 0) return;
			//计算默认状态下的总长度
			var allLiWidth = 0;
			$containerUl.find('li').each(function(){
					var $this = $(this);
					var $thisPaddingLeft = parseFloat($this.css('padding-left'));
					var $thisPaddingRight = parseFloat($this.css('padding-right'));
					var $thisBorderLeft = parseFloat($this.css('border-left-width'));
					var $thisBorderRight = parseFloat($this.css('border-right-width'));
					var liWidth = $this.width() + $thisPaddingLeft + $thisPaddingRight + $thisBorderLeft + $thisBorderRight;
					allLiWidth += liWidth;
			});
			if(allLiWidth <= width) return;
			//当菜单总长度超长或超宽时将其设置为平均长度
			var realWidth = width/liLength - 1;
			$containerUl.find('li').each(function(k){
					var $this = $(this);
					var $thisPaddingLeft = parseFloat($this.css('padding-left'));
					var $thisPaddingRight = parseFloat($this.css('padding-right'));
					var $thisBorderLeft = parseFloat($this.css('border-left-width'));
					var $thisBorderRight = parseFloat($this.css('border-right-width'));
					var liWidth = $this.width() + $thisPaddingLeft + $thisPaddingRight + $thisBorderLeft + $thisBorderRight;
					if(liWidth > realWidth) $this.width(realWidth - ($thisPaddingLeft + $thisPaddingRight + $thisBorderLeft + $thisBorderRight));
			});
	}
		function companyPersonInfo(data){
			//单位下个人信息
			leftMainInfo(data.ywsj, 'infoRsxx');
			//底部按钮
			rightMainFoot(data.menu, 'grxxInfoFooter');
		}
		
		//最近汇缴信息、补缴信息模拟数据
		var data3 = {
				ywsj : {
					zjhjxx : [
						{
							id : 'djh',
							label : '登记号',
							value : '454564465465',
							button : [],
							level : '1'
						},
						{
							id : 'ksny',
							label : '开始年月',
							value : '2010-09',
							button : [],
							level : '1'
						},
						{
							id : 'hjjcny',
							label : '缴存年月',
							value : '2010-09',
							button : [],
							level : '1'
						},
						{
							id : 'sjrs',
							label : '实缴人数',
							value : '500',
							button : [],
							level : '1'
						},
						{
							id : 'sjjr',
							label : '实缴金额',
							value : '500',
							button : [],
							level : '1'
						},
						{
							id : 'jcfs',
							label : '缴存方式',
							value : '汇缴',
							button : [],
							level : '1'
						},
						{
							id : 'aaa',
							label : 'aaa',
							value : '2010-09',
							button : [],
							level : '0'
						},
						{
							id : 'bbb',
							label : 'bbb',
							value : '2010-09',
							button : [],
							level : '0'
						},
						{
							id : 'ccc',
							label : 'ccc',
							value : '500',
							button : [],
							level : '0'
						},
						{
							id : 'ddd',
							label : 'ddd',
							value : '500',
							button : [],
							level : '0'
						},
						{
							id : 'eee',
							label : 'eee',
							value : '汇缴',
							button : [],
							level : '0'
						}
					],
					zjbjxx : [
						{
							id : 'djh1',
							label : '登记号',
							value : '454564465465',
							button : [],
							level : '1'
						},
						{
							id : 'ksny1',
							label : '开始年月',
							value : '2010-09',
							button : [],
							level : '1'
						},
						{
							id : 'hjjcny1',
							label : '缴存年月',
							value : '2010-09',
							button : [],
							level : '1'
						},
						{
							id : 'sjrs1',
							label : '实缴人数',
							value : '500',
							button : [],
							level : '1'
						},
						{
							id : 'sjjr1',
							label : '实缴金额',
							value : '500',
							button : [],
							level : '1'
						},
						{
							id : 'jcfs1',
							label : '缴存方式',
							value : '汇缴',
							button : [],
							level : '1'
						},
						{
							id : 'aaaa',
							label : 'aaa',
							value : '2010-09',
							button : [],
							level : '0'
						},
						{
							id : 'bbbb',
							label : 'bbb',
							value : '2010-09',
							button : [],
							level : '0'
						}
					],
					menu : [
						{
							id : 'hj',
							label : '汇缴',
							icon : 'wydk',
							url : 'xx/xxx/',
							funccode : '123213123',
							children : []
						},
						{
							id:'tj',
							label:'退缴',
							icon : 'wydk',
							url : 'xx/xxx/',
							funccode : '123213123',
							children : []
						},
						{
							id:'jcdjcx',
							label:'缴存登记撤销',
							icon : 'wydk',
							url : 'xx/xxx/',
							funccode : '123213123',
							children : []
						},
						{
							id:'jcbltz',
							label:'缴存比例调整',
							icon : 'wydk',
							url : 'xx/xxx/',
							funccode : '123213123',
							children : []
						}
					]
				}	
		}
		
		//最近汇缴信息、补缴信息
		//companyMoneyInfo(data3);
		ydl.ajax(ydl.contexPath + companyMoneyInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			companyMoneyInfo(data);
		}, {'ajaxContainer' : $('.right-base-mid-left, .right-base-mid-right')});
		
		function companyMoneyInfo(data){
			//最近汇缴信息
			leftMainInfo(data.ywsj.zjhjxx, 'infoHjxx');
			//最近补缴信息
			leftMainInfo(data.ywsj.zjbjxx, 'infoBjxx');
			//底部菜单
			rightMainFoot(data.menu, 'hjxxInfoFooter');
		}
		
		//单位明细账内容
		/*var data4 = {
				ywsj : {
					head : ['交易日期','业务类型','发生人数','发生额','余额'],
					body : [
						['2019-01-08','转账还款','213','33,453.00','234,342.01'],
						['2019-01-08','转账还款','213','33,453.00','234,342.01'],
						['2019-01-08','转账还款','213','33,453.00','234,342.01']
					]
				}	
		}*/
		//companyDetailsInfo(data4);
		ydl.ajax(ydl.contexPath + companyDetailsInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
			companyDetailsInfo(data);
		}, {'ajaxContainer' : $('.right-base-foot')});
		
		
		
		//单位明细账头部
		function rightDetailsHead(data, id){
			var html = '';
			$.each(data, function(k, v){
				html += '<td> '+ v +' </td>';
			});
			$('#' + id).append(html);
		}
		//单位明细账主体
		function rightDetailsBody(data, id){
			var html = '';			
			$.each(data, function(k, v){
				var htmlcon = '';
				$.each(v, function(a, b){
					htmlcon += '<td> '+ b +' </td>';
				});
				html += '<tr>'+ htmlcon +'</tr>';
			});
			$('#' + id).append(html);
		}
		function companyDetailsInfo(data){
			//单位明细账
			rightDetailsHead(data.ywsj.head, 'infoDwmxzHead');
			rightDetailsBody(data.ywsj.body, 'infoDwmxz');
			$("#infoDwmxzMore").append('<a id="' + data.menu[0].id + '" href="' + getUrl(data.menu[0].url) + '" data-menuid="' + data.menu[0].funccode + '" title="' + data.menu[0].label + '"><span>更多</span><span class="glyphicon glyphicon-menu-right"></span></a>');
			
		}
	  
  });
  
  //生成首页用户信息HTML
  function userContentHtml(data) {
	  return '<div class="col col-sm-12 home-user-name">' + (data.name || '') + '</div>' +
		  $.map(data.info || [], function (d) {
			  return '<div class="col col-md-' + d.cols + '"><label>' + d.label + '：</label><span>' + d.value + '</span></div>';
		  }).join('');
  }