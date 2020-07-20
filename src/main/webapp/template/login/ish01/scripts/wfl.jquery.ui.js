/*
 * Web Front Lib
 * web前端jquery插件库
 * Copyright 华信永道. All rights reserved.
 * @author: 万隆
 * @version: 1.1.0
 */

/****************************************************************
/**选项卡
/**
   * API:
   * this.triggers    =>  jQuery
   * this.panels      =>  jQuery
   * this.config      =>  Object
   * this.container   =>  jQuery   
   * this.length      =>  Number
   * this.index       =>  Number
   * this.switchTo()  =>  Function
/**
   * DOM: 
   * triggers  =>  $(this).find(this.triggers)
   * panels    =>  $(this).find(this.panels)  
*/
;(function($){
	$.wflTabs = $.wflTabs || {};
	$.wflTabs = {
		//属性配置
		config : {
			triggers : null,//触发元素_String,jQuery			
			triggerCurrentCls : 'current',//trigger当前样式
			panels : null,//容器元素_jQuery
			initIndex : 0,//初始激活元素[-1激活最后一个元素,false不激活]		
			triggerType : 'mouseover',//触发方式[mouseover,click]
			collapse : false,//允许收缩自己
			effect : 'none',//String_["none","fade","ajax"]		
			duration : 300,//每次切换所花的时间
			plugin : false,//插件模式
			api : false
		},
		Effects : {
			'none' : function(i){
				var self = this,
					defaults = self.config,
					panels = self.panels,
					panel = panels.eq(i);
				if(defaults.collapse){
					panels.not(panel).hide().end().eq(i).is(":hidden") ? panel.show() : panel.hide();					
				}else{
					panels.hide().eq(i).show();
				}				
			},
			'fade' : function(i){
				var self = this,
					defaults = self.config,
					panels = self.panels,
					panel = panels.eq(i);
				if(defaults.collapse){
					panels.not(panel).hide().end().eq(i).is(":hidden") ? panel.fadeIn() : panel.fadeOut();					
				}else{
					panels.hide().eq(i).fadeIn();
				}		
			}
		},
		Plugins : []
	}	
	function wflTabs($container, defaults){
		var self = this,
			$self = $(this);
		$.extend(self, {
			_init : function(){
				self.container = $container;
				self.config = defaults;
				//获取元素
				self.triggers = typeof defaults.triggers === 'string' ? self.container.children(defaults.triggers).children() : self.container.find(defaults.triggers);
				self.panels = typeof defaults.panels === 'string' ? self.container.children(defaults.panels) : self.container.find(defaults.panels);					
				//数据
				self.length = self.panels.length;
				if(self.length < 1){
					return false;	
				}
				self.index = defaults.initIndex === false ? undefined : (defaults.initIndex + (defaults.initIndex < 0 ? self.length : 0));
				//初始化
				if(defaults.initIndex !== false){
					defaults.initIndex !== -1 ? self.triggers.eq(defaults.initIndex).addClass(defaults.triggerCurrentCls) : self.triggers.eq(self.length-1).addClass(defaults.triggerCurrentCls);
					defaults.initIndex !== -1 ? self.panels.eq(defaults.initIndex).show() : self.panels.eq(self.length-1).show();					
				}
				//绑定
				var index,trigger;
				for(var i=0; i<self.length; i++){
					trigger = self.triggers.eq(i);
					trigger.bind(defaults.triggerType, {index : i}, function(e){
						index = e.data.index;		
						if(!self._triggerIsValid(index)) {
							if(!defaults.collapse) return false;
						}
						self.switchTo(index);
					});
				}
			},
			//屏蔽当前节点
			_triggerIsValid : function(i){
				return self.index !== i;
			},
			_switchTrigger : function(i){
				var cls = defaults.triggerCurrentCls,
					triggers = self.triggers,
					trigger = triggers.eq(i);				
				if(defaults.collapse){
					triggers.not(trigger).removeClass(cls).end().eq(i).hasClass(cls) ? trigger.removeClass(cls) : trigger.addClass(cls);				
				}else{
					triggers.removeClass(cls).eq(i).addClass(cls);
				}	
			},
			_switchPanel : function(i){
				$.wflTabs.Effects[defaults.effect].call(self, i);
			},
			switchTo : function(i){
				self._switchTrigger(i);
				self._switchPanel(i);
				self.index = i;
				return self;
			}
		});
		self._init();
	}
	
	$.fn.wflTabs = function(config){
		var defaults = $.extend({}, $.wflTabs.config, config),
			$self = $(this),
			e;
		e = new wflTabs($self, defaults);
		return defaults.api ? e : $self;
	}
})(jQuery);

