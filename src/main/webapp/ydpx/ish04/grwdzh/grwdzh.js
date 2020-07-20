$(function() {
	ydl.sessionData('plat_curplace','首页');
	
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
		
		//主体框架收起展开效果
		$('.info-more').on('click', function(){
			var $this = $(this);
			//恢复默认状态
			$('.info-base').removeClass('active less');
			if($this.closest('.right-base').length > 0) {
				//右侧联动
				$('.right-base .info-base').addClass('less');
			}
			//设置展开
			$this.closest('.info-base').removeClass('less').addClass('active');
			//设置贷款信息分列状态
			setColNum('gjjdkInfo');
		});
		$('.info-normal').on('click', function(){
			var $this = $(this);
			if($this.closest('.right-base').length > 0) {
				//右侧联动
				$('.right-base .info-base').removeClass('less');
			}
			//设置展开
			$this.closest('.info-base').removeClass('active');
			//设置贷款信息分列状态
			setColNum('gjjdkInfo');
		});
		//设置分列状态（主要用于公积金贷款账户信息）
		function setColNum(id){
			var $infoBase = $('#'+id);
			if(!$infoBase.find('.info-main .info-body').hasClass('paying')) return;
			if($infoBase.hasClass('active'))	$infoBase.find('.info-body > div > div').removeClass('col-md-4 col-md-6').addClass('col-md-4');
			else $infoBase.find('.info-body > div > div').removeClass('col-md-4 col-md-6').addClass('col-md-6');
		}


	//页面信息接口地址
	var personBaseInfoUrl = '/template/ish04/grst/wdzh/hqgrjbxx';	//个人基本信息
	var personAccountInfoUrl = '/template/ish04/grst/wdzh/hqgrzhjbxx'	//个人账户基本信息
    var loanInfoUrl = '/template/ish04/grst/wdzh/hqgjjdkzhxx';	//贷款状态
    
    //personBaseInfoUrl = '';
    //personAccountInfoUrl = '';
    //loanInfoUrl = '';

	//个人基本信息测试数据
	var data1 =  {
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
							id : 'zjlx',
							label : '证件类型',
							value : '身份证',
							button : [],
							level : '1'
						},
						{
							id : 'zjhm',
							label : '证件号码',
							value : '220123123123213123123123',
							button : [],
							level : '1'
						},
						{
							id : 'sjhm',
							label : '手机号码',
							value : '13912341234',
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
							level : '1'
						},
						{
							id : 'zjhm2',
							label : '出生日期',
							value : '19910107',
							button : [],
							level : '1'
						},
						{
							id : 'zjhm3',
							label : '婚姻状况',
							value : '已婚',
							button : [],
							level : '1'
						},
						{
							id : 'zjhm4',
							label : '性别',
							value : '男性',
							button : [],
							level : '1'
						}
			],
			jtzyxx : [
						{
							id : 'hj1',
							label : '户籍',
							value : '北京市',
							button : [],
							level : '0'
						},
						{
							id : 'hj2',
							label : '家庭住址',
							value : '北京市海淀区北太平庄路18号12层',
							button : [],
							level : '0'
						},
						{
							id : 'hj3',
							label : '邮政编码',
							value : '10010',
							button : [],
							level : '0'
						},
						{
							id : 'hj4',
							label : '固定电话',
							value : '010-84933266',
							button : [],
							level : '0'
						},
						{
							id : 'hj5',
							label : '职业',
							value : '技术员',
							button : [],
							level : '0'
						}
			]
		}	
	};
	//个人基本信息
	//personBaseInfo(data1);
	ydl.ajax(ydl.contexPath + personBaseInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
		personBaseInfo(data);
	}, {'ajaxContainer' : 'grInfo'});
	
	//个人账户基本信息测试数据
	var data2 = {
    zhye : {
        label : '账户余额' ,
        value : '10000.00'
    },
    zhzt : {
        label : '账户状态' ,
        value : '01',
text : '正常'
    },
    ywsj : [
        {
                id : 'grzh',
                label : '个人账号',
                value: 'G3283',
                button: [],
                level : '1'
        },
        {
                id : 'jcjs',
                label : '缴存基数',
                value: '2000.00',
                button: [
                    {
                        id : 'jcjs11',
                        label : '更新资料',
                        icon : 'wydk',
                        url : '/xx/xxx/',
                        funccode : '123213123',
                        button : []
                    }
                ],
                level : '1'
        },
        {
            id : 'jcjs2',
            label : '缴存基数222',
            value: '2000.00',
            button: [
                {
                    id : 'jcjs21',
                    label : '更新资料',
                    icon : 'wydk',
                    url : '/xx/xxx/',
                    funccode : '123213123',
                    button : []
                }
            ],
            level : '0'
        },
        {
                id : 'grzh2',
                label : '月缴存额',
                value: '800.00',
                button: [],
                level : '1'
        },
        {
                id : 'grzh3',
                label : '缴至年月',
                value: '2019-09',
                button: [],
                level : '1'
        },
        {
            id : 'grzh32',
            label : '缴至年月2',
            value: '2019-09',
            button: [],
            level : '0'
        },
        {
            id : 'grzh321',
            label : '缴至年月2',
            value: '2019-09',
            button: [],
            level : '0'
        },
        {
            id : 'grzh322',
            label : '缴至年月2',
            value: '2019-09',
            button: [],
            level : '0'
        },
        {
            id : 'grzh32',
            label : '缴至年月2',
            value: '2019-09',
            button: [],
            level : '0'
        },
        {
            id : 'grzh323',
            label : '缴至年月2',
            value: '2019-09',
            button: [],
            level : '0'
        },
        {
            id : 'grzh324',
            label : '缴至年月2',
            value: '2019-09',
            button: [],
            level : '0'
        },
        {
                id : 'grzh4',
                label : '单位账号',
                value: 'D6678678',
                button: [],
                level : '1'
        },
        {
                id : 'grzh5',
                label : '单位名称',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '1'
        },
        {
            id : 'grzh51',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh52',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh53',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh54',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh55',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh56',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh57',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh58',
            label : '单位名称',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        },
        {
            id : 'grzh52',
            label : '单位名称2',
            value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
            button: [],
            level : '0'
        }
    ],
    button: [
        {
                id : 'wytq',
                label : '我要提取',
                icon : 'wydk',
                url :  '',
                funccode : '123213123',
                children : [
                    {
                                id : 'aa1',
                                label : '菜单1',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    },
                    {
                                id : 'aa2',
                                label : '菜单2',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    },
                    {
                                id : 'aa3',
                                label : '菜单3',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    },
                    {
                                id : 'aa4',
                                label : '菜单4',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    },
                    {
                                id : 'aa5',
                                label : '菜单5',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    },
                    {
                                id : 'aa6',
                                label : '菜单6',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    },
                    {
                                id : 'aa7',
                                label : '菜单7',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    },
                    {
                                id : 'aa8',
                                label : '菜单8',
                                icon : 'wydk',
                                url : '/xx/xxx/',
                                funccode : '123213123',
                                children : []
                    }
                ]
        
        },
        {
            id : 'zhmx',
            label : '账户明细',
            icon : 'wydk',
            url : 'wydk',
            funccode : '123213123',
            children : []
        }
    ],
    menu: [
        {
            id : 'grjczmdy',
                    label : '个人缴存证明打印个人缴存证明打印个人缴存证明打印个人缴存证明打印',
                    icon : 'wydk',
                    url : 'xx/xxx/',
                    funccode : '123213123',
                    children : []
        },
        {
            id : 'grjczmdy11',
                    label : '个人缴存证明打印个人缴存证明打印个人缴存证明打印个人缴存证明打印',
                    icon : 'wydk',
                    url : 'xx/xxx/',
                    funccode : '123213123',
                    children : []
        },
        {
            id : 'grjczmdy2',
                    label : '个人缴存证明打印个人缴存证明打印个人缴存证明打印个人缴存证明打印',
                    icon : 'wydk',
                    url : 'xx/xxx/',
                    funccode : '123213123',
                    children : []
        },
        {
                    id : 'jxdzd',
                    label : '结息对账单',
                    icon : 'wydk',
                    url : 'xx/xxx/',
                    funccode : '123213123',
                    children : []
        },
        {
            id : 'jxdzd2',
            label : '查询密码变更',
            icon : 'wydk',
            url : 'xx/xxx/',
            funccode : '123213123',
            children : []
        },
        {
            id : 'jxdzd3',
            label : '支付密码找回',
            icon : 'wydk',
            url : 'xx/xxx/',
            funccode : '123213123',
            children : []
        }
        ,
        {
            id : 'jxdzd3',
            label : '支付密码找回',
            icon : 'wydk',
            url : 'xx/xxx/',
            funccode : '123213123',
            children : []
        }
        ,
        {
            id : 'jxdzd4',
            label : '支付密码变更',
            icon : 'wydk',
            url : 'xx/xxx/',
            funccode : '123213123',
            children : []
        }
        
        ,
        {
            id : 'jxdzd4',
            label : '支付密码变更',
            icon : 'wydk',
            url : 'xx/xxx/',
            funccode : '123213123',
            children : []
        }
        
        ,
        {
            id : 'jxdzd4',
            label : '支付密码变更',
            icon : 'wydk',
            url : 'xx/xxx/',
            funccode : '123213123',
            children : []
        }
        
    ]
	};
	//personAccountInfo(data2);
	ydl.ajax(ydl.contexPath + personAccountInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
		personAccountInfo(data);
	}, {'ajaxContainer' : 'grzhInfo'});

	//无贷款状态测试数据
	var data30 = {
    dkzt : '0',
    button : [
            {
                    id : 'dksq',
                    label : '贷款申请',
                    icon : 'wydk',
                    url : '/xx/xxx/',
                    funccode : '123213123',
                    children : []
            }
    ],
    ywsj : [
            {
                    id : 'grzh1',
                    label : '个人账号11',
                    value: 'G3283',
                    button: [],
                    level : '1'
            },
            {
                    id : 'jcjs1',
                    label : '缴存基数11',
                    value: '2000.00',
                    button: [
                        {
                            id : 'jcjs111',
                            label : '更新资料11',
                            icon : 'wydk',
                            url : '/xx/xxx/',
                            funccode : '123213123',
                            children : []
                        }
                    ],
                    level : '1'
            },
            {
                id : 'jcjs21',
                label : '缴存基数22211',
                value: '2000.00',
                button: [
                    {
                        id : 'jcjs211',
                        label : '更新资料11',
                        icon : 'wydk',
                        url : '/xx/xxx/',
                        funccode : '123213123',
                        children : []
                    }
                ],
                level : '0'
            },
            {
                    id : 'grzh21',
                    label : '月缴存额11',
                    value: '800.00',
                    button: [],
                    level : '1'
            },
            {
                    id : 'grzh31',
                    label : '缴至年月11',
                    value: '2019-09',
                    button: [],
                    level : '1'
            },
            {
                id : 'grzh321',
                label : '缴至年月211',
                value: '2019-09',
                button: [],
                level : '0'
            },
            {
                id : 'grzh3211',
                label : '缴至年月211',
                value: '2019-09',
                button: [],
                level : '0'
            },
            {
                id : 'grzh3221',
                label : '缴至年月211',
                value: '2019-09',
                button: [],
                level : '0'
            },
            {
                id : 'grzh321',
                label : '缴至年月211',
                value: '2019-09',
                button: [],
                level : '0'
            },
            {
                id : 'grzh3231',
                label : '缴至年月211',
                value: '2019-09',
                button: [],
                level : '0'
            },
            {
                id : 'grzh3241',
                label : '缴至年月211',
                value: '2019-09',
                button: [],
                level : '0'
            },
            {
                    id : 'grzh41',
                    label : '单位账号11',
                    value: 'D6678678',
                    button: [],
                    level : '1'
            },
            {
                    id : 'grzh51',
                    label : '单位名称11',
                    value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                    button: [],
                    level : '1'
            },
            {
                id : 'grzh511',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh521',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh531',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh541',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh551',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh561',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh571',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh581',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            },
            {
                id : 'grzh521',
                label : '单位名称211',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                button: [],
                level : '0'
            }
    ],
    menu: [
            {
                id : 'zcxxMenu1',
                label : '公积金贷款试算',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
            },
            {
                id : 'zcxxMenu2',
                label : '合作楼盘查询',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
            },
            {
                id : 'zcxxMenu2',
                label : '贷款记录查询',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
            }
    ]
	};
	//贷款申请状态测试数据
	var data31 = {
		dkzt : '1',
		dkjd : [
		{jdzt:'1' , jdmc : '开始', jdrq : '2019-01-01'},
		{jdzt:'1' , jdmc : '初审', jdrq : '2019-01-02'},
		{jdzt:'1' , jdmc : '复审', jdrq : '2019-01-03'},
		{jdzt:'0' , jdmc : '终审', jdrq : ''},
		{jdzt:'0' , jdmc : '结束', jdrq : ''}
		],
		ywsj : [
						{
										id : 'grzh1',
										label : '个人账号11',
										value: 'G3283',
										children: [],
										level : '1'
						},
						{
										id : 'jcjs1',
										label : '缴存基数11',
										value: '2000.00',
										children: [
												{
														id : 'jcjs111',
														label : '更新资料11',
														icon : 'wydk',
														url : '/xx/xxx/',
														funccode : '123213123',
														children : []
												}
										],
										level : '1'
						},
						{
								id : 'jcjs21',
								label : '缴存基数22211',
								value: '2000.00',
								children: [
										{
												id : 'jcjs211',
												label : '更新资料11',
												icon : 'wydk',
												url : '/xx/xxx/',
												funccode : '123213123',
												children : []
										}
								],
								level : '0'
						},
						{
										id : 'grzh21',
										label : '月缴存额11',
										value: '800.00',
										children: [],
										level : '1'
						},
						{
										id : 'grzh31',
										label : '缴至年月11',
										value: '2019-09',
										children: [],
										level : '1'
						},
						{
								id : 'grzh321',
								label : '缴至年月211',
								value: '2019-09',
								children: [],
								level : '0'
						},
						{
								id : 'grzh3211',
								label : '缴至年月211',
								value: '2019-09',
								children: [],
								level : '0'
						},
						{
								id : 'grzh3221',
								label : '缴至年月211',
								value: '2019-09',
								children: [],
								level : '0'
						},
						{
								id : 'grzh321',
								label : '缴至年月211',
								value: '2019-09',
								children: [],
								level : '0'
						},
						{
								id : 'grzh3231',
								label : '缴至年月211',
								value: '2019-09',
								children: [],
								level : '0'
						},
						{
								id : 'grzh3241',
								label : '缴至年月211',
								value: '2019-09',
								children: [],
								level : '0'
						},
						{
										id : 'grzh41',
										label : '单位账号11',
										value: 'D6678678',
										children: [],
										level : '1'
						},
						{
										id : 'grzh51',
										label : '单位名称11',
										value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
										children: [],
										level : '1'
						},
						{
								id : 'grzh511',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh521',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh531',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh541',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh551',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh561',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh571',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh581',
								label : '单位名称11',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						},
						{
								id : 'grzh521',
								label : '单位名称211',
								value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
								children: [],
								level : '0'
						}
		],
		menu: [
						{
								id : 'zcxxMenu1',
								label : '公积金贷款试算',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},
						{
								id : 'zcxxMenu2',
								label : '合作楼盘查询',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						},
						{
								id : 'zcxxMenu2',
								label : '贷款记录查询',
								icon : 'wydk',
								url : '/xx/xxx/',
								funccode : '123213123',
								children : []
						}
		]
	};
	//还款状态测试数据
	var data32 = {
    dkzt : '2',
    chart : {       //（图表部分，在点击更多展开框架后，仅显示贷款余额(dkye)和已还比例(yhbl)这两项）
		dkyh : {
			label : '贷款已还',
			value : '1500'
        },
		dkye : {
			label : '贷款余额',
			value : '7500'
        },
        yhbl : '25%'
    },
    button : [
        {
                id : 'tqhk',
                label : '提前还款',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
        },
        {
                id : 'hkmx',
                label : '还款明细',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
        }
    ],
    dkzhzt : '1',	//(0表示未逾期“绿色正常的图标”。1表示已经逾期“红色逾期的图标”)
    ywsj : [
            {
                    id : 'grzh1',
                    label : '个人账号11',
                    value: 'G3283',
                    button: [],
                    level : '1'
            },
            {
                    id : 'jcjs1',
                    label : '缴存基数11',
                    value: '2000.00',
                    button: [
                        {
                            id : 'jcjs111',
                            label : '更新资料11',
                            icon : 'wydk',
                            url : '/xx/xxx/',
                            funccode : '123213123',
                            children : []
                        }
                    ],
                    level : '1'
            },
            {
                id : 'jcjs21',
                label : '缴存基数22211',
                value: '2000.00',
                button: [
                    {
                        id : 'jcjs211',
                        label : '更新资料11',
                        icon : 'wydk',
                        url : '/xx/xxx/',
                        funccode : '123213123',
                        children : []
                    }
                ],
                level : '0'
            },
            {
                    id : 'grzh21',
                    label : '月缴存额11',
                    value: '800.00',
                    buttton: [],
                    level : '1'
            },
            {
                    id : 'grzh31',
                    label : '缴至年月11',
                    value: '2019-09',
                    buttton: [],
                    level : '1'
            },
            {
                id : 'grzh321',
                label : '缴至年月211',
                value: '2019-09',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh3211',
                label : '缴至年月211',
                value: '2019-09',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh3221',
                label : '缴至年月211',
                value: '2019-09',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh321',
                label : '缴至年月211',
                value: '2019-09',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh3231',
                label : '缴至年月211',
                value: '2019-09',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh3241',
                label : '缴至年月211',
                value: '2019-09',
                buttton: [],
                level : '0'
            },
            {
                    id : 'grzh41',
                    label : '单位账号11',
                    value: 'D6678678',
                    buttton: [],
                    level : '1'
            },
            {
                    id : 'grzh51',
                    label : '单位名称11',
                    value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                    buttton: [],
                    level : '1'
            },
            {
                id : 'grzh511',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh521',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh531',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh541',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh551',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh561',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh571',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh581',
                label : '单位名称11',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            },
            {
                id : 'grzh521',
                label : '单位名称211',
                value: '华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司华信永道（北京）科技股份优先公司',
                buttton: [],
                level : '0'
            }
    ],
    menu: [
            {
                id : 'zcxxMenu1',
                label : '公积金贷款试算',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
            },
            {
                id : 'zcxxMenu2',
                label : '合作楼盘查询',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
            },
            {
                id : 'zcxxMenu2',
                label : '贷款记录查询',
                icon : 'wydk',
                url : '/xx/xxx/',
                funccode : '123213123',
                children : []
            }
    ]
	};
	//loanInfo(data32);
	ydl.ajax(ydl.contexPath + loanInfoUrl, {'poolkey' : poolSelect._POOLKEY}, function(data){
		loanInfo(data);
	}, {'ajaxContainer' : 'gjjdkInfo'});

	//切换显示隐藏金额按钮
	$('.info-header').on('click', '.header-money-icon', function(){
		$(this).closest('.header-money').toggleClass('money-show');
	});

	//切换我要提取按钮下拉菜单显示隐藏
	$('#drawingOutBase').on('click', function(){
		$(this).toggleClass('menu-show');
	});

});

