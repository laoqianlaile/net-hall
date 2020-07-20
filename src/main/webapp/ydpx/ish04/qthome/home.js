$(function() {
	ydl.sessionData('plat_curplace','首页');
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
		  $('.submenu-keep').toggleClass('hidden');
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
			  $('.submenu').css({left: menuOffset.left + 110, top: menuOffset.top + 90,  width: 0, height: 0});
			  $('.submenu').animate({width: menuWidth, height: 350, left: menuLeft, top: menuOffset.top -90},'300');
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
	  $('.submenu-keep').click(function () {
		  $(this).addClass('hidden');
	  });
  });
  
  //生成首页用户信息HTML
  function userContentHtml(data) {
	  return '<div class="col col-sm-12 home-user-name">' + (data.name || '') + '</div>' +
		  $.map(data.info || [], function (d) {
			  return '<div class="col col-md-' + d.cols + '"><label>' + d.label + '：</label><span>' + d.value + '</span></div>';
		  }).join('');
  }