/**
* Ajax load 
*/
(function($){
	$.extend($.wflTabs.config, {
		contentURL : []//url数组
	});	
	$.wflTabs.Effects['ajax'] = function(i){
		var self = this,
			defaults = self.config,
			triggers = self.triggers,
			panels = self.panels,
			len = defaults.contentURL.length;
		if(defaults.effect !== 'ajax' || self.length < 1 || len < 1){
			return false;	
		}
		defaults.collapse = false;//强制转换
		
		panels.show().load(defaults.contentURL[i], function(responseText, textStatus, XMLHttpRequest){
			//请确保您被请求的页面编码与请求页面编码一致
			//this指向当前的DOM对象
			//responseText请求返回的内容
			//textStatus请求返回的状态[success,error]
			//XMLHttpRequest  XMLHttpRequest对象
		});	
	}
})(jQuery);

/****************************************************************
/** 
   菜单
*/
;(function($){
	$.wflMenu = $.wflMenu || {};
	$.wflMenu = {
		//属性配置
		config : {
			triggers : null,//触发元素_jQuery
			triggerCurrentCls : 'current',//trigger当前样式
			panels : null,//容器元素_jQuery
			panelKeepState : true,// 触发后容器元素是否保持显示状态
			panelReturnCur : false,//触发非当前元素后是否返回现当前元素状态
			triggerType : 'mouseover',//触发方式[mouserover,click]
			initIndex : 0,//初始激活元素[-1代表最后一个元素,false代表不激活]
			pattern : 'normal',//模式[normal,infinite]
			effect : null,//触发效果[fade,slide]
			duration : 300,//每次切换所花的时间
			api : false
		},
		Patterns : {//模式
			'normal' : function(i){
				var self = this,
					e = self.panels.hide().end().eq(i);
				self._showPanel(e);
			}
		},
		Plugins : []
	};	
	
	function wflMenu($container, defaults){
		var self = this,
			$self = $(this);		
		$.extend(self, {
			//初始化插件
			_initPlugins : function(){
				var plugins = $.wflMenu.Plugins,
					len = plugins.length;
				for(var i = 0; i < len ; i++){
					if(plugins[i].init){
						plugins[i].init(self);
					}
				}
			},
			//初始化
			_init : function(){
				self.container = $container;
				self.config = defaults;				
				//-------------------------------获取triggers And panels-------------------------------
				if(!defaults.triggers.jquery || !defaults.panels.jquery){				
					return;
				}	
				self.triggers = $container.find(defaults.triggers);
				self.panels =  $container.find(defaults.panels);			
				//-------------------------------初始化-------------------------------
				if(defaults.initIndex !== false){
					defaults.initIndex === -1 ? self.triggers.eq(self.triggers.length-1).addClass(defaults.triggerCurrentCls) : self.triggers.eq(defaults.initIndex).addClass(defaults.triggerCurrentCls);
					if(defaults.panelKeepState)	self.panels.eq(defaults.initIndex).show();
				}				
				//-------------------------------绑定-------------------------------						
				self.length = self.triggers.length;			
				self.index = defaults.initIndex === false ? undefined : (defaults.initIndex + (defaults.initIndex < 0 ? self.length : 0));
				
				self.triggers.each(function(i){
					var $this = $(this);
					$this.bind(defaults.triggerType, function(){
						if(!self._triggerIsValid(i)) return;
						self.switchTo(i);											   
					});
				});
				self.container.bind("mouseleave", function(){
					if(defaults.panelReturnCur && defaults.initIndex !== false){
						self.switchTo(defaults.initIndex);
					}
					if(!defaults.panelKeepState){
						self.panels.hide();
					}
				});			
			},
			//end init				
			//屏蔽元素重复触发
			_triggerIsValid : function(i) {
				return self.index !== i;
			},
			//触发
			_switchTrigger : function(i){
				self.triggers
				.removeClass(defaults.triggerCurrentCls)
				.end()
				.eq(i).addClass(defaults.triggerCurrentCls);
			},
			_switchPanel : function(i){
				$.wflMenu.Patterns[defaults.pattern].call(self, i);
			},
			_showPanel : function(e){
				switch(defaults.effect){
					case "fade" :
						e.fadeIn(defaults.duration);
						break;
					case "slide" :
						e.slideDown(defaults.duration);
						break;
					default :
						e.show();
				}
			},
			switchTo : function(i){
				self._switchTrigger(i);
				self._switchPanel(i);
				self.index = i;
				return self;
			}
		});
		//初始化并安装插件
		self._init();
		self._initPlugins();
	}
	
	$.fn.wflMenu = function(defaults){
		var defaults = $.extend({}, $.wflMenu.config, defaults),
			$self = $(this),
			e = [];
		e = new wflMenu($self, defaults);
		return defaults.api ? e : $self;		
	}
})(jQuery);