//页面左侧通用结构
function leftMainInfo(data, id) {
	var html = '';
	$.each(data, function(k, v){
		html += '<li class="' + (v.level == '1' ? 'level-main' : '') + '" id="' +
							v.id + '"><label title=" ' + v.label + '">' + v.label + '</label>' + (v.button.length > 0 ? ('<a id="' + v.button[0].id +
							'" href="' + getUrl(v.button[0].url) + '" data-menuid="' + v.button[0].funccode +
							'" title="' + v.button[0].label + '"><span class="ydico-ish ydico-ish-' + v.button[0].icon +
							'"></span></a>') : '') + '<span title=" ' + v.value + '">' + v.value + '</span></li>';
	});
	$('#' + id).html(html);
}

//页面右侧通用主要内容，col表示列数，默认'4'分为三列
function rightMainBody(data, id, col){
	var html = '';
	$.each(data, function(k, v){
		html += '<div class="col-md-' + (col || '4') + ' ' + (v.level == '1' ? 'level-main' : '') + '" id="' + v.id +
						'" ><label title="' + v.label + '">' + v.label + '</label><p title="' + v.value + '">' + v.value +
						(v.button && v.button.length > 0 ? '<a id="' + v.button[0].id + '" href="' + getUrl(v.button[0].url) +
						'" data-menuid="' + v.button[0].funccode + '" title="' + v.button[0].label +
						'"><span class="ydico-ish ydico-ish-' + v.button[0].icon + '"></span></a>' : '') +'</p></div>';
	});
	$('#' + id).append(html);
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

//页面按钮设置
function setButton(buttonClass, data){
	var $renewInfo = $('.' + buttonClass);
	var buttonData = data;
	$renewInfo.attr('id', buttonData.id).attr('data-menuid', buttonData.funccode).attr('href',  getUrl(buttonData.url)).html((buttonData.icon && buttonData.icon.length > 0 ? '<span class="ydico-ish ydico-ish-' + buttonData.icon + '"></span>' : '') + buttonData.label);
}

//页面按钮悬浮显示的标签
function setButtonMenu(id, data){
	var html = '';
	$.each(data, function(k, v){
		html += '<a class="drawing-out-menu" id="' + v.id + '" href="' + getUrl(v.url) + '" data-menuid="' + v.funccode + '" title="' + v.label + '"><span class="ydico-ish ydico-ish-' + v.icon + '"></span></a>';
	});
	$('#' + id).prepend(html);
}

//页面url处理
function getUrl(url){
	if(!url || url.length == 0) return 'javascript:void(0);';
	else return ydl.contexPath + url;
}

//生成个人基本信息
function personBaseInfo(data){
	//用户名
	$('#infoUserName').text(JSON.parse(pageData.userContent).name);
	//资料完成度百分比
	$('#progressBar').attr('aria-valuenow', data.zlwzd).css('width',data.zlwzd + '%');
	$('#progressNum').text(data.zlwzd);
	//更新资料
    //删除更新资料图标
    data.button[0].icon = '';
	setButton('renew-info', data.button[0]);
	//基础信息
	leftMainInfo(data.ywsj.jcxx, 'infoJcxx');
	//家庭职业信息
	leftMainInfo(data.ywsj.jtzyxx, 'infoJtzyxx');
}

//生成个人账户基本信息
function personAccountInfo(data){
	//账户余额
	$('#zhye').text(data.zhye.value);
	$('#zhyeLabel').text(data.zhye.label);
	//账户状态
	var accountState = data.zhzt.value;
	$('#zhzt').text(data.zhzt.text).attr('title', data.zhzt.label);
	if(accountState == '01') {
		//正常
		$('#grzhInfo .header-money, #zhzt').addClass('normal');
	}else {
		//其他异常状态
		$('#grzhInfo .header-money, #zhzt').addClass('error');
	}
	//我要提取按钮
	if(data.button[0].children && data.button[0].children.length > 1) {
		//如果我要提取悬浮菜单数目大于1
		setButton('drawing-out', data.button[0]);
		//我要提取按钮悬浮菜单
		setButtonMenu('drawingOutBase', data.button[0].children);
	}else {
		//如果我要提取悬浮菜单数目大于1
		setButton('drawing-out', data.button[0].children[0]);
	}
	
	//账户明细按钮
	setButton('account-info', data.button[1]);
	//主要内容
	rightMainBody(data.ywsj, 'grzhInfoBody');
	//底部菜单链接
	rightMainFoot(data.menu, 'grzhInfoFooter');
}

//生成首页用户信息HTML
function userContentHtml(data) {
	return '<div class="col col-sm-12 home-user-name">' + (data.name || '') + '</div>' +
		$.map(data.info || [], function (d) {
			return '<div class="col col-md-' + d.cols + '"><label>' + d.label + '：</label><span>' + d.value + '</span></div>';
		}).join('');
}

//生成公积金贷款账户信息
function loanInfo(data) {
	var $infoHeader = $('#gjjdkInfo .info-header');
	if(data.dkzt == '0') {
		//无贷款状态
		$infoHeader.append('<div id="noLoan">当前暂无贷款</div><div class="header-right"><a class="loan-application bg-red"></a><div class="state" id="dkzt"></div></div>');
		//贷款申请按钮
		setButton('loan-application', data.button[0]);
		//主要内容
		rightMainBody(data.ywsj, 'dkxxInfoBody');
	}else if(data.dkzt == '1') {
		//贷款申请状态
		var dkjdHtml = '<ul>';
		$.each(data.dkjd, function(k, v){
			dkjdHtml += '<li class="' + (v.jdzt == '1' ? 'done' : (v.jdzt == '2' ? 'doing' : '') ) + '">' + v.jdmc + '<p>' + v.jdrq + '</p></li>';
		});
		dkjdHtml += '</ul>'
		$infoHeader.append(dkjdHtml);
		//主要内容
		rightMainBody(data.ywsj, 'dkxxInfoBody');
	}else if(data.dkzt == '2') {
		//还款状态
		$('#gjjdkInfo .info-body').addClass('paying');
		$infoHeader.append('<div class="header-money">' +
											'<div id="yhbl"></div>' +
											'<span><span id="zhyeHkLabel"></span><span class="header-money-icon"></span></span>' +
											'<div class="info-money-hide">**.**<span class="info-money-unit">元</span></div>' +
											'<div class="info-money-show"><span id="zhyeHk"></span><span class="info-money-unit">元</span></div></div>' +
											'<div class="header-right"><a class="ahead-repay bg-blue"></a><a class="repay-detail bg-red"></a>' +
											'<div class="state" id="hkzt"></div></div>');
		//已还比率
		//$('#yhbl').text(data.chart.yhbl.label + data.chart.yhbl.value);
		//账户总额
		$('#zhyeHk').text(data.chart.dkze.value);
		$('#zhyeHkLabel').html(data.chart.dkze.label + '<span>（元）</span>');
		//提前还款按钮
		setButton('ahead-repay', data.button[0]);
		//还款明细按钮
		setButton('repay-detail', data.button[1]);
		//账户状态
		var accountState = data.dkzhzt;
		if(accountState == '0') {
			//正常
			$('#gjjdkInfo .header-money, #hkzt').addClass('normal');
			$('#hkzt').text('正常').attr('title', '还款状态');
		}else {
			//逾期
			$('#gjjdkInfo .header-money, #hkzt').addClass('error');
			$('#hkzt').text('逾期').attr('title', '还款状态');
		}
		//环形图
		$infoHeader.before('<div class="necklace" id="pieChart"></div>');
		var myChart = echarts.init($('#pieChart')[0]);
		var option = {
            tooltip: {
                trigger: 'item',
                formatter: '{b}（元）:<br/>{c} ({d}%)'
            },
				series : [
						{
								type : 'pie',
								hoverAnimation : false,
								cursor : 'default',
								radius : ['88%', '100%'],
								labelLine : {
										normal : {
												show : false
										}
								},
								data : [
										{value: ydl.delComma(data.chart.dkyh.value), name:'已还本金', itemStyle : {color : '#ef5467'}, emphasis : {itemStyle : {color : '#ef5467'}}},
										{value: ydl.delComma(data.chart.dkye.value), name:'未还本金', itemStyle : {color : '#dbdbdb'}, emphasis : {itemStyle : {color : '#dbdbdb'}}},
								]
						}
				]
		};
		myChart.setOption(option);

		//主要内容
		rightMainBody(data.ywsj, 'dkxxInfoBody', '6');
	}
	//底部菜单链接
	rightMainFoot(data.menu, 'policInfoFooter');
}

//生成ajax遮罩层ajax-running-overlay
//function ajaxRunningCover(id, state){
	
//}