/****************************************************************
/** 
   无限级菜单
*/
;(function($){
	$.wflInfiniteMenu = $.wflInfiniteMenu || {};
	$.wflInfiniteMenu = {
		//属性配置
		config : {
			triggers : 'a',//触发元素_String,jQuery
			panels : 'div',//节点容器_String,jQuery	
			triggerType : 'mouseover',//触发方式_String
			currentCls : 'current',//当前样式名称_String
			hasChildCls : 'arrow',//含子节点的节点箭头样式名称_String
			initIndex : false,//当前激活元素_Boolean,Number
			holdPanel : false,//节点容器显隐状态_boolean
			mode : 'normal',//模式_String
			effect : 'slide',//切换效果_String
			duration : 300,//每次切换时间[ms]_Number
			api : false//对外API接口_Boolean
		},		
		Plugins : []
	}
	function wflInfiniteMenu($container, defaults){
		var self = this,
			$self = $(this);
		$.extend(self, {
			_init : function(){
				self.container = $container;
				self.config = defaults;	
				self.triggers = self.container.find(defaults.triggers);
				self.topTriggers = self.container.children().children(defaults.triggers);
				self.panels = self.container.find(defaults.panels);					
				self.length = self.triggers.length;
				if(self.length < 1){
					return false;	
				}	
				if(defaults.initIndex !== false){
					self.topTriggers.eq(defaults.initIndex).addClass(defaults.currentCls);
					if(defaults.holdPanel === true) self._show(self.topTriggers.eq(defaults.initIndex).next());
				}					
				var index, $index;					
				for(var i=0; i<self.length; i++){//初始化与绑定
					trigger = self.triggers.eq(i);
					if(self._hasChild(trigger)){
						trigger.addClass(defaults.hasChildCls);
					}					
					trigger.bind(defaults.triggerType, {index : i}, function(e){
						index = e.data.index;
						$index = self.triggers.eq(index);	
						if(self.topTriggers.index($index)>=0){
							self.triggers.removeClass(defaults.currentCls).eq(index).addClass(defaults.currentCls);	
						}		
						defaults.mode === 'infinite' ? self._hideChild($index) : self._hidePanels(index);
						if(self._hasChild($index)){
							self._showChild($index);	
						}
					})
				}
				self.container.bind("mouseleave", function(){//撤销
					setTimeout(function(){
						self._hideAllChild();	
						if(defaults.initIndex !== false){
							self.topTriggers.removeClass(defaults.currentCls).eq(defaults.initIndex).addClass(defaults.currentCls);
							if(defaults.holdPanel === true) self._show(self.topTriggers.eq(defaults.initIndex).next());
						}
						else{
							self.triggers.removeClass(defaults.currentCls);
						}								
					},defaults.duration / 3);									  
				})
			},
			_hasChild : function(el){
				return el.next().is(defaults.panels) ? true : false;
			},
			_showChild : function(el){	
				if(!(self.topTriggers.index(el)>=0)){
					var position = el.position();
					el.next().css({
						"top" : position.top,
						"left" : position.left + el.outerWidth()
					});
				}	
				if(el.next().is(":hidden")){
					self._show(el.next());
				}
			},
			_hideChild : function(el){
				if(self._hasChild(el) && el.next().is(":visible")){
					self._hide(el.next());
				}
				else if( (self._hasChild(el) && el.next().is(":hidden")) || !self._hasChild(el) ){
					self._hide(el.parent().parent().find(defaults.panels));
				}
				else{
					return false;
				}
			},
			_hideAllChild : function(){
				self._hide(self.panels);
			},
			_hidePanels : function(index){
				if(self._hasChild(self.triggers.eq(index))){
					self._show(self.panels.hide().eq(index));	
				}				
			},
			_show : function(el){
				switch(defaults.effect){
					case "slide" :
						el.slideDown(defaults.duration);
						break;
					case "fade" :
						el.fadeIn(defaults.duration);
						break;
					default :
						el.show();
				}
			},
			_hide : function(el){
				switch(defaults.effect){
					case "slide" :
						el.slideUp(defaults.duration);
						break;
					case "fade" :
						el.fadeOut(defaults.duration);
						break;
					default :
						el.hide();
				}
			}
		})
		self._init();
	}
	$.fn.wflInfiniteMenu = function(config){
		var defaults = $.extend({}, $.wflInfiniteMenu.config, config),
			$self = $(this),
			e;
		e = new wflInfiniteMenu($self, defaults);
		return defaults.api ? e : $self;
	}	
})(jQuery);

/****************************************************************
/** 
   手风琴 
*/
/**
   * API:
   * this.triggers    =>  jQuery
   * this.panels      =>  jQuery
   * this.config      =>  Object
   * this.container   =>  jQuery   
   * this.length      =>  Number
   * this.index       =>  Number
   * this.switchTo()  =>  Function
/**
   * DOM: 
   * triggers  =>  $(this).find(this.triggers)
   * panels    =>  $(this).find(this.panels)  
*/
;(function($){
	$.wflAccordion = $.wflAccordion || {};
	$.wflAccordion = {
		//属性配置
		config : {
			triggers : null,//触发元素_String,jQuery			
			triggerCurrentCls : 'current',//trigger当前样式
			panels : null,//容器元素_jQuery
			initIndex : 0,//初始激活元素[-1激活最后一个元素,false不激活]		
			triggerType : 'click',//触发方式[mouserover,click]
			multiple : false,//允许展开多个_Boolean
			collapse : false,//允许收缩自己_Boolean
			effect : 'none',//String_["none","slide"]		
			duration : 300,//每次切换所花的时间_Number
			plugin : false,//插件模式_Boolean
			api : false//Boolean
		},
		Effects : {
			'none' : function(i){
				var self = this,
					defaults = self.config,
					panels = self.panels,
					pT = self.panels.eq(i);	
				if(defaults.multiple) pT.is(":hidden") ? pT.show() : pT.hide();
				else{			
					defaults.collapse ? panels.not(pT).hide() : panels.hide();
					defaults.collapse ? (pT.is(":hidden") ? pT.show() : pT.hide()) : pT.show();
				}
			}
		},	
		// 插件扩展参数
		Plugins : []
	};		
	function wflAccordion($container, defaults){		
		var self = this,
			$self = $(this);		
		$.extend(self, {	
			//Plugin init
			_initPlugins : function(){
				var plugins = $.wflAccordion.Plugins,
					len = plugins.length,
					i = 0;
				for( ; i<len; i++){
					if(plugins[i].init){
						plugins[i].init(self);
					}
				}
			},		
			//初始化
			_init : function(){
				self.container = $container;
				self.config = defaults;				
				//-------------------------------获取triggers And panels-------------------------------
				self.triggers = self.container.find(defaults.triggers); 
				self.panels = self.container.find(defaults.panels);				
				//-------------------------------初始化-------------------------------
				self.length = self.triggers.length;	
				if(defaults.initIndex && defaults.multiple){
					self.triggers.addClass(defaults.triggerCurrentCls);
					self.panels.show();
				}				
				if(defaults.initIndex !== false && defaults.initIndex !== true){
					defaults.initIndex === -1 ? self.triggers.eq(self.triggers.length-1).addClass(defaults.triggerCurrentCls) : self.triggers.eq(defaults.initIndex).addClass(defaults.triggerCurrentCls);
					defaults.initIndex === -1 ? self.panels.eq(self.panels.length-1).show() : self.panels.eq(defaults.initIndex).show();
				}
				self.index = defaults.initIndex === false || defaults.initIndex === true  ? undefined : (defaults.initIndex + (defaults.initIndex < 0 ? self.length : 0));	
				//-------------------------------绑定-------------------------------	
				if(self.length < 1){
					return false;
				}				
				else{
					var trigger, index;
					for(var i = 0; i < self.length; i++){//用for代替each循环提升性能				
						trigger = self.triggers.eq(i);						
						trigger.bind(defaults.triggerType, {index: i}, function(e){
							index = e.data.index;
							if(!self._triggerIsValid(index)){
								if(!defaults.collapse && !defaults.multiple) return false;
							}
							self.switchTo(index);											   
						});
					}
				}
			},
			//end init				
			//当前节点判断
			_triggerIsValid : function(i) {
				return self.index !== i;
			},
			//触发
			_switchTrigger : function(i){
				var cls = defaults.triggerCurrentCls,
					triggers = self.triggers,
					tT = self.triggers.eq(i);	
				if(defaults.multiple){
					tT.toggleClass(cls);
				}
				else{
					if(defaults.collapse){
						triggers.not(tT).removeClass(cls);
						triggers.eq(i).hasClass(cls) ? tT.removeClass(cls) : tT.addClass(cls);
					}else{					
						triggers.removeClass(cls).eq(i).addClass(cls);
					}
				}		
			},
			_switchPanel : function(i){
				$.wflAccordion.Effects[defaults.effect].call(self, i);
			},
			willTo : function() {				
				return self.index < self.length - 1 ? self.index + 1 : 0;//循环执行				
			},
			switchTo : function(i){
				self._switchTrigger(i);
				self._switchPanel(i);
				self.index = i;
				return self;
			}
		});
		self._init();
		if(defaults.plugin) self._initPlugins();
	}	
	$.fn.wflAccordion = function(config){		
		var defaults = $.extend({}, $.wflAccordion.config, config),//将页面定义配置参数合并、覆盖到默认配置参数，不改变原有结构
			$self = $(this),
			e = [];
		e = new wflAccordion($self, defaults);
		return defaults.api ? e : $self;		
	}
})(jQuery);

/**
* slide 滑入滑出效果 
*/
(function($){
	$.wflAccordion.Effects['slide'] = function(i){
		var self = this,
			defaults = self.config,
			panels = self.panels,
			pT = self.panels.eq(i),
			dt = defaults.duration;	
		if ( defaults.effect !== 'slide') {
			return false;
		}
		if(defaults.multiple) pT.is(":hidden") ? pT.slideDown(dt) : pT.slideUp(dt);
		else{			
			defaults.collapse ? panels.not(pT).hide() : panels.slideUp(dt);
			defaults.collapse ? (pT.is(":hidden") ? pT.slideDown(dt) : pT.slideUp(dt)) : pT.slideDown(dt);
		}	
	}
})(jQuery);

/****************************************************************
/** 
   switchable 
*/
/**
   整合了tabs slide accordion scrollable,可自定义动画效果,支持插件扩展,可以广泛应用于选项卡、幻灯片、折叠面板、滚动等web页面应用.
   * API:
   * this.config      =>  Object
   * this.container   =>  jQuery
   * this.triggers    =>  jQuery
   * this.panels      =>  jQuery
   * this.length      =>  Number
   * this.index       =>  Number
   * this.willTo()    =>  Number, Boolean
   * this.switchTo()  =>  Function
*/   	
;(function($) {
	$.switchable = $.switchable || {};
	
	$.switchable = {
		// 配置
		config : {
			triggers : true,//触发元素_Boolean, String, jQuery[ true : 根据triggersElWrap和triggersEl配置选项创建,默认为<div class=[triggerWrapCls]><a>1</a>……</div>]
			putTriggers : "insertBefore",//使用何种方法把自动生成的triggers插入到DOM树中, 位置相对于panels的容器[一般常用的还有 insertAfter appendTo prependTo]_String,
			triggerWrapCls : "ui-switchable-trigger",//自动生成的触发元素容器样式名称_String,
			triggersElWrap : "div",//自动生成的触发元素容器元素结构_String,
			triggersEl : "a",//自动生成的触发元素结构_String,
			triggerCurrentCls : "current",//当前触发元素样式名称_String
			panels : null,//容器元素_Selector	,jQuery		
			triggerType : "mouseenter",//触发方式_String	
			triggerRepeat : false,//重复触发
			initIndex : 0,//默认激活索引_Number[-1表示最后一个元素]	
			delay : 100,//触发延迟时间:毫秒_Number
			steps : 1,//每次切换的panel数量,可见区域的panel数量_Number				
			//动画效果
			effect : "none",//String_["none","fade","slide","scroll","accordion","ajax"]			
			easing : "swing",//String
			duration : 600,//每次切换所花的时间:毫秒_Number			
			loop : true,//是否循环执行
			//回调函数
			beforeSwitch : null,
			onSwitch : null,
			api : false
		},
		// 效果
		Effects: {
			'none': function(from, to) {
				var self = this,
					defaults = self.config;	
				self.panels
				  .slice(from * defaults.steps, (from + 1) * defaults.steps).hide()
				  .end()
				  .slice(to * defaults.steps, (to + 1) * defaults.steps).show();
			}
		},	
		// 插件
		Plugins: []
	};
	
	function switchable($container, defaults, selector){
		var self = this,
			$self = $(this),
			_beforeFn = 'beforeSwitch',
			_onFn = 'onSwitch';
		//执行回调函数
		if ( $.isFunction(defaults[_beforeFn]) ) {
			$self.bind(_beforeFn, defaults[_beforeFn]);
		}
		if( $.isFunction(defaults[_onFn]) ){
			$self.bind(_onFn, defaults[_onFn]);
		}
		//扩展配置
		$.extend( self, {
			//初始化自定义插件
			_initPlugins : function(){
				var plugins = $.switchable.Plugins,
					len = plugins.length,
					i = 0;
				for( ; i<len; i++){
					if(plugins[i].init){
						plugins[i].init(self);
					}
				}
			},			
			//初始化
			_init : function(){				
				self.container = $container;
				self.config = defaults;//配置动画效果
				//-------------------------------获取panels-------------------------------
				if(!!defaults.panels && (defaults.panels.jquery || typeof defaults.panels === 'string')){
					self.panels = $container.find(defaults.panels);
				}
				else{
					self.panels = $container.children();					 
				}
				
				self.length = Math.ceil( self.panels.length / defaults.steps);
				if( self.length < 1 )	return false;//无panels容器返回
				//当前索引 从0开始,若是负值,则指向最后一个元素
				self.index = defaults.initIndex === false ? undefined : ( defaults.initIndex + (defaults.initIndex < 0 ? self.length : 0));	
				if (defaults.initIndex !== false) {
					self.panels.slice(self.index * defaults.visible, (self.index + 1) * defaults.visible).show(); 
				}
				
				//-------------------------------获取triggers并绑定事件-------------------------------
				if(!!defaults.triggers){
					var trigger, i ,index;
					//获取triggers,先判断是否为jquery对象
					if(defaults.triggers.jquery){
						self.triggers = defaults.triggers.slice(0, self.length);
					}
					else{
						//非jquery对象,创建触发元素, 目前支持数字和项目符号[可以考虑增加自定义数组]
						var triggersIsString = $.type(defaults.triggers) === 'string',
               				arr = [],
							putTriggersBegin = "<" + defaults.triggersEl + ">",
							putTriggersEnd = "</" + defaults.triggersEl + ">",
							putTriggersWrap = "<" + defaults.triggersElWrap + "/>",
							href = "javascript:void(0)";
						if(defaults.triggersEl === 'a'){
							putTriggersBegin = "<" + defaults.triggersEl + " href='javascript:void(0);' target: '_self'>";
						}
						for(var i = 1; i <= self.length; i++){							
							arr.push(putTriggersBegin + (triggersIsString ? defaults.triggers : i) + putTriggersEnd);							
						}
						self.triggers = $(putTriggersWrap, {
							'class' : defaults.triggerWrapCls,
							'html' : arr.join('')//数组连接参数为空							
						})[defaults.putTriggers]($container).find(defaults.triggersEl);
					}
					//激活当前triggers
					if (defaults.initIndex !== false) {
						defaults.initIndex === -1 ? self.triggers.eq(self.triggers.length-1).addClass(defaults.triggerCurrentCls) : self.triggers.eq(self.index).addClass(defaults.triggerCurrentCls);
					}
					//绑定事件
					for(var i = 0; i < self.length; i++){						
						//当前trigger
						trigger = self.triggers.eq(i);						
						if(defaults.triggerType.indexOf('mouse')>=0){							
							trigger[defaults.triggerType]({index : i}, function(e){//定义追加数据index,同bind方法
								index = e.data.index;
								// 避免重复触发
								if(!defaults.triggerRepeat){
									if ( !self._triggerIsValid(index) )	return;
								}
								self._delayTimer = setTimeout(function(){
									self.switchTo(index);
								}, defaults.delay);	
							}).mouseleave(function() {
								self._cancelDelayTimer();
							});
						}
						else{
							trigger[defaults.triggerType]({index : i}, function(e){
								index = e.data.index;
								if(!defaults.triggerRepeat){
									if ( !self._triggerIsValid(index))	return;		
								}
								self._cancelDelayTimer();	
              					self.switchTo(index);								
							});
						}
					}
					//end bind
				}
			},
			//end init				
			//屏蔽重复
			_triggerIsValid : function(compare){
				return	self.index !== compare;
			},
			//清空触发延迟时间
			 _cancelDelayTimer : function() {
				if ( self._delayTimer ){
					clearTimeout(self._delayTimer);
					self._delayTimer = undefined;
				}
			},
			//处理triggers触发
			_switchTrigger : function(from, to) {
				self.triggers
				.eq(from).removeClass(defaults.triggerCurrentCls)
				.end()
				.eq(to).addClass(defaults.triggerCurrentCls);
			},
			//处理panels触发
			_switchPanels : function(from, to, direction) {//call用来代替另一个对象调用一个方法
				$.switchable.Effects[defaults.effect].call(self, from, to, direction);
			},
			
			willTo : function(isBackward) {
				if ( isBackward ) {
					return self.index > 0 ? self.index - 1 : (defaults.loop ? self.length - 1 : false);
				} else {
					return self.index < self.length - 1 ? self.index + 1 : (defaults.loop ? 0 : false);
				}
			},
			
			switchTo : function(to, direction) {
				// call beforeSwitch()
				var event = $.Event(_beforeFn);
				$self.trigger(event, [to]);
				// 如果 beforeSwitch() return false 则阻止本次切换
				if ( event.isDefaultPrevented() ) {
					return;
				}				
				// switch panels & triggers
				self._switchPanels(self.index, to, direction);
				if ( !!defaults.triggers ) {
					self._switchTrigger(self.index, to);
				}				
				// update index
				self.index = to;					
				// call onSwitch()
				event.type = _onFn;
				$self.trigger(event, [to]);				
				return self;
			}
		})
		//初始化并安装插件
		self._init();
		self._initPlugins();
	}	
	$.fn.switchable = function(defaults) {
		var defaults = $.extend({}, $.switchable.config, defaults),
			$self = $(this),
        	len = $self.length,
        	selector = $self.selector,
        	e = [],
        	i;		
		// 将 effect 格式化为小写
		defaults.effect = defaults.effect.toLowerCase();	
		for (var i = 0; i < len; i++ ) {
			e[i] = new switchable($self.eq(i), defaults, selector + '[' + i + ']');
		}	
		return defaults.api ? e[0] : $self;
	};
})(jQuery);

/**
* 淡入淡出效果
*/
(function($) {
	$.switchable.Effects['fade'] = function(from, to) {
    	var self = this,
        	defaults = self.config;	
		// fade effect only supports steps == 1.
		if ( defaults.effect !== 'fade' || defaults.steps !== 1 ) {
			return;
		}
		self.panels
			.slice(from * defaults.steps, (from + 1) * defaults.steps).fadeOut()
			.end()
			.slice(to * defaults.steps, (to + 1) * defaults.steps).fadeIn(defaults.duration);		
	};	
})(jQuery);

/**
* 滑动效果
*/
(function($) {	
	var scrollEffects = ['scrollleft', 'scrollright', 'scrollup', 'scrolldown'],
		_position = 'position',
        _absolute = 'absolute',
        _relative = 'relative';
	// 新增参数
	$.extend($.switchable.config, {		
		visible : 1,//panel可见数量
		endToend : false,//首尾相连
		groupSize : []// panel-group 的尺寸, panels 多列垂直滚动或者自动获取不正确时
	});
	for(var i = 0; i < 4; i++){
		$.switchable.Effects[scrollEffects[i]] = function(from, to, direction) {
			var self = this,
				panels = self.panels.parent(),
				defaults = self.config,		
				//thisVisible = self.panels.slice(to * defaults.steps, (to + 1) * defaults.steps),
				max = self.length - 1,	
				isBackward = direction === 'backward',					
				isCritical = defaults.endToend && (isBackward && from === 0 && to === max || direction === 'forward' && from === max && to === 0),			
				props= {};			
			
			//props = self.isHoriz ? { top : -thisVisible.position().top } : { left : -thisVisible.position().left };
			props[self.isHoriz ? 'left' : 'top'] = isCritical ? self._adjustPosition(isBackward) : -self.groupSize[self.isHoriz ? 0 : 1] * to;			
			
			// 开始动画
			if ( panels.is(":animated") ) {
				panels.stop(true);
			}
			panels.animate(props, defaults.duration, defaults.easing, function(){
				//循环播放设置
				if ( isCritical ) {
					self._resetPosition(isBackward);
				}
			});				
		};
	}
	//写入插件
	$.switchable.Plugins.push({
		name : 'scroll effect',				
		init : function(obj){
			var defaults = obj.config,
				steps = defaults.steps,
				panels = obj.panels,
				wrap = panels.parent(),		
				index = $.inArray(defaults.effect, scrollEffects),
				isHoriz = index === 0 || index === 1,
				w = panels.eq(0).outerWidth(true),
				h = panels.eq(0).outerHeight(true),
				x = isHoriz ? 0 : 1,
				max = obj.length - 1,
				prop = isHoriz ? 'left' : 'top',
				props = {};
			
			// 1. 获取 panel-group 尺寸
			obj.groupSize = [
				defaults.groupSize[0] || w * steps,
				defaults.groupSize[1] || h * steps
			];

			// 2. if end2end == true
			if(defaults.endToend){	
				var len = panels.length,
					totalSize = !isHoriz && defaults.groupSize[0] ?
                          // 多列垂直滚动
                          obj.groupSize[x] * obj.length :
                          // 单行水平或单列垂直滚动
                          (isHoriz ? w : h) * len,
					lastGroupLen = len - max * steps,
            		lastGroupSize = (isHoriz ? w : h) * lastGroupLen,
            		adjustSize = !isHoriz && defaults.groupSize[0] ? obj.groupSize[x] : lastGroupSize,
            		start;
				// 强制 loop == true
				defaults.loop = true;
				
				// clone panels from beginning to end
				if ( defaults.visible && defaults.visible < len && defaults.visible > lastGroupLen ) {
					panels.slice(0, defaults.visible)
						.clone(true)
						.appendTo(wrap)
						// 同步 click 事件
						.click(function(e){
							e.preventDefault();
							panels.eq( $(this).index() - len ).click();
					});
				}
				//
				$.extend(obj, {
					/**
					* 调整位置
					*/
					_adjustPosition: function(isBackward) {
						start = isBackward ? max : 0;						
						props[_position] = _relative;
						props[prop] = (isBackward ? -1 : 1) * totalSize;
						panels.slice(start * steps, (start + 1) * steps).css(props);						
						return isBackward ? adjustSize : -totalSize;
					},
					
					/**
					* 复原位置
					*/
					_resetPosition: function(isBackward) {
						start = isBackward ? max : 0;						
						props[_position] = '';
						props[prop] = '';
						panels.slice(start * steps, (start + 1) * steps).css(props);						
						props[_position] = undefined;
						props[prop] = isBackward ? -obj.groupSize[x] * max : 0;
						wrap.css(props);
					}
				});	
			}	
			
			// 3. 存储动画属性, 便于外部(如 autoplay)调用	
			obj.isHoriz = isHoriz;
			obj.isBackward = index === 1 || index === 3;
		}
	});
})(jQuery);

/**
* 自动播放
*/
(function($) {
	// 新增参数
	$.extend($.switchable.config, {		
		// 是否自动播放
		autoplay: false,
		// 自动播放间隔3秒
		interval: 3000,
		// 鼠标悬停
		pauseOnHover: true
	});//扩展并覆盖，改变原有结构
	
	$.switchable.Plugins.push({
		name : 'autoplay',		
		init : function(obj){
			//定义参数
			var defaults = obj.config,
				pausing = false,//当前播停状态
				timer1,
				timer2,
				to;
			//设置autoplay=true有效
	  		if(!defaults.autoplay || obj.length <=1){
				return;
			}
			//支持鼠标悬停,应用jquery:hover方法
			if(defaults.pauseOnHover){
				obj.panels.add(obj.triggers).hover(function(){//追加标签 add方法将triggers添加到panels对象中
					obj._pause();									  
				}, function(){
					if(!pausing){
						obj._play();
					}
				});
			}
			
			//自动播放
			function run(){
				to = obj.willTo(obj.isBackward);
				if(to===false){
					obj._canelTimers();
					return;
				}
				obj.switchTo(to, obj.isBackward ? 'backward' : 'forward');
			}
			
			function autoRun(){
				timer2 = setInterval(function(){
					run();			  
				}, defaults.interval);
			}
			
			//Add API
			$.extend(obj, {
				/**
				* 启动
				*/
				_play: function() {
					obj._cancelTimers();					
					// 让外部知道当前的状态
					obj.paused = false;					
					// 让首次(或者暂停后恢复)切换和后续的自动切换的间隔时间保持一致
					timer1 = setTimeout(function(){						
						autoRun();
					}, defaults.interval);
				},
				
				/**
				* 暂停
				*/
				_pause: function() {
					obj._cancelTimers();					
					obj.paused = true;
				},
				
				/**
				* 取消切换定时器
				*/
				_cancelTimers: function() {
					if ( timer1 ) {
						clearTimeout(timer1);
						timer1 = undefined;
					}					
					if ( timer2 ) {
						clearInterval(timer2);
						timer2 = undefined;
					}
				},
				 /**
				 * 对外api, 使外部可以在暂停后恢复切换
				 */
				 play : function(){
					 obj._play();
					 pausing = false;
					 return obj;
				 },
				 
				 pause : function(){
					 obj._pause();
					 pausing = true;
					 return obj;
				 }				 
			});			
			//初始化播放
			obj._play();
		}
	});
})(jQuery);

/**
* 手风琴效果
*/
(function($){
	$.extend($.switchable.config, {
		multiple : false,//允许展开多个
		accordionHoriz : false,//水平方向
		accordionProps : {}//样式属性配置
	});
	
	$.switchable.Effects["accordion"] = function(from, to){
		var self = this,
			defaults = self.config,
			panels = self.panels,
			$thisItem = panels.eq(to),
			$thisVisible = panels.eq(from),
			visible = $thisItem.is(":visible") ? true : false,
			props;		
		if(defaults.multiple){
			if(defaults.accordionHoriz){
				return;	
			}
			if(visible){
				$thisItem.slideUp(defaults.duration);
			}
			else{
				$thisItem.slideDown(defaults.duration);
			}
		}
		else{
			defaults.triggerRepeat = false;//强制取消重复触发
			if(defaults.accordionHoriz){//only supports defaults.multiple == false
				props = $.extend({width : $thisVisible.width()}, defaults.accordionProps);	
				$thisVisible.animate({ width : 0 }, defaults.duration, defaults.easing, function(){		
					$(this).hide();
				});				
				$thisItem.animate(props, defaults.duration, defaults.easing, function(){																				  
				}).show();	
			}
			else{
				panels.slideUp(defaults.duration).eq(to).slideDown(defaults.duration);
			}
		}
	}
	
	$.switchable.Plugins.push({
		name : 'effect accordion',
		init : function(obj){
			var defaults = obj.config;			
			// accordion effect only supports steps == 1.
			if(defaults.effect !== 'accordion' || defaults.steps !== 1){
				return;
			}
			//重写内部api
			$.extend(obj, {				
				_switchTrigger : function(from, to) {	
					var triggers = obj.triggers,
						cls = defaults.triggerCurrentCls;
					triggers.eq(to).toggleClass(cls);	
					if ( !defaults.multiple && from !== to ) {
						triggers.eq(from).removeClass(cls);
					}
				}				
			});			
		}
	});
})(jQuery);

/****************************************************************
/** 
   元素页面浮动 
			  
   this.config      =>  属性配置	  
*